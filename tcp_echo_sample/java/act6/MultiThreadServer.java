// created_at : 2008-09-08
// $Id: MultiThreadServer.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class MultiThreadServer {

    public static void main(String args[]) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            int myport = Integer.parseInt(args[0]);
            // サーバーソケットの生成
            ServerSocket serverSocket = new ServerSocket(myport);

            // メインループ
            while(true) {
                try {
///                    System.out.println("クライアントからの接続をポート"+myport+"で待ちます");
                    // クライアントからの接続を待ちます
                    Socket socket = serverSocket.accept();
                    System.out.println(sdf.format(new java.util.Date()) + " >> " + 
                                       socket.getRemoteSocketAddress()  + "から接続を受付ました");
///                    System.out.println(socket.getInetAddress() + "から接続を受付ました");

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            // 出力ストリームを取得
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // 入力ストリームを取得
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // 読み込んだ行をそのまま出力ストリームに書き出す
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                // 読み込んだデータを表示
                System.out.println(sdf.format(new java.util.Date()) + " >> [" + inputLine + "]");
                // 出力ストリームに書き出す
                out.println(inputLine);
            }

///// クライアント側から切断されるまで　Read待ちしたい
///// クライアント側から切断されるまで　Read待ちしたい

            // 入出力ストリームを閉じる
            if (in != null) {
                System.out.println("入力ストリームを閉じる");
                in.close();
            }
            if (out != null) {
                System.out.println("出力ストリームを閉じる");
                out.close();
            }

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (socket != null) {
                    System.out.println("ソケットを閉じる");
                    // ソケットを閉じる
                    socket.close();
                }
            } catch (IOException e) {}
                System.out.println("コネクションが切断されました "+ socket.getRemoteSocketAddress());
      }

    }
}
