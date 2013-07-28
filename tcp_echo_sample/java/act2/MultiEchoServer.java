// created_at : 2004-12-20
// $Id: MultiEchoServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
// http://www.techscore.com/tech/Java/Network/2.html
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

class EchoServer {
	public static void main(String args[]) {
		int port;
		ServerSocket serverSocket = null;
		try {
			port = Integer.parseInt(args[0]);
			serverSocket = new ServerSocket(port);
			System.out.println("EchoServer‚ª‹N“®‚µ‚Ü‚µ‚½(port="
				+ serverSocket.getLocalPort() + ")");
			
			while(true) {
				Socket socket = serverSocket.accept();
				new EchoThread(socket).start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(serverSocket != null) {
					serverSocket.close();
				}
			} catch(IOException e) {}
		}
	}
}

class EchoThread extends Thread {
	private Socket socket;
	private InetAddress addrRemote = null;
	private InetAddress addrLocal = null;
	
	public EchoThread(Socket socket) {	
		this.socket = socket;
		addrRemote = socket.getInetAddress();
		addrLocal = socket.getLocalAddress();
		System.out.println("Ú‘±‚³‚ê‚Ü‚µ‚½ ");
		System.out.print("Remote(" + addrRemote.getHostAddress() + ") -> ");
		System.out.print("Local(" + addrLocal.getHostAddress() + ")\n");
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String line;
			while((line = in.readLine()) != null) {
				System.out.println(addrRemote.getHostAddress() + " -> óM: " + line);
				out.println(line);
				System.out.println(addrRemote.getHostAddress() + " <- ‘—M: " + line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch(IOException e) {}
			System.out.println("Ø’f‚³‚ê‚Ü‚µ‚½(" + addrRemote.getHostAddress() + ")");
		}
	}
}

