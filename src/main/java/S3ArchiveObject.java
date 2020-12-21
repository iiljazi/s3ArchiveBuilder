import java.util.Date;
import java.util.concurrent.Future;

public class S3ArchiveObject {
	private String key;
	private String size;
	private String sqsReceiveHandle;
	private String localDirectory;
	private String localFileName;
	private Date date;
	private Future<?> future;	

	S3ArchiveObject(String key, String size, String sqsReceiveHandle, String localDirectory, String localFileName, Date date) {
		this.setKey(key);
		this.setSize(size);
		this.setSQSReceiveHandle(sqsReceiveHandle);
		this.setLocalDirectory(localDirectory); 
		this.setLocalFileName(localFileName);
		this.setDate(date);
	}

	// Get Methods for S3ArchiveObject
	public String getKey() {
		return this.key;
	}
	public String getSize() {
		return this.size;
	}	
	public String getSQSReceiveHandle() {
		return this.sqsReceiveHandle;
	}	
	public String getLocalDirectory() {
		return this.localDirectory;
	}	
	public String getLocalFileName() {
		return this.localFileName;
	}		
	public Date getDate() {
		return this.date;
	}
	public Future<?> getS3Future() {
		return this.future;
	}	
	
	// Set Methods for S3ArchiveObject
	public void setKey(String key) {
		this.key = key;
	}
	public void setSize(String size) {
		this.size = size;
	}	
	public void setSQSReceiveHandle(String sqsReceiveHandle) {
		this.sqsReceiveHandle = sqsReceiveHandle;
	}	
	public void setLocalDirectory(String localDirectory) {
		this.localDirectory = localDirectory;
	}	
	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}		
	public void setDate(Date date) {
		this.date = date;
	}
	public void setS3Future(Future<?> future) {
		this.future = future;
	}		
}
