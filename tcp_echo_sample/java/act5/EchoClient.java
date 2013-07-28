/*
 * �쐬��: 2004/12/21
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * ���ɊȒP�ȃe�L�X�g����MTCP�N���C�A���g�i�Ꭶ�j<br />
 *<br />
 * �g�����F<code>java -cp . EchoClient host port</code><br />
 * <br />
 * echo �T�[�o�[�Ƃ��ɂȂ��ē���m�F���Ă݂Ă��������B<br />
 * ���Ȃ݂�TECHSCORE�̂�w�ǁ��ʂ��ł��B<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version $Id: EchoClient.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- �Q�D�\�P�b�g</a>
 */
public class EchoClient {

	/**
	 * �G���g���|�C���g
	 * 
	 * @param args �R�}���h���C�������z��
	 * @return void
	 */
	public static void main(String args[]) {
		Socket socket = null;
		/*
		 * �����̐�������Ȃ�����usage��\������B
		 */
		if(args.length < 2) {
			usage();
			return;
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		try {
			socket = new Socket(host, port);
			System.out.println("�ڑ����܂��� " + host + ":" + port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));
			String input;
			// ��s�����͂���Ȃ���
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
			System.out.println("�ؒf����܂��� ");
		}
	}
	
	/**
	 * �g�����̕\��
	 */
	public static void usage() {
		String msg = "args: host port\n" +
			"host : Server Host Name(Address)\n" +
			"port : Server Process Port\n";
		System.out.print(msg);
	}
}
