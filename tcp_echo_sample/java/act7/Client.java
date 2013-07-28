import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @version $Id: Client.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 */
public class Client
{
	static String REMOTE_HOST = "";
	
	static int REMOTE_PORT = 0;

	static int LOCAL_PORT = 0;

	static Charset CHARSET = null;

	static int RECORD_SIZE = 0;
	
	static long INTERVAL = 0;
	
	public static void main(String[] args) throws Exception
	{
		if (args.length != 7) {
			System.out
					.println("usage: java Client <remote_host> <remote_port> <local_port> <charset> <record_size> <file> <interval in millis>");
			System.out.println("(if <local_port> = 0, then system default is used.)");
			Logger.listupAvailableCharsets();
			System.exit(1);
		}

		Client.REMOTE_HOST = args[0];

		Client.REMOTE_PORT = Integer.parseInt(args[1]);
		
		Client.LOCAL_PORT = Integer.parseInt(args[2]);

		try {
			Client.CHARSET = Charset.forName(args[3]);
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

		Client.RECORD_SIZE = Integer.parseInt(args[4]);
		
		Client.INTERVAL = Integer.parseInt(args[6]);

		File file = new File(args[5]);
		BufferedReader br = null;
		String line = null;
		List<String> datas = new ArrayList<String>();

		int line_all = 0; // �S�s��
		int line_valid = 0; // �R�����g�A��s�ȊO�̗L���s��
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));

			while ((line = br.readLine()) != null) {
				line_all++;

				// "#"�Ŏn�܂邩�Atrim����������0�̍s�̓X�L�b�v
				if(line.startsWith("#")) {
					continue;
				}
				if(line.trim().length() == 0) {
					continue;
				}

				if (line.length() > Client.RECORD_SIZE) {
					line = line.substring(0, Client.RECORD_SIZE);
				}
				datas.add(String.format("%5s", line));
				line_valid++;
			}
			Logger.log("�f�[�^�t�@�C��[" + args[5] + "], �S�s��[" + line_all + "]/�L���s��[" + line_valid + "] ���[�h����");
		} catch(Exception e) {
			Logger.log("�f�[�^�t�@�C��[" + args[5] + "]�̃��[�h���ɃG���[���������܂����B");
			e.printStackTrace();
		} finally {
			if(br != null) {
				try { 
					br.close();
				} catch(Exception ignored) {
				}
			}
		}

		CharsetEncoder encoder = Client.CHARSET.newEncoder();
		CharsetDecoder decoder = Client.CHARSET.newDecoder();

		Socket client = new Socket();
		client.setReuseAddress(true);
		if (Client.LOCAL_PORT > 0) {
			client.bind(new InetSocketAddress(Client.LOCAL_PORT));
		}
		// �^�C���A�E�g�͗p���Ȃ��B
		//(�R���\�[���c�[���Ƃ���������A�^�C���A�E�g�͖ڎ��Œ����Ɍ��o�\)
		client.connect(new InetSocketAddress(Client.REMOTE_HOST, Client.REMOTE_PORT));

		BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
		BufferedInputStream bis = new BufferedInputStream(client.getInputStream());

		Iterator<String> it = datas.iterator();
		int cnt = 1;
		while (it.hasNext()) {
			String data = it.next();
			String progress = "[" + cnt + "/" + line_valid + "],";
			ByteBuffer write_bb = encoder.encode(CharBuffer.wrap(data));
			write_bb.rewind();
			Logger.log(progress + "SEND:DECODED=[" + data + "]", client);
			Logger.log(progress + "SEND:RAW=[" + Logger.dumpRawHex(write_bb) + "]", client);
			bos.write(write_bb.array(), 0, write_bb.limit());
			bos.flush();
			
			ByteBuffer read_bb = ByteBuffer.allocate(write_bb.capacity());
			int readbytes = bis.read(read_bb.array(), 0, read_bb.capacity());
			read_bb.rewind();
			read_bb.limit(readbytes);
			Logger.log(progress + "RECV:RAW=[" + Logger.dumpRawHex(read_bb) + "]", client);
			read_bb.rewind();
			CharBuffer cb = decoder.decode(read_bb);
			Logger.log(progress + "RECV:DECODED=[" + cb.toString() + "]", client);
			Logger.log(progress + "...completed, sleep [" + Client.INTERVAL + "] milli seconds...", client);
			
			Thread.sleep(Client.INTERVAL);
			cnt++;
		}
		client.close();
	}


}
