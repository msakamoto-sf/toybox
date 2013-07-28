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
			Logger.log("READ�^�C���A�E�g�ɂ��N���C�A���g�\�P�b�g�`���l����ؒf���܂��B");
			try {
				client.close();
			} catch (IOException ignored) {
			}
		}
	}

	public void run()
	{
		try {
			Logger.log("�N���C�A���g�X���b�h���J�n���܂��B", this.client_sc.socket());

			Selector selector = Selector.open();

			this.client_sc.configureBlocking(false);
			Logger.log("�N���C�A���g�\�P�b�g�`���l����NON-BLOCKING�ɐݒ肵�܂����B", this.client_sc
					.socket());

			SelectionKey clientkey = this.client_sc.register(selector,
					SelectionKey.OP_READ);
			Logger.log("�N���C�A���g�\�P�b�g�`���l����Selector�ɓo�^���܂����B", this.client_sc.socket());

			clientkey.attach(new Integer(0));

			for (;;) {
				if (selector.select(this.timeout_in_millis) == 0) {
					Logger.log("�N���C�A���g�\�P�b�g�`���l���ɂ�READ�^�C���A�E�g���������܂����B", this.client_sc.socket());
					Logger.log("READ�^�C���A�E�g�ɂ��N���C�A���g�\�P�b�g�`���l����ؒf���܂��B", this.client_sc.socket());
					try {
						this.client_sc.socket().close();
						this.client_sc.close();
					} catch (IOException ignored) {
					}
					selector.close();
					Logger.log("�N���C�A���g�\�P�b�g�`���l���̃Z���N�^��close���܂����B");
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
					Logger.log("�N���C�A���g�\�P�b�g�`���l���̏������J�n���܂��B", client.socket());
					if (!processClientSocketChannel(key, client)) {
						selector.close();
						Logger.log("�N���C�A���g�\�P�b�g��close����܂����B�Z���N�^��close���܂����B");
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
			Logger.log("�N���C�A���g�\�P�b�g�`���l�����ǂݍ��݂ł��܂���B�X�L�b�v���܂��B", client.socket());
			return true;
		}

		int bytesread = client.read(this.buffer);
		Logger.log("�N���C�A���g�\�P�b�g�`���l�����[" + bytesread + "]�o�C�gread����܂����B", client.socket());
		if (bytesread == -1) {
			key.cancel();
			Logger.log("�N���C�A���g�\�P�b�g�`���l����ؒf���܂��B", client.socket());
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
		int start = 1; // �J�n�C���f�b�N�X (���̒l���܂�)
		int end = 3; // �I���C���f�b�N�X (���̒l���܂܂Ȃ�) 
	    String str = "000"; // �ȑO�̓��e��u������V����������
	    sb.replace(start, end, str);
		
		return sb.toString();
	}
}
