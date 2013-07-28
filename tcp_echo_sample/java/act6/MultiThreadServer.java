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
            // �T�[�o�[�\�P�b�g�̐���
            ServerSocket serverSocket = new ServerSocket(myport);

            // ���C�����[�v
            while(true) {
                try {
///                    System.out.println("�N���C�A���g����̐ڑ����|�[�g"+myport+"�ő҂��܂�");
                    // �N���C�A���g����̐ڑ���҂��܂�
                    Socket socket = serverSocket.accept();
                    System.out.println(sdf.format(new java.util.Date()) + " >> " + 
                                       socket.getRemoteSocketAddress()  + "����ڑ�����t�܂���");
///                    System.out.println(socket.getInetAddress() + "����ڑ�����t�܂���");

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            // �o�̓X�g���[�����擾
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // ���̓X�g���[�����擾
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            // �ǂݍ��񂾍s�����̂܂܏o�̓X�g���[���ɏ����o��
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                // �ǂݍ��񂾃f�[�^��\��
                System.out.println(sdf.format(new java.util.Date()) + " >> [" + inputLine + "]");
                // �o�̓X�g���[���ɏ����o��
                out.println(inputLine);
            }

///// �N���C�A���g������ؒf�����܂Ł@Read�҂�������
///// �N���C�A���g������ؒf�����܂Ł@Read�҂�������

            // ���o�̓X�g���[�������
            if (in != null) {
                System.out.println("���̓X�g���[�������");
                in.close();
            }
            if (out != null) {
                System.out.println("�o�̓X�g���[�������");
                out.close();
            }

        } catch(Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (socket != null) {
                    System.out.println("�\�P�b�g�����");
                    // �\�P�b�g�����
                    socket.close();
                }
            } catch (IOException e) {}
                System.out.println("�R�l�N�V�������ؒf����܂��� "+ socket.getRemoteSocketAddress());
      }

    }
}
