// created_at : 2004-12-21
// $Id: EchoThreader.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * EchoServer(������RelayServer)�ւ̃N���C�A���g�v���Z�X���X���b�h�ŕ�����C��
 * �����グ�A�����ɑ���M���邽�߂̃}���`�X���b�hTCP�R�l�N�V�����̃T���v���B
 * �Ƃ͂����A�����܂ł������̃T���v���ŁB
 * GUI��DB/FileIO�Ƒg�ݍ��킹���ꍇ�A�O���Ƃ̔r�������GUI�̐���Asynchironized,
 * volatile����ь������͕K�{�B
 */
public class EchoThreader implements Runnable {

	int id;
	String host;
	int port;

	int cycleCount = 10;
	int cycleInterval = 100;

	public EchoThreader(int id, String host, int port, int count, int interval) {
		this.id = id;
		this.host = host;
		this.port = port;
		this.cycleCount = count;
		this.cycleInterval = interval;
	}

	public static void usage() {
		String msg = "args: number_of_thread, echo_server_host, echo_server_port\n"
					+"      cycle_count, cycle_interval\n";
		System.out.println(msg);
	}

	public static void main(String args[]) {
		if(args.length < 5) {
			usage();
			return;
		}
		int num = Integer.parseInt(args[0]);
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		int count = Integer.parseInt(args[3]);
		int interval = Integer.parseInt(args[4]);
		int i;
		for(i = 0; i < num; i++) {
			new Thread(new EchoThreader(i, host, port, count, interval)).start();
		
		}
	}
	
	public void run() {
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			System.out.println("(" + id + ") Connected.");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			int i;
			for(i = 0; i < cycleCount; i++) {
				out.println(i);
				String line = in.readLine();
				if(line != null) {
					System.out.print("(" + id + "," + line + ")");
				} else {
					break;
				}
				try {
					Thread.sleep(cycleInterval);
				} catch(InterruptedException e) { e.printStackTrace(); }
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
}

