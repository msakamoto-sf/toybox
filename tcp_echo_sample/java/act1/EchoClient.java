// created_at : 2004-12-20
// $Id: EchoClient.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class EchoClient {

	public static void main(String args[]) {
		Socket socket = null;
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		try {
			socket = new Socket(host, port);
			System.out.println("ê⁄ë±ÇµÇ‹ÇµÇΩ " + host + ":" + port);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));
			String input;
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
			System.out.println("êÿífÇ≥ÇÍÇ‹ÇµÇΩ ");
		}
	}
}
