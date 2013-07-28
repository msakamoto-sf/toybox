/*
 * �쐬��: 2004/12/21
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Java�X���b�h��Echo�T�[�o�[�i�Ꭶ�j<br />
 *<br />
 * �g�����F<code>java -cp . EchoServer port</code><br />
 * <br />
 * telnet�N���C�A���g�ƍ��킹�ē���m�F���Ă݂Ă��������B<br />
 * �ڑ��҂��󂯃X���b�h�ɂ��A�ڑ���������X���b�h���������A�Ή������܂��B<br />
 * ���Ȃ݂�TECHSCORE�̂�w�ǁ��ʂ��ł��B<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version $Id: MultiEchoServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- �Q�D�\�P�b�g</a>
 */
class EchoServer {

	/**
	 * �G���g���|�C���g
	 * 
	 * @param args �R�}���h���C�������z��
	 * @return void
	 */
	public static void main(String args[]) {
		int port;
		ServerSocket serverSocket = null;
		/*
		 * �����̐�������Ȃ�����usage��\������B
		 */
		if(args.length < 2) {
			usage();
			return;
		}

		try {
			port = Integer.parseInt(args[0]);
			serverSocket = new ServerSocket(port);
			System.out.println("EchoServer���N�����܂���(port="
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
	 * �g�����̕\��
	 */
	public static void usage() {
		String msg = "args: port\n" +
			"port : Server Port\n";
		System.out.print(msg);
	}
}

/**
 * EchoServer�ɂ��N�������X���b�h�N���X�B<br />
 * ���ۂ�Echo�T�[�o�[�Ƃ��Ă̏����i�R�F�����j�����s���܂��B<br />
 * ���Ȃ݂�TECHSCORE�̂�w�ǁ��ʂ��ł��B<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version 2004.12.21.01
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- �Q�D�\�P�b�g</a>
 */
class EchoThread extends Thread {
	/**
	 * �N���C�A���g�Ƃ̃\�P�b�g
	 */
	private Socket socket;
	/**
	 * �N���C�A���g���̃A�h���X���
	 */
	private InetAddress addrRemote = null;
	/**
	 * ���[�J���i�T�[�o�[�j���̃A�h���X���
	 */
	private InetAddress addrLocal = null;

	/**
	 * TECHSCORE�̂Ƃ͏����Ⴂ�܂��B�Ƃ��������̂܂܂��ƃG���[�E�E�E�BTECHSCORE�ł�
	 * socket.getRemoteSocketAddress()�����̂܂ܕ�����ƘA�����Ďg���Ă܂����A���ۂ�
	 * �G���[�B�����ƃA�h���X���N���X�Ƃ��Ĉ���Ȃ��Ă�N.G.�ł����E�E�E�B
	 * 
	 * @param socket �T�[�o�[�\�P�b�g��accept()�̕Ԃ����\�P�b�g�B
	 */	
	public EchoThread(Socket socket) {	
		this.socket = socket;
		// TECHSCORE�ł�
		// System.out.println("�ڑ����܂���" + socket.getRemoteSocketAddress());
		// �݂������Ă邯�ǁA���ۂ̓G���[�ɂȂ�B
		addrRemote = socket.getInetAddress();
		addrLocal = socket.getLocalAddress();
		System.out.println("�ڑ�����܂��� ");
		System.out.print("Remote(" + addrRemote.getHostAddress() + ") -> ");
		System.out.print("Local(" + addrLocal.getHostAddress() + ")\n");
	}

	/**
	 * �X���b�h�G���g���|�C���g<br />
	 * �R�F�������s���X���b�h����
	 */
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String line;
			while((line = in.readLine()) != null) {
				System.out.println(addrRemote.getHostAddress() + " -> ��M: " + line);
				out.println(line);
				System.out.println(addrRemote.getHostAddress() + " <- ���M: " + line);
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null) {
					socket.close();
				}
			} catch(IOException e) {}
			System.out.println("�ؒf����܂���(" + addrRemote.getHostAddress() + ")");
		}
	}
}

