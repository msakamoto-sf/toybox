// created_at : 2008-09-08
// $Id: SocketClient.java 19530 2008-09-18 14:17:50Z msakamoto-sf $
import java.io.*;
import java.net.*;
import java.util.*;

public class SocketClient {
    public static void main(String[] args){
        try{
            int myport = Integer.parseInt(args[0]);

            System.out.println("�T�[�o�̐ڑ��|�[�g"+myport+"�ɐڑ����܂��B");
            // �\�P�b�g�𐶐�
            Socket socket = new Socket("localhost", myport);

            // �o�̓X�g���[�����擾
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // ���̓X�g���[�����擾
            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(
                                    socket.getInputStream()));

            // �uHello World�v���T�[�o�[�ɑ���
            out.println("Hello World");
            // �ǂݍ��񂾃f�[�^��\��
            System.out.println(in.readLine());

            System.out.println("end of sending datas.");
            Thread.sleep(5 * 1000);
            
            // ���o�̓X�g���[�������
            out.close();
            System.out.println("closed output stream.");
            Thread.sleep(5 * 1000);

            in.close();
            System.out.println("closed input stream.");
            Thread.sleep(5 * 1000);

            // �\�P�b�g�����
            socket.close();
            System.out.println("socket was closed.");
//        } catch(IOException e){
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
