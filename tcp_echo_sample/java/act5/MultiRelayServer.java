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
 * Java�X���b�h��Echo�u�����[�v�T�[�o�[�i�Ꭶ�j<br />
 *<br />
 * �g�����F<code>java -cp . RelayServer port1 host port2</code><br />
 * <br />
 * Echo�n�V���O�����C���e�L�X�gTCP�ėp�̃����[���s���܂��B<code>port1</code>
 * �Ƀ����[�T�[�o�[���҂��󂯂�|�[�g�ԍ����B<code>host</code>�ɁA�����[��́A
 * �܂�{�̃T�[�o�[���ғ����Ă���z�X�g�����B<code>port2</code>�ɁA�{�̃T�[�o�[��
 * �҂��󂯂Ă���|�[�g�ԍ����w�����܂��B<br />
 * <br />
 * <code>EchoClient + EchoServer / 2</code>�݂����ȁB<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version $Id: MultiRelayServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- �Q�D�\�P�b�g</a>
 */
class RelayServer {

	/**
	 * �G���g���|�C���g
	 * 
	 * @param args �R�}���h���C�������z��
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
			System.out.println("RelayServer���N�����܂���(port="
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
	 * �g�����̕\��
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
 * RelayServer�ɂ��N�������X���b�h�N���X�B<br />
 * ���ۂ̃����[���������s���܂��B<br />
 * 
 * @author sakamoto@nsd-ltd.co.jp
 * @version 2004.12.21.01
 * @see <a href="http://www.techscore.com/tech/Java/Network/2.html">TECHSCORE -Java- �Q�D�\�P�b�g</a>
 */
class RelayThread extends Thread {
	/**
	 * �����[��ɂȂ��邽�߂̃\�P�b�g
	 */
	private Socket distSocket;
	/**
	 * �����[���i�N���C�A���g�j�Ƃ̃\�P�b�g
	 */
	private Socket srcSocket;
	/**
	 * �����[���i�N���C�A���g�j�̃A�h���X���
	 */
	private InetAddress addrRemote = null;
	/**
	 * �����[�T�[�o�[���g�i���[�J���j�̃A�h���X���
	 */
	private InetAddress addrLocal = null;
	/**
	 * �����[��̃z�X�g��
	 */
	private String relayHost;
	/**
	 * �����[��̃|�[�g
	 */
	private int relayPort;

	/**
	 * 
	 * @param socket �N���C�A���g�Ƃ̐ڑ��p�\�P�b�g�i�T�[�o�[�\�P�b�g��accept()���Ԃ��\�P�b�g�j
	 * @param target �����[��z�X�g��
	 * @param port �����[��|�[�g�ԍ�
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
	 * �X���b�h�G���g���|�C���g<br />
	 * �����[�������s�����������B
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
				System.out.println("��M: " + req);
				distOut.println(req);
				System.out.print("Local(" + addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + ") -> ");
				System.out.print("Relay(" + relayHost + ":" + relayPort + ") ");
				System.out.println("���M: " + req);

				res = distIn.readLine();
				if(res != null) {
					System.out.print("Relay(" + relayHost + ":" + relayPort + ") -> ");
					System.out.print("Local(" + addrLocal.getHostAddress() + ":" + distSocket.getLocalPort() + ") ");
					System.out.println("��M: " + res);
					srcOut.println(res);
					System.out.print("Local(" + addrLocal.getHostAddress() + ":" + srcSocket.getLocalPort() + ") -> ");
					System.out.print("Remote(" + addrRemote.getHostAddress() + ":" + srcSocket.getPort() + ") ");
					System.out.println("���M: " + res);
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

