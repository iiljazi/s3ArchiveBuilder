import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import com.amazonaws.util.IOUtils;

public class S3TarGzBuilder {
	String ArchiveName = null;
	String ArchiveDirectory = null;
	TarArchiveOutputStream TarArchiveOutPutStream = null;
	
	S3TarGzBuilder(SQSContext ctx) {
		this.ArchiveName = ctx.getLocalArchiveName();
		this.ArchiveDirectory = ctx.getLocalDirectory();
		this.TarArchiveOutPutStream = createTarGzArchive();
	}
	
	private TarArchiveOutputStream createTarGzArchive() {
		FileOutputStream fOut;
		BufferedOutputStream buffOut;
		GzipCompressorOutputStream gzOut;
		TarArchiveOutputStream tOut;
		try {
			fOut = new FileOutputStream(this.ArchiveDirectory + this.ArchiveName);
			buffOut = new BufferedOutputStream(fOut);
			try {
				gzOut = new GzipCompressorOutputStream(buffOut);
				tOut = new TarArchiveOutputStream(gzOut);
				return tOut;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void addInputStreamToArchive(InputStream s3Object, String localFileName, String objSize) {
		//Convert Input Stream to Output Stream
	    TarArchiveEntry tarEntry = new TarArchiveEntry(localFileName);
	    tarEntry.setSize(Long.valueOf(objSize));
	    try {
			this.TarArchiveOutPutStream.putArchiveEntry(tarEntry);
			IOUtils.copy(s3Object, this.TarArchiveOutPutStream);
			s3Object.close();
			this.TarArchiveOutPutStream.closeArchiveEntry();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeTarGzArchive() {
		try {
			this.TarArchiveOutPutStream.flush();
			this.TarArchiveOutPutStream.finish();
			this.TarArchiveOutPutStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
