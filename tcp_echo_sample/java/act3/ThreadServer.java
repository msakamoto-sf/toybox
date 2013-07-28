// created_at : 2004-12-24
// $Id: ThreadServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.io.*;
import java.net.*;
import java.util.*;

public class ThreadServer {
  public static void main(String argv[]) {
    try {

      int ServerPortNo1;

      ServerPortNo1 = Integer.parseInt(argv[0]);

      // �T�[�o�[�\�P�b�g�̐���
//      ServerSocket serverSocket = new ServerSocket(5555);
      ServerSocket serverSocket = new ServerSocket(ServerPortNo1);

      // ���C�����[�v
      while(true) {
        try {
//          System.out.println("�N���C�A���g����̐ڑ����|�[�g5555�ő҂��܂�");
//          System.out.println("�N���C�A���g����̐ڑ����|�[�g10007�ő҂��܂�");
          // �N���C�A���g����̐ڑ���҂��܂�
          Socket socket = serverSocket.accept();
System.out.println("-------- " + socket.getInetAddress() + " ����ڑ�����t�܂���" + "-------- ");
System.out.println("Client�|�[�g: " + socket.getPort() + " Server�|�[�g: " + socket.getLocalPort() );
System.out.println("---------------- ");

          // �������X���b�h�ɔC���܂�
          new Connect(socket);

        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class Connect extends Thread {
  private Socket socket = null;

  public Connect(Socket socket) {
    this.socket = socket;
    // �X���b�h�J�n
    this.start();
  }

  public void run() {
    try {


      int yy;
      int mm;
      int dd;
      int hh;
      int mn;
      int sc;
      int ms;

      // �o�̓X�g���[�����擾
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      // ���̓X�g���[�����擾
      BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));

      // �ǂݍ��񂾍s�����̂܂܏o�̓X�g���[���ɏ����o��
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        System.out.println("---------------- ");
        System.out.println("Client�|�[�g: " + socket.getPort() + " Server�|�[�g: " + socket.getLocalPort() );

        System.out.println("��M: " + inputLine);

        Calendar date = Calendar.getInstance();
        yy = date.get(Calendar.YEAR);
        mm = date.get(Calendar.MONTH);  // 0 = 1 ��
        dd = date.get(Calendar.DATE);
        hh = date.get(Calendar.HOUR_OF_DAY);   // 12 ����
        mn = date.get(Calendar.MINUTE);
        sc = date.get(Calendar.SECOND);
        ms = date.get(Calendar.MILLISECOND);
/**
        out.println("���M: " + inputLine + "---->" + socket.getLocalPort() + "---->" + hh + ":" + mn + ":" + sc + ":" + ms);
        System.out.println("���M: " + inputLine + "---->" + socket.getLocalPort() + "---->" + hh + ":" + mn + ":" + sc + ":" + ms);
**/

        System.out.println("---------------- ");
      }


    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (socket != null) {
      System.out.println("�������I�������̂Őڑ���؂�܂� 1");
          // ���o�̓X�g���[�������
//          in.close();
//          out.close();
          // �\�P�b�g�����
          socket.close();
        }
      } catch (IOException e) {}
      try {
        if (socket != null) {
      System.out.println("�������I�������̂Őڑ���؂�܂� 2");
          // ���o�̓X�g���[�������
//          in.close();
//          out.close();
          // �\�P�b�g�����
          socket.close();
        }
      } catch (IOException e) {}
    }
  }


/*

      System.out.println("�������I�������̂Őڑ���؂�܂�");

      // ���o�̓X�g���[�������
      in.close();
      out.close();
      // �\�P�b�g�����
      socket.close();
    } catch(Exception e) {
      try {
        socket.close();
      }catch(Exception ex) {
        e.printStackTrace();
      }
    }
  }
*/
}