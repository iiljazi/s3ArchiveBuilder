public class ArchiveConfig {
	private String type;
	private String baseDirectory;
	private String awsCredentials;
	private String sourceBucket;
	private String targetBucket;
	private String archiveFilePrefix;
	private String archiveFileFolder;
	private String region;
	private String queue;
	private String groupID;
	private String filter;
	private String command;
	private String s3ThreadNum;
	
	//S3ArchiveConfiguration() {}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBaseDirectory() {
		return this.baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public String getAwsCredentials() {
		return this.awsCredentials;
	}

	public void setAwsCredentials(String awsCredentials) {
		this.awsCredentials = awsCredentials;
	}

	public String getSourceBucket() {
		return this.sourceBucket;
	}

	public void setSourceBucket(String sourceBucket) {
		this.sourceBucket = sourceBucket;
	}

	public String getTargetBucket() {
		return this.targetBucket;
	}

	public void setTargetBucket(String targetBucket) {
		this.targetBucket = targetBucket;
	}

	public String getArchiveFilePrefix() {
		return this.archiveFilePrefix;
	}

	public void setArchiveFilePrefix(String archiveFilePrefix) {
		this.archiveFilePrefix = archiveFilePrefix;
	}

	public String getArchiveFileFolder() {
		return this.archiveFileFolder;
	}

	public void setArchiveFileFolder(String archiveFileFolder) {
		this.archiveFileFolder = archiveFileFolder;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getQueue() {
		return this.queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getGroupID() {
		return this.groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getFilter() {
		return this.filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getCommand() {
		return this.command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getS3ThreadNum() {
		return this.s3ThreadNum;
	}

	public void setS3ThreadNum(String s3ThreadNum) {
		this.s3ThreadNum = s3ThreadNum;
	}	  
}
