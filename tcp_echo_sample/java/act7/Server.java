import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Set;

/**
 * @version $Id: Server.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 */
public class Server
{
	static Charset CHARSET = null;

	static int LOCAL_PORT = 0;

	static long READ_TIMEOUT = 0;

	static int ECHO_SIZE = 0;

	public static void main(String[] args) throws Exception
	{
		if (args.length != 4) {
			System.out
					.println("usage: java Server <port> <charset> <read_timeout> <record_size>");
			Logger.listupAvailableCharsets();
			System.exit(1);
		}

		Server.LOCAL_PORT = Integer.parseInt(args[0]);

		try {
			Server.CHARSET = Charset.forName(args[1]);
		} catch (IllegalCharsetNameException e) {
			System.err.println("�w�肳�ꂽ�����Z�b�g�����s���ł��B");
			e.printStackTrace();
			Logger.listupAvailableCharsets();
			System.exit(1);
		} catch (UnsupportedCharsetException e) {
			System.err.println("�w�肳�ꂽ�����Z�b�g������ Java ���z�}�V���C���X�^���X��ł͗��p�ł��܂���B");
			e.printStackTrace();
			Logger.listupAvailableCharsets();
			System.exit(1);
		}

		Server.READ_TIMEOUT = Integer.parseInt(args[2]);

		Server.ECHO_SIZE = Integer.parseInt(args[3]);

		// �T�[�o�̋N��
		new Server();
	}

	public Server() throws Exception
	{

		Selector selector = Selector.open();

		ServerSocketChannel server_sc = null;
		server_sc = ServerSocketChannel.open();
		Logger.log("�T�[�o�[���\�P�b�g�`���l����open���܂����B");

		server_sc.socket().setReuseAddress(true);
		server_sc.socket().bind(new InetSocketAddress(Server.LOCAL_PORT));
		Logger.log("�T�[�o�[���\�P�b�g�`���l�����|�[�g�ԍ�[" + Server.LOCAL_PORT + "]��bind���܂����B",
				server_sc.socket());

		server_sc.configureBlocking(false);
		Logger.log("�T�[�o�[���\�P�b�g�`���l����NON-BLOCKING�ɐݒ肵�܂����B", server_sc.socket());

		SelectionKey serverkey = server_sc.register(selector,
				SelectionKey.OP_ACCEPT);
		Logger.log("�T�[�o�[���\�P�b�g�`���l����Selector�ɓo�^���܂����B", server_sc.socket());

		for (;;) {
			selector.select();
			Set keys = selector.selectedKeys();

			for (Iterator i = keys.iterator(); i.hasNext();) {
				SelectionKey key = (SelectionKey) i.next();
				i.remove();

				if (key != serverkey) {
					continue;
				}
				if (!key.isAcceptable()) {
					continue;
				}
				Logger
						.log("�T�[�o�[���\�P�b�g�`���l����accept()���������܂����B", server_sc
								.socket());

				// �T�[�o�[���̃\�P�b�g�`���l���ɕω��L�肩��ACCEPT
				// ���N���C�A���g�p�\�P�b�g�`���l�����擾
				SocketChannel client = server_sc.accept();
				Logger.log("�N���C�A���g�\�P�b�g�`���l�����擾���܂����B", client.socket());

				new Worker(client, Server.CHARSET, Server.READ_TIMEOUT,
						Server.ECHO_SIZE);
			}
		}
	}
}
