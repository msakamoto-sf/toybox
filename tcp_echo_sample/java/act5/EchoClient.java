/*
 * 作成日: 2004/12/21
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * 非常に簡単なテキスト送受信TCPクライアント（例示）<br />
 *<br />
 * 使い方：<code>java -cp . EchoClient host port</code><br />
 * <br />
 * echo サーバーとかにつなげて動作確認してみてください。<br />
 * ちなみにTECHSCOREのを殆ど○写しです。<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version $Id: EchoClient.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- ２．ソケット</a>
 */
public class EchoClient {

	/**
	 * エントリポイント
	 * 
	 * @param args コマンドライン引数配列
	 * @return void
	 */
	public static void main(String args[]) {
		Socket socket = null;
		/*
		 * 引数の数が足りない時はusageを表示する。
		 */
		if(args.length < 2) {
			usage();
			return;
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		try {
			socket = new Socket(host, port);
			System.out.println("接続しました " + host + ":" + port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));
			String input;
			// 空行が入力されない間
			while((input = keyIn.readLine()).length() > 0) {
				out.println(input);
				String line = in.readLine();
				if(line != null) {
					System.out.println(line);
				} else {
					break;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch(IOException e) {}
			System.out.println("切断されました ");
		}
	}
	
	/**
	 * 使い方の表示
	 */
	public static void usage() {
		String msg = "args: host port\n" +
			"host : Server Host Name(Address)\n" +
			"port : Server Process Port\n";
		System.out.print(msg);
	}
}
