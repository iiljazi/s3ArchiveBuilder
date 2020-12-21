import java.io.InputStream;
import java.util.concurrent.Future;

public class S3InputStreamTuple {
	Future<InputStream> future = null;
	String fileName = null;
	String objSize = null;
	S3InputStreamTuple(Future<InputStream> future, String fileName, String objSize) {
		this.future = future;
		this.fileName = fileName;
		this.objSize = objSize;
	}
}
