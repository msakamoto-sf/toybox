import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * @version $Id: Worker.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 */
public class Worker extends Thread
{
	CharsetEncoder encoder = null;

	CharsetDecoder decoder = null;

	ByteBuffer buffer = null;

	SocketChannel client_sc = null;

	long timeout_in_millis = 0;

	public Worker(SocketChannel client_sc_a, Charset charset,
			long timeout_in_millis_a, int echosize)
	{
		this.encoder = charset.newEncoder();
		this.decoder = charset.newDecoder();
		this.client_sc = client_sc_a;
		this.timeout_in_millis = timeout_in_millis_a;
		this.buffer = ByteBuffer.allocate(echosize);

		this.start();
	}

	public void cancelByReadTimeout(Set keys)
	{
		for (Iterator i = keys.iterator(); i.hasNext();) {
			SelectionKey key = (SelectionKey) i.next();
			i.remove();
			SocketChannel client = (SocketChannel) key.channel();
			key.cancel();
			Logger.log("READタイムアウトによりクライアントソケットチャネルを切断します。");
			try {
				client.close();
			} catch (IOException ignored) {
			}
		}
	}

	public void run()
	{
		try {
			Logger.log("クライアントスレッドを開始します。", this.client_sc.socket());

			Selector selector = Selector.open();

			this.client_sc.configureBlocking(false);
			Logger.log("クライアントソケットチャネルをNON-BLOCKINGに設定しました。", this.client_sc
					.socket());

			SelectionKey clientkey = this.client_sc.register(selector,
					SelectionKey.OP_READ);
			Logger.log("クライアントソケットチャネルをSelectorに登録しました。", this.client_sc.socket());

			clientkey.attach(new Integer(0));

			for (;;) {
				if (selector.select(this.timeout_in_millis) == 0) {
					Logger.log("クライアントソケットチャネルにてREADタイムアウトが発生しました。", this.client_sc.socket());
					Logger.log("READタイムアウトによりクライアントソケットチャネルを切断します。", this.client_sc.socket());
					try {
						this.client_sc.socket().close();
						this.client_sc.close();
					} catch (IOException ignored) {
					}
					selector.close();
					Logger.log("クライアントソケットチャネルのセレクタをcloseしました。");
					return;
				}

				Set keys = selector.selectedKeys();
				for (Iterator i = keys.iterator(); i.hasNext();) {
					SelectionKey key = (SelectionKey) i.next();
					i.remove();

					if (key != clientkey) {
						continue;
					}
					SocketChannel client = (SocketChannel) key.channel();
					Logger.log("クライアントソケットチャネルの処理を開始します。", client.socket());
					if (!processClientSocketChannel(key, client)) {
						selector.close();
						Logger.log("クライアントソケットがcloseされました。セレクタをcloseしました。");
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean processClientSocketChannel(SelectionKey key, SocketChannel client)
			throws Exception
	{
		if (!key.isReadable()) {
			Logger.log("クライアントソケットチャネルが読み込みできません。スキップします。", client.socket());
			return true;
		}

		int bytesread = client.read(this.buffer);
		Logger.log("クライアントソケットチャネルより[" + bytesread + "]バイトreadされました。", client.socket());
		if (bytesread == -1) {
			key.cancel();
			Logger.log("クライアントソケットチャネルを切断します。", client.socket());
			client.close();
			return false;
		}
		
		if (this.buffer.position() == this.buffer.limit()) {
			this.buffer.rewind();
			Logger.log("RECV:RAW=[" + Logger.dumpRawHex(this.buffer) + "]", client.socket());
			this.buffer.rewind();

			String rcv = this.decoder.decode(this.buffer).toString();
			this.buffer.clear();
			Logger.log("RECV:DECODED=[" + rcv + "]", client.socket());

			String res = this.fabricateResponseData(rcv);
			Logger.log("SEND:DECODED=[" + res + "]", client.socket());
			ByteBuffer write_bb = encoder.encode(CharBuffer.wrap(res));
			Logger.log("SEND:RAW=[" + Logger.dumpRawHex(write_bb) + "]", client.socket());
			write_bb.rewind();
			client.write(write_bb);
			int num = ((Integer) key.attachment()).intValue();
			key.attach(new Integer(num + 1));
		}

		return true;
	}

	String fabricateResponseData(String source)
	{
		StringBuffer sb = new StringBuffer(source);
		
		// http://java.sun.com/j2se/1.5.0/ja/docs/ja/api/java/lang/StringBuffer.html
		int start = 1; // 開始インデックス (この値を含む)
		int end = 3; // 終了インデックス (この値を含まない) 
	    String str = "000"; // 以前の内容を置換する新しい文字列
	    sb.replace(start, end, str);
		
		return sb.toString();
	}
}
