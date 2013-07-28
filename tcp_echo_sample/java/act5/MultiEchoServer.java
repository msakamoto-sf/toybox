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
 * Javaスレッド版Echoサーバー（例示）<br />
 *<br />
 * 使い方：<code>java -cp . EchoServer port</code><br />
 * <br />
 * telnetクライアントと合わせて動作確認してみてください。<br />
 * 接続待ち受けスレッドにより、接続がきたらスレッドをおこし、対応させます。<br />
 * ちなみにTECHSCOREのを殆ど○写しです。<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version $Id: MultiEchoServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- ２．ソケット</a>
 */
class EchoServer {

	/**
	 * エントリポイント
	 * 
	 * @param args コマンドライン引数配列
	 * @return void
	 */
	public static void main(String args[]) {
		int port;
		ServerSocket serverSocket = null;
		/*
		 * 引数の数が足りない時はusageを表示する。
		 */
		if(args.length < 2) {
			usage();
			return;
		}

		try {
			port = Integer.parseInt(args[0]);
			serverSocket = new ServerSocket(port);
			System.out.println("EchoServerが起動しました(port="
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
	/**
	 * 使い方の表示
	 */
	public static void usage() {
		String msg = "args: port\n" +
			"port : Server Port\n";
		System.out.print(msg);
	}
}

/**
 * EchoServerにより起動されるスレッドクラス。<br />
 * 実際のEchoサーバーとしての処理（山彦処理）を実行します。<br />
 * ちなみにTECHSCOREのを殆ど○写しです。<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version 2004.12.21.01
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- ２．ソケット</a>
 */
class EchoThread extends Thread {
	/**
	 * クライアントとのソケット
	 */
	private Socket socket;
	/**
	 * クライアント側のアドレス情報
	 */
	private InetAddress addrRemote = null;
	/**
	 * ローカル（サーバー）側のアドレス情報
	 */
	private InetAddress addrLocal = null;

	/**
	 * TECHSCOREのとは少し違います。というかそのままだとエラー・・・。TECHSCOREでは
	 * socket.getRemoteSocketAddress()をそのまま文字列と連結して使ってますが、実際は
	 * エラー。ちゃんとアドレス情報クラスとして扱わなくてはN.G.でした・・・。
	 * 
	 * @param socket サーバーソケットのaccept()の返したソケット。
	 */	
	public EchoThread(Socket socket) {	
		this.socket = socket;
		// TECHSCOREでは
		// System.out.println("接続しました" + socket.getRemoteSocketAddress());
		// みたくしてるけど、実際はエラーになる。
		addrRemote = socket.getInetAddress();
		addrLocal = socket.getLocalAddress();
		System.out.println("接続されました ");
		System.out.print("Remote(" + addrRemote.getHostAddress() + ") -> ");
		System.out.print("Local(" + addrLocal.getHostAddress() + ")\n");
	}

	/**
	 * スレッドエントリポイント<br />
	 * 山彦処理を行うスレッド中枢
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String line;
			while((line = in.readLine()) != null) {
				System.out.println(addrRemote.getHostAddress() + " -> 受信: " + line);
				out.println(line);
				System.out.println(addrRemote.getHostAddress() + " <- 送信: " + line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch(IOException e) {}
			System.out.println("切断されました(" + addrRemote.getHostAddress() + ")");
		}
	}
}

