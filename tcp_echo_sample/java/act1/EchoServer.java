// created_at : 2004-12-20
// $Id: EchoServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
// http://www.techscore.com/tech/Java/Network/2.html
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class EchoServer {
	public static final int ECHO_PORT = 10007;

	public static void main(String args[]) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		InetAddress addrRemote = null;
		InetAddress addrLocal = null;
		try {
			serverSocket = new ServerSocket(ECHO_PORT);
			System.out.println("EchoServerÇ™ãNìÆÇµÇ‹ÇµÇΩ(port="
				+ serverSocket.getLocalPort() + ")");
			System.out.println("hoge");
			socket = serverSocket.accept();
			addrRemote = socket.getInetAddress();
			addrLocal = socket.getLocalAddress();
			System.out.println("ê⁄ë±Ç≥ÇÍÇ‹ÇµÇΩ ");
			System.out.print("Remote(" + addrRemote.getHostAddress() + ") -> ");
			System.out.print("Local(" + addrLocal.getHostAddress() + ")\n");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String line;
			while((line = in.readLine()) != null) {
				System.out.println("éÛêM: " + line);
				out.println(line);
				System.out.println("ëóêM: " + line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch(IOException e) {}
			try {
				if(serverSocket != null) {
					serverSocket.close();
				}
			} catch(IOException e) {}
		}
	}
}

