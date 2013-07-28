import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @version $Id: Logger.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 */
public class Logger
{
	static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	public static void log(String s, ServerSocket sock)
	{
		int local_port = sock.getLocalPort();
		String local_host = sock.getInetAddress().getHostAddress();
		log(s, "", 0, local_host, local_port);
	}

	public static void log(String s, Socket sock)
	{
		int local_port = sock.getLocalPort();
		int remote_port = sock.getPort();
		String remote_host = sock.getInetAddress().getHostAddress();
		String local_host = sock.getLocalAddress().getHostAddress();
		log(s, remote_host, remote_port, local_host, local_port);
	}

	public static void log(String s)
	{
		log(s, "", 0, "", 0);
	}

	public synchronized static void log(String s, String remote_host,
			int remote_port, String local_host, int local_port)
	{
		long now = System.currentTimeMillis();
		System.out.println(String.format("[%s:%d]->[%s:%d],<%d>,%s(%d),%s",
				remote_host, remote_port, local_host, local_port, Thread
						.currentThread().getId(), sdf.format(new Date(now)),
				now, s));
	}

	public synchronized static String dumpRawHex(ByteBuffer bb)
	{
		byte b = 0;
		StringBuffer sb = new StringBuffer(bb.limit() * 5);
		while (bb.hasRemaining()) {
			b = bb.get();
			sb.append(" 0x");
			sb.append(String.format("%X", 0xff & b));
		}
		return sb.toString();
	}

	public static void listupAvailableCharsets()
	{
		System.out.println("利用可能な文字セット名(charset):");
		Map<String, Charset> charmaps = Charset.availableCharsets();
		Set<String> charnames = charmaps.keySet();
		Iterator<String> i = charnames.iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}
}
