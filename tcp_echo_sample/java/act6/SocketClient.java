// created_at : 2008-09-08
// $Id: SocketClient.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.io.*;
import java.net.*;
import java.util.*;

public class SocketClient {
    public static void main(String[] args){
        try{
            int myport = Integer.parseInt(args[0]);

            System.out.println("サーバの接続ポート"+myport+"に接続します。");
            // ソケットを生成
            Socket socket = new Socket("localhost", myport);

            // 出力ストリームを取得
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // 入力ストリームを取得
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    socket.getInputStream()));

            // 「Hello World」をサーバーに送る
            out.println("Hello World");
            // 読み込んだデータを表示
            System.out.println(in.readLine());

            System.out.println("end of sending datas.");
            Thread.sleep(5 * 1000);
            
            // 入出力ストリームを閉じる
            out.close();
            System.out.println("closed output stream.");
            Thread.sleep(5 * 1000);

            in.close();
            System.out.println("closed input stream.");
            Thread.sleep(5 * 1000);

            // ソケットを閉じる
            socket.close();
            System.out.println("socket was closed.");
//        } catch(IOException e){
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
