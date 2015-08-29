/*�ļ����Ͷ�*/
package disms.SISStore.client;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;

import javax.swing.JFileChooser;

/**
* �ڷ������˿���������� ʵ�����׽��� �������ļ�
*
* @author 
*/
public class SendFile extends Thread {

    // Զ�̵�IP�ַ���
    String remoteIPString = null;
    // Զ�̵ķ���˿�
    int port;
    // ��ʱ�׽���
    Socket tempSocket;
    // �����ļ��õ������
    OutputStream outSocket;
    // �����͵��ļ�
    RandomAccessFile outFile;
    // �����ļ��õ���ʱ������
    byte byteBuffer[] = new byte[1024];

    /**
    * ���췽��������ѡ�����ļ���λ�� �����ⲿ����Զ�̵�ַ�Ͷ˿�
    *
    */
    public SendFile(String remoteIPString, int port,String filePath) {
        try {
            this.remoteIPString = remoteIPString;
            this.port = port;

           // ѡ���͵��ļ�λ��
//            JFileChooser jfc = new JFileChooser(".");
            File file = new File(filePath);
//            int returnVal = jfc.showOpenDialog(new javax.swing.JFrame());
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                file = jfc.getSelectedFile();
//
//            }

            outFile = new RandomAccessFile(file, "r");

        } catch (Exception e) {
        }
    }

    /**
    * �Ⱦ������Ƿ��������ȿ���
    *
    */
    public void run() {
        try {
            this.tempSocket = new Socket(this.remoteIPString, this.port);
            System.out.println("����������ӳɹ�!");
            outSocket = tempSocket.getOutputStream();

            int amount;
            System.out.println("��ʼ�����ļ�...");
            while ((amount = outFile.read(byteBuffer)) != -1) {
                outSocket.write(byteBuffer, 0, amount);
//                System.out.println("�ļ�������...");
            }
            System.out.println("Send File complete");
//            javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(),
//                    "�ѷ������", "��ʾ!", javax.swing.JOptionPane.PLAIN_MESSAGE);
            outFile.close();
            tempSocket.close();

        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    /**
    * ���Է���
    *
    * @param args
    */
    public static void main(String args[]) {
        SendFile sf1 = new SendFile("127.0.0.1", 10000,"C:\\1(1).mp3");
        SendFile sf2 = new SendFile("127.0.0.1", 10000,"C:\\1(2).mp3");
        SendFile sf3 = new SendFile("127.0.0.1", 10000,"C:\\1(3).mp3");
        SendFile sf4 = new SendFile("127.0.0.1", 10000,"C:\\1(4).mp3");
        Thread t1 = new Thread(sf1);
        Thread t2 = new Thread(sf2);
        Thread t3 = new Thread(sf3);
        Thread t4 = new Thread(sf4);
        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }
}