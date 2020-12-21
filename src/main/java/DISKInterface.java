import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

public class DISKInterface {
	private String baseDir;
	private String archivePrefix;
	
	DISKInterface(String baseDir, String archivePrefix) {
		this.baseDir = baseDir;
		this.archivePrefix = archivePrefix;
	}
	
	private String getBaseDir() {
		return this.baseDir;
	}
	
	private String getArchivePrefix() {
		return this.archivePrefix;
	}
	
	// Called by SQS Consumers to cleanup Temporary Archive Directory
	public static void cleanArchiveContextDirectory(SQSContext archiveCTX) {
		String localDirectory = archiveCTX.getLocalDirectory();
		File file = new File(localDirectory);
		try {
			FileUtils.deleteDirectory(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Called by SQS Consumers when receiving a new context from SQS Queue
	public String createLocalDirectory() {
		UUID uuid = UUID.randomUUID();
		String localDirectory = this.getBaseDir() + uuid.toString() + "/";
		File theDir = new File(localDirectory);
		if (!theDir.exists()){
		    theDir.mkdirs();
		}
		return localDirectory;
	}
	
	// Called by SQS Consumers to build the Archive Name
	public String generateArchiveName(SQSContext ctx) {
		String prefix = ctx.getPrefix();
		String year = ctx.getYear();
		String device = prefix.split("/")[0];
		String archiveName = this.getArchivePrefix() + "_" + device + "_" + year + ".tar.gz";
		return archiveName;
	}
}
