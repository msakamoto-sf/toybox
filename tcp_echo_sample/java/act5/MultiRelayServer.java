/*
 * 作成日: 2004/12/21
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Javaスレッド版Echo「リレー」サーバー（例示）<br />
 *<br />
 * 使い方：<code>java -cp . RelayServer port1 host port2</code><br />
 * <br />
 * Echo系シングルラインテキストTCP汎用のリレーを行います。<code>port1</code>
 * にリレーサーバーが待ち受けるポート番号を。<code>host</code>に、リレー先の、
 * つまり本体サーバーが稼働しているホスト名を。<code>port2</code>に、本体サーバーの
 * 待ち受けているポート番号を指示します。<br />
 * <br />
 * <code>EchoClient + EchoServer / 2</code>みたいな。<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version $Id: MultiRelayServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- ２．ソケット</a>
 */
class RelayServer {

	/**
	 * エントリポイント
	 * 
	 * @param args コマンドライン引数配列
	 * @return void
	 */
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
	
	/**
	 * 使い方の表示
	 */	
	public static void usage() {
		String msg = "args: port1 target port2\n" +
			"port1  : Relay Server Accept Port\n" +
			"target : Relay Server Distination Host\n" +
			"port2  : Relay Server Distination Port\n";
		System.out.print(msg);
	}
}

/**
 * RelayServerにより起動されるスレッドクラス。<br />
 * 実際のリレー処理を実行します。<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version 2004.12.21.01
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- ２．ソケット</a>
 */
class RelayThread extends Thread {
	/**
	 * リレー先につなげるためのソケット
	 */
	private Socket distSocket;
	/**
	 * リレー元（クライアント）とのソケット
	 */
	private Socket srcSocket;
	/**
	 * リレー元（クライアント）のアドレス情報
	 */
	private InetAddress addrRemote = null;
	/**
	 * リレーサーバー自身（ローカル）のアドレス情報
	 */
	private InetAddress addrLocal = null;
	/**
	 * リレー先のホスト名
	 */
	private String relayHost;
	/**
	 * リレー先のポート
	 */
	private int relayPort;

	/**
	 * 
	 * @param socket クライアントとの接続用ソケット（サーバーソケットのaccept()が返すソケット）
	 * @param target リレー先ホスト名
	 * @param port リレー先ポート番号
	 */	
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

	/**
	 * スレッドエントリポイント<br />
	 * リレー処理を行う中枢部分。
	 */
	public void run() {
		try {
			distSocket = new Socket(relayHost, relayPort);
			System.out.print("Relay To ... ");
			System.out.print(addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + " -> ");
			System.out.print(relayHost + ":" + relayPort + "\n");
			
			BufferedReader srcIn = new BufferedReader(new InputStreamReader(srcSocket.getInputStream()));
			PrintWriter srcOut = new PrintWriter(srcSocket.getOutputStream(), true);
			BufferedReader distIn = new BufferedReader(new InputStreamReader(distSocket.getInputStream()));
			PrintWriter distOut = new PrintWriter(distSocket.getOutputStream(), true);
			String req;
			String res;
			while((req = srcIn.readLine()) != null) {
				System.out.print("Remote(" + addrRemote.getHostAddress() + ":" + srcSocket.getPort() + ") -> ");
				System.out.print("Local(" + addrLocal.getHostAddress() + ":" + srcSocket.getLocalPort() + ") ");
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
					System.out.print("Local(" + addrLocal.getHostAddress() + ":" + srcSocket.getLocalPort() + ") -> ");
					System.out.print("Remote(" + addrRemote.getHostAddress() + ":" + srcSocket.getPort() + ") ");
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

