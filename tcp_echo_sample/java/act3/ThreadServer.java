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

      // サーバーソケットの生成
//      ServerSocket serverSocket = new ServerSocket(5555);
      ServerSocket serverSocket = new ServerSocket(ServerPortNo1);

      // メインループ
      while(true) {
        try {
//          System.out.println("クライアントからの接続をポート5555で待ちます");
//          System.out.println("クライアントからの接続をポート10007で待ちます");
          // クライアントからの接続を待ちます
          Socket socket = serverSocket.accept();
System.out.println("-------- " + socket.getInetAddress() + " から接続を受付ました" + "-------- ");
System.out.println("Clientポート: " + socket.getPort() + " Serverポート: " + socket.getLocalPort() );
System.out.println("---------------- ");

          // 処理をスレッドに任せます
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
    // スレッド開始
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

      // 出力ストリームを取得
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      // 入力ストリームを取得
      BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));

      // 読み込んだ行をそのまま出力ストリームに書き出す
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        System.out.println("---------------- ");
        System.out.println("Clientポート: " + socket.getPort() + " Serverポート: " + socket.getLocalPort() );

        System.out.println("受信: " + inputLine);

        Calendar date = Calendar.getInstance();
        yy = date.get(Calendar.YEAR);
        mm = date.get(Calendar.MONTH);  // 0 = 1 月
        dd = date.get(Calendar.DATE);
        hh = date.get(Calendar.HOUR_OF_DAY);   // 12 時間
        mn = date.get(Calendar.MINUTE);
        sc = date.get(Calendar.SECOND);
        ms = date.get(Calendar.MILLISECOND);
/**
        out.println("送信: " + inputLine + "---->" + socket.getLocalPort() + "---->" + hh + ":" + mn + ":" + sc + ":" + ms);
        System.out.println("送信: " + inputLine + "---->" + socket.getLocalPort() + "---->" + hh + ":" + mn + ":" + sc + ":" + ms);
**/

        System.out.println("---------------- ");
      }


    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (socket != null) {
      System.out.println("処理が終了したので接続を切ります 1");
          // 入出力ストリームを閉じる
//          in.close();
//          out.close();
          // ソケットを閉じる
          socket.close();
        }
      } catch (IOException e) {}
      try {
        if (socket != null) {
      System.out.println("処理が終了したので接続を切ります 2");
          // 入出力ストリームを閉じる
//          in.close();
//          out.close();
          // ソケットを閉じる
          socket.close();
        }
      } catch (IOException e) {}
    }
  }


/*

      System.out.println("処理が終了したので接続を切ります");

      // 入出力ストリームを閉じる
      in.close();
      out.close();
      // ソケットを閉じる
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