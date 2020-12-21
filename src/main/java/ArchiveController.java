import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Logger;
import com.google.gson.Gson;

public class ArchiveController {	
	S3Interface s3 = null;
	SQSInterface sqs = null;
	DISKInterface disk = null;
	SQSProducer producer = null;
	SQSConsumer consumer = null;
	Logger logger = null; 
	private static int processors = Runtime.getRuntime().availableProcessors();
	
	ArchiveController(ArchiveConfig configCTX) {
		String type = configCTX.getType();
		String baseDir = configCTX.getBaseDirectory();
		File logDir = new File(baseDir + "/Controller/");
		if (!logDir.exists())
		    logDir.mkdirs();
		
		// Initialize SQS Controller Log
		String logName = "sqs-" + type;
		this.logger = new ArchiveLogger(logName, logDir.getAbsolutePath()).getLogger();
		this.logger.info("SQS Controller invoked on Base Directory: " + baseDir);
		
		if(type.compareTo("producer") == 0) {
			this.logger.info("SQS Controller Initializing SQS Producer ... ");
			initSQSProducer(configCTX);
		}
		else if(type.compareTo("consumer") == 0) {
			this.logger.info("SQS Controller Initializing SQS Consumers ... ");
			initSQSConsumer(configCTX);
		}
		else
			this.logger.info("SQS Controller found an unsupported type = " + type + " Exiting ...");
	}
	
	private void initSQSProducer(ArchiveConfig configCTX) {
	    // Create Directory Structure for Producer:
	    // baseDir/Producer/Logs
	    // baseDir/Producer/Checkpoint
		this.logger.info("SQS Controller Creating SQS Producer Directory Structure Over Base Directory ... ");
	    String baseDir = configCTX.getBaseDirectory();
	    File logPath = new File(baseDir + "/Producer/");
	    if(!logPath.exists())
	    	logPath.mkdirs();
	    
	    this.logger.info("SQS Controller Creating Classes required by SQS Producer ... ");
	    // Create Required Classes and Launch Producer
		this.s3 = new S3Interface(configCTX.getSourceBucket(), 
				configCTX.getRegion(),configCTX.getArchiveFileFolder(), Integer.valueOf(configCTX.getS3ThreadNum()));
	    this.sqs = new SQSInterface(configCTX.getQueue(), configCTX.getRegion(), 
	    		configCTX.getGroupID());
	    this.producer = new SQSProducer(configCTX.getSourceBucket(), this.s3, this.sqs, 
	    		this.logger,logPath.getAbsolutePath(), configCTX.getCommand());
	    
	    this.logger.info("SQS Controller Starting SQS Producer Thread  ... ");
	    this.producer.produce(configCTX.getFilter());
	}
	
	private void initSQSConsumer(ArchiveConfig configCTX) {
	    // Create Directory Structure for Consumers
	    // baseDir/Consumer/Archives
		this.logger.info("SQS Controller Creating Classes required by SQS Consumers ... ");
	    String baseDir = configCTX.getBaseDirectory();
	    File archiveDir = new File(baseDir + "/Consumer/Archives/");
	    if(!archiveDir.exists())
	    	archiveDir.mkdirs();
	    
	    // Create Required Classes and Launch Consumers
	    this.logger.info("SQS Controller Creating SQS Consumers Directory Structure Over Base Directory ... ");
		this.s3 = new S3Interface(configCTX.getSourceBucket(), configCTX.getRegion(), configCTX.getArchiveFileFolder(),
				Integer.valueOf(configCTX.getS3ThreadNum()));
		this.sqs = new SQSInterface(configCTX.getQueue(), configCTX.getRegion(), 
				configCTX.getGroupID());
		this.disk = new DISKInterface(archiveDir.getAbsolutePath() + "/", configCTX.getArchiveFilePrefix());
		this.consumer = new SQSConsumer(this.s3, this.sqs, this.disk, processors, this.logger);
		
		this.logger.info("SQS Controller Starting SQS Consumer Threads  ... ");
		this.consumer.consume();
		
		// Wait for Consumers to Shutdown and Terminate
		if(configCTX.getType().compareTo("consumer")==0) {
			// Ensure consumer executor is closed
			while(!this.consumer.executor.isTerminated()){
				this.logger.info("SQS Consumers Actively Working ...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.logger.info("Successfully Shutdown SQS Consumer Threads ...");
		}
	}

	public static void main(String[] args) {
		// Get Path To Configuration
		ArchiveConfig configCTX = null; // = new S3ArchiveConfiguration();
		Gson gson = new Gson();
		ArchiveController controller = null;
		String configurationFile = System.getProperty("configurationFile");
		File configFile = null;
		if(configurationFile != null)
			configFile = new File(configurationFile);
		else
			System.out.println("The path variable received is null");
		
		// Read Configuration File and Convert S3ArchiveConfiguration Object
		FileReader reader;
		try {
			reader = new FileReader(configFile);
			configCTX = gson.fromJson(reader, ArchiveConfig.class);
			if(configCTX != null)
				 controller = new ArchiveController(configCTX);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem with reading Controller Configuration ... Exiting");
			e.printStackTrace();
		}
		
		// Ensure s3 executor is closed
		controller.s3.executor.shutdownNow();
		while(!controller.s3.executor.isTerminated()) {
			controller.logger.info("Waiting for S3 Executors to ShutDown ...");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		controller.logger.info("Successfully Shutdown S3 Interface Executors ...");
		controller.logger.info("SQS Controller All " + configCTX.getType() + " threads have exited ...");
		controller.logger.info("SQS Controller Exiting ...");
	}
}
