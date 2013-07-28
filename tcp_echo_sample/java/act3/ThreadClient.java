// created_at : 2004-12-24
// $Id: ThreadClient.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.net.*;
import java.io.*;
import java.util.*;

public class ThreadClient {
  public static void main(String[] args){
    try{

      int PortNo1;
      int PortNo2;
      int PortNo3;

      int ServerPortNo1;

      PortNo1 = Integer.parseInt(args[1]);
      ServerPortNo1 = Integer.parseInt(args[2]);
//      PortNo2 = Integer.parseInt(args[1]);
//      PortNo3 = Integer.parseInt(args[1]);
//      PortNo2 = Integer.parseInt(args[1]) + 10;
//      PortNo3 = Integer.parseInt(args[1]) + 20;

      // �\�P�b�g�𐶐�
      InetAddress MyAdr = InetAddress.getByName("localhost");
      Socket socket1 = new Socket(args[0], ServerPortNo1, MyAdr, PortNo1);
//      Socket socket2 = new Socket(args[0], 10007, MyAdr, PortNo2);
//      Socket socket3 = new Socket(args[0], 10007, MyAdr, PortNo3);

      // �������X���b�h�ɔC���܂�
      new ClientConnect(socket1,1);
//      new ClientConnect(socket2,2);
//      new ClientConnect(socket3,3);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}



class ClientConnect extends Thread {
  private Socket socket = null;
  private int ThdId= 0;

  public ClientConnect(Socket socket, int ThdId) {
    this.socket = socket;
    this.ThdId = ThdId;
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
                              new InputStreamReader(
                              socket.getInputStream()));

      // �L�[����
      BufferedReader keyIn = new BufferedReader(new InputStreamReader(System.in));

      socket.setSoTimeout(10000);
      System.out.println( "�\�P�b�g�^�C���A�E�g�l(ms): " + socket.getSoTimeout());

      String input;
      int cnt;

      for(cnt=0; cnt<10;cnt++ ) {

        try {
          // �X���b�h���~���܂�
          this.sleep(10000);
System.out.println("Catch 01");
        } catch (InterruptedException e) {
        }

        Calendar date = Calendar.getInstance();
        yy = date.get(Calendar.YEAR);
        mm = date.get(Calendar.MONTH);  // 0 = 1 ��
        dd = date.get(Calendar.DATE);
        hh = date.get(Calendar.HOUR_OF_DAY);   // 12 ����
        mn = date.get(Calendar.MINUTE);
        sc = date.get(Calendar.SECOND);
        ms = date.get(Calendar.MILLISECOND);

        System.out.println("---------------- ");
        System.out.println("�X���b�h�F " + ThdId + " Client�|�[�g: " + socket.getLocalPort() + " Server�|�[�g: " + hh + ":" + mn + ":" + sc + ":" + ms);


        out.println("�X���b�h�F " + ThdId + " ���M: " + socket.getLocalPort() + "---->" + hh + ":" + mn + ":" + sc + ":" + ms);
        System.out.println("�X���b�h�F " + ThdId + " ���M: " + socket.getLocalPort() + "---->" + hh + ":" + mn + ":" + sc + ":" + ms);

        String line = null;
        try {
          line = in.readLine();
        } catch (InterruptedIOException e) {
System.out.println("Catch 02");
          out.println("�X���b�h�F " + ThdId + " ���M: " + socket.getLocalPort() + "---->Timeout!! " + hh + ":" + mn + ":" + sc + ":" + ms);
          System.out.println("�X���b�h�F " + ThdId + " ���M: " + socket.getLocalPort() + "---->Timeout!! " + hh + ":" + mn + ":" + sc + ":" + ms);
        }
        if (line != null) {
           System.out.println("��M: " + line);
           System.out.println("---------------- ");
        } else {
          break;
        }
      }

    } catch (IOException e) {
System.out.println("Catch 03");
      e.printStackTrace();
    }
  }
}