// created_at : 2004-12-21
// $Id: MultiRelayServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
// http://www.techscore.com/tech/Java/Network/2.html
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

class RelayServer {
	public static void main(String args[]) {
		int port1;
		int port2;
		String target;
		ServerSocket serverSocket = null;
		if(args.length < 3) {
			usage();
			return;
		}
		try {
			port1 = Integer.parseInt(args[0]);
			target = args[1];
			port2 = Integer.parseInt(args[2]);
			serverSocket = new ServerSocket(port1);
			System.out.println("RelayServerが起動しました(port="
				+ serverSocket.getLocalPort() + ")");
			
			while(true) {
				Socket socket = serverSocket.accept();
				new RelayThread(socket, target, port2).start();
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
	
	public static void usage() {
		String msg = "args: port1 target port2\n" +
			"port1  : Relay Server Accept Port\n" +
			"target : Relay Server Distination Host\n" +
			"port2  : Relay Server Distination Port\n";
		System.out.print(msg);
	}
}

class RelayThread extends Thread {
	private Socket distSocket;
	private Socket srcSocket;
	private InetAddress addrRemote = null;
	private InetAddress addrLocal = null;
	private String relayHost;
	private int relayPort;
	
	public RelayThread(Socket socket, String target, int port) {	
		srcSocket = socket;
		relayHost = target;
		relayPort = port;
		addrRemote = srcSocket.getInetAddress();
		addrLocal = srcSocket.getLocalAddress();
		
		System.out.print("Relay From ... ");
		System.out.print(addrRemote.getHostAddress() + ":" + srcSocket.getPort() + " -> ");
		System.out.print(addrLocal.getHostAddress() + ":" + srcSocket.getLocalPort() + "\n");
	}
	public void run() {
		try {
			distSocket = new Socket(relayHost, relayPort);
			System.out.print("Relay To ... ");
			System.out.print(addrLocal.getHostAddress() + ":" + srcSocket.getLocalPort() + " -> ");
			System.out.print(relayHost + ":" + relayPort + "\n");
			
			BufferedReader srcIn = new BufferedReader(new InputStreamReader(srcSocket.getInputStream()));
			PrintWriter srcOut = new PrintWriter(srcSocket.getOutputStream(), true);
			BufferedReader distIn = new BufferedReader(new InputStreamReader(distSocket.getInputStream()));
			PrintWriter distOut = new PrintWriter(distSocket.getOutputStream(), true);
			String req;
			String res;
			while((req = srcIn.readLine()) != null) {
				System.out.print("Remote(" + addrRemote.getHostAddress() + ":" + distSocket.getPort() + ") -> ");
				System.out.print("Local(" + addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + ") ");
				System.out.println("受信: " + req);
				distOut.println(req);
				System.out.print("Local(" + addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + ") -> ");
				System.out.print("Relay(" + relayHost + ":" + relayPort + ") ");
				System.out.println("送信: " + req);

				res = distIn.readLine();
				if(res != null) {
					System.out.print("Relay(" + relayHost + ":" + relayPort + ") -> ");
					System.out.print("Local(" + addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + ") ");
					System.out.println("受信: " + res);
					srcOut.println(res);
					System.out.print("Local(" + addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + ") -> ");
					System.out.print("Remote(" + addrRemote.getHostAddress() + ":" + distSocket.getPort() + ") ");
					System.out.println("送信: " + res);
				} else {
					break;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(distSocket != null) {
					distSocket.close();
				}
			} catch(IOException e) {}
			System.out.println("Disconnect from Relay Host (" + relayHost + ")");
			try {
				if(srcSocket != null) {
					srcSocket.close();
				}
			} catch(IOException e) {}
			System.out.println("Disconnect from Remote Host (" + addrRemote.getHostAddress() + ")");
		}
	}
}

