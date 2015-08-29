package disms.SISStore.client;
/*�������˽����ļ�*/
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFileChooser;

/**
* �����õ��İ󶨶˿ڳ�ʼΪ10002������󶨲��ɹ���������Ķ˿ڣ�
* �󶨴�����tryBindTimes��������ʾ�����ʧ�ܻ������һ��
* ��ǰ�󶨶˿���DefaultBindPort+tryBindTimes������
* ���ϵͳ(���ô˳���Ķ���)���Ի�ȡ��ǰ�İﶨ�˿ڣ�
* �����߿ͻ��˷���Ķ˿ں���ʹ������ȷ���ӵ��ö˿��ϣ�
* @author
*
*/
//public class GetFile extends Thread {
public class GetFile{

    // �����׽��ֵȴ��Է������Ӻ��ļ�����
    ServerSocket serSocket;
    // �ɷ����׽��ֲ����� �׽���
    Socket tempSocket;
    // ���ڶ�ȡ
    InputStream inSocket;
    // ��������ļ�
    RandomAccessFile inFile = null;
    // ��ʱ������
    byte byteBuffer[] = new byte[1024];
    // Ĭ����10002�˿ڼ�������
    int defaultBindPort = 10002;
    // ��ʼ�İ󶨶˿ڴ���Ϊ0
    int tryBindTimes = 0;
    // ��ǰ�󶨵Ķ˿ں���10002Ĭ�϶˿�
    int currentBindPort = defaultBindPort + tryBindTimes;

    /**
    * @���췽��
    * @�׳��쳣��ԭ�����޷��󶨷���Ķ˿�
    * */
    public void getTheFile(int port,String filePath) throws Exception {
        try {
            // �󶨷���Ķ˿�
            this.bindToServerPort();

        } catch (Exception e) {
            e.printStackTrace();
            // �󶨲��ɹ�����
            System.out.println(e.toString());
            throw new Exception("�󶨶˿ڲ��ɹ�!");

        }
        // �ļ�ѡ�����Ե�ǰ��Ŀ¼��
//        JFileChooser jfc = new JFileChooser(".");
//        jfc.showSaveDialog(new javax.swing.JFrame());
//        // ��ȡ��ǰ��ѡ���ļ�����
//        File savedFile = jfc.getSelectedFile();

        // �Ѿ�ѡ�����ļ�
        File savedFile = new File(filePath);
        if (savedFile != null) {
            // ��ȡ�ļ������ݣ�����ÿ���Կ�ķ�ʽ��ȡ����
            inFile = new RandomAccessFile(savedFile, "rw");

        }
    }
   
   /**
    * @����߳�
    */
    public void run() {
        try {
            if (this.inFile == null) {
                System.out.println("û��ѡ���ļ�");
                // �رշ����׽���
                this.serSocket.close();
                // û��ѡ���ļ�
                return;
            }
           
            System.out.println("wait for..." + '\n' + "�ȴ��Է�����");
            // �ȴ��Է�������
            tempSocket = serSocket.accept();
            // �����������Ͻ��׳��쳣
            this.serSocket.setSoTimeout(5000);
            // ��ȡ������
            this.inSocket = tempSocket.getInputStream();
           
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
            return;
        }
       
        // ����Ϊ�����ļ������ �׽���������
        int amount;
        try {
            while ((amount = inSocket.read(byteBuffer)) != -1) {
                inFile.write(byteBuffer, 0, amount);
            }
            // �ر���
            inSocket.close();
 //           javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(),"�ѽ��ճɹ�", "��ʾ!", javax.swing.JOptionPane.PLAIN_MESSAGE);
            System.out.println("Get OK");
            System.out.println("�������!");
            // �ر��ļ�
            inFile.close();
            // �ر���ʱ�׽���
            tempSocket.close();
            // �رշ����׽���
            this.serSocket.close();
           
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }

    /**
    * @�󶨶˿�
    * @throws Exception �׳��쳣��ԭ�����޷��󶨷���Ķ˿�
    */
    private void bindToServerPort() throws Exception {
        try {
            // ����󶨵Ķ˿ںŵ���ǰ�Ŀ���̨��
            System.out.println("�԰󶨵Ķ˿ں���:" + this.currentBindPort);
            // ���Լ��Ļ����Ͽ�һ���������׽��ֲ��ȴ������ߵ�����
           // serSocket.setReuseAddress(true);
            serSocket = new ServerSocket(this.currentBindPort);

        } catch (Exception e) {
            e.printStackTrace();
            // �󶨲��ɹ�����
            System.out.println(e.toString());
            // ���˲�ֹһ����
            this.tryBindTimes = this.tryBindTimes + 1;
           // �ɲ鿴�ԵĴ���getTryBindedTimes
            this.currentBindPort = this.defaultBindPort + this.tryBindTimes;

            // ����ԵĴ�������20�� �˳�
            if (this.tryBindTimes >= 20) {
                throw new Exception("�޷��󶨵�ָ���˿�" + '\n' + "����̫�����!");

            }
            // �ݹ�İ�
            this.bindToServerPort();
        }

        // ����󶨵Ķ˿ںŵ���ǰ�Ŀ���̨��
        System.out.println("�ɹ��󶨵Ķ˿ں���: " + this.currentBindPort);

    }

    // ��ȡ�԰󶨵Ķ˿�
    public int getTryBindedTimes() {
        return this.tryBindTimes;
    }

    // ��ȡ�Ѿ��󶨵Ķ˿�
    public int getCurrentBindingPort() {
        return this.currentBindPort;
    }
   

    /**
    * @���Է���
    * @param args
    */
    public static void main(String args[]) {
        GetFile getFile = new GetFile();
        try {

            getFile.getTheFile(10002, "E:\\lib.zip");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("�޷������ļ�!");
            System.exit(1);
        }
        getFile.run();
        try {

            getFile.getTheFile(10002, "E:\\lib2.zip");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("�޷������ļ�!");
            System.exit(1);
        }
        getFile.run();

    }

}