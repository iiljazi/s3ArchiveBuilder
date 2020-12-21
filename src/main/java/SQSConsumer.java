import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

import com.amazonaws.services.sqs.model.Message;
import com.google.gson.Gson;

public class SQSConsumer {
	S3Interface s3 = null;
	SQSInterface sqs = null;
	DISKInterface disk = null;
	int threadNum; 
	ThreadPoolExecutor executor = null;
	Logger logger = null;
	
	SQSConsumer(S3Interface s3, SQSInterface sqs, DISKInterface disk, int threadNum, Logger logger) {
		this.s3 = s3;
		this.sqs = sqs;
		this.disk = disk;
		this.threadNum = threadNum;
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
		this.logger = logger;
	}
	
	// Runnable Task For Consumer Threads
	private class s3ArchiveRunnable implements Runnable {
		@Override
		public void run() {
			// Thread was Not passed a CheckPoint Context
			while(!sqs.isSqsQueueEmpty()) {
				SQSContext ctx = getMessageFromSQS();
	    		if(ctx != null)
	    			processS3ArchiveContextTar(ctx);
			}
		}
	}
	
	public void consume() {
		for(int i=0; i<this.threadNum; i++) {
			this.logger.info("Starting New SQS Consumer Thread ... : " + i);
			this.submitS3ArchiveProcessing();
		}
		// Call ShutDown to Consumer ThreadPool, after dispatching all threads
		this.executor.shutdown();
	}
	
	private void processS3ArchiveContextTar(SQSContext ctx) {
		List<S3InputStreamTuple> inStream3Tuple = new ArrayList<S3InputStreamTuple>();
		S3TarGzBuilder tar = new S3TarGzBuilder(ctx);
		this.logger.info("Received New Context from SQS ... Processing");
		this.logger.info("Reading All S3 Objects In SQS Context and Writting To Tar Archive: " + ctx.getLocalArchiveName());
		for(S3ArchiveObject obj : ctx.getS3ArchiveObjects()){
			obj.setLocalDirectory(ctx.getLocalDirectory());
			inStream3Tuple.add(this.s3.submitObjectIntoTar(obj));
		}
			
		// Waiting for all S3 Objects to be read and archived
		this.logger.info("Waiting For S3 Objects To Be Written into Archive: " + ctx.getLocalArchiveName());
		while(!inStream3Tuple.isEmpty()) {
			Iterator<S3InputStreamTuple> futureItr = inStream3Tuple.iterator();
			while(futureItr.hasNext()) {
				S3InputStreamTuple entry = futureItr.next();
				if(entry.future.isDone()) {
					try {
						InputStream s3In = entry.future.get();
						tar.addInputStreamToArchive(s3In, entry.fileName, entry.objSize);
						futureItr.remove();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
				
			// Wait 1 second before re-checking Future Status 
			try {
				Thread.sleep(1000);
				this.logger.info("Waiting For S3 Objects To Be Written into Archive: " + ctx.getLocalArchiveName());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		// Close Tar Archives OutputStream
		this.logger.info("Finished Building Tar Archive: " + ctx.getLocalArchiveName());
		tar.closeTarGzArchive();
		
		// Uploading Archive File to S3
		this.logger.info("Uploading Tar Archive: " + ctx.getLocalArchiveName() + " -> To S3 Bucket");
		S3Interface.uploadS3Archive(ctx);
		
		// Cleaning Up Local Directory Structure
		this.logger.info("Cleaning Up Directory: " + ctx.getLocalDirectory());
		DISKInterface.cleanArchiveContextDirectory(ctx);	
		
		// Delete Message From SQS Queue
		String deleteRequestHandle = ctx.getDeleteRequestHandle();
		sqs.deleteSQSMessage(deleteRequestHandle);
	}
	
	// For FIFO Queue this Function Declaration Must Be:
	// private synchronized S3ArchiveContext getMessageFromSQS() {
	private SQSContext getMessageFromSQS() {
		SQSContext ctx = null;
		List<Message> messages = this.sqs.readSQSMessages();
		ListIterator<Message> itr = messages.listIterator();
    	if(itr.hasNext()) {
    		this.logger.info("Got Work Context From SQS Queue ...");
    		// Get SQS Message for Local Processing    	    		
    		Message message = itr.next();
    		String jsonCTX = message.getBody();
    		
    		// Convert to ArchiveConsumerContext using GSON
    		Gson gson = new Gson();
    		ctx = gson.fromJson(jsonCTX, SQSContext.class);
    		
    		// Set Directory Name and Archive Name
    		String localDirectory = this.disk.createLocalDirectory();
    		String localArchiveName = this.disk.generateArchiveName(ctx);
    		ctx.setLocalDirectory(localDirectory);
    		ctx.setLocalArchiveName(localArchiveName);
    		
    		// Set Delete Request Handle ID
    		ctx.setDeleteRequestHandle(message.getReceiptHandle());
    	}
    	return ctx;
	}

	public void submitS3ArchiveProcessing() {
		s3ArchiveRunnable job = new s3ArchiveRunnable();
		this.executor.submit(job);
	}
}