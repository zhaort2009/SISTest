/*�������˽����ļ�*/
package disms.SISStore.server;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFileChooser;


/**
* �����õ��İ󶨶˿ڳ�ʼΪ10000������󶨲��ɹ���������Ķ˿ڣ�
* �󶨴�����tryBindTimes��������ʾ�����ʧ�ܻ������һ��
* ��ǰ�󶨶˿���DefaultBindPort+tryBindTimes������
* ���ϵͳ(���ô˳���Ķ���)���Ի�ȡ��ǰ�İﶨ�˿ڣ�
* �����߿ͻ��˷���Ķ˿ں���ʹ������ȷ���ӵ��ö˿��ϣ�
* @author
*
*/
public class GetFile extends Thread {

    // �����׽��ֵȴ��Է������Ӻ��ļ�����
    static ServerSocket serSocket;
    // �ɷ����׽��ֲ����� �׽���
    Socket tempSocket;
    // ���ڶ�ȡ
    InputStream inSocket;
    // ��������ļ�
    RandomAccessFile inFile = null;
    // ��ʱ������
    byte byteBuffer[] = new byte[1024];
    // Ĭ����10000�˿ڼ�������
    static int defaultBindPort = 10000;
    static boolean binded = false;
    private ThreadPool filePool;
    private ThreadPool msgpool;
    private final int poolSize = 3;
 
    /**
    * @���췽��
    * @�׳��쳣��ԭ�����޷��󶨷���Ķ˿�
    * */
    public GetFile(String filePath) throws Exception {
        serSocket = new ServerSocket(defaultBindPort);  
        filePool = new ThreadPool(Runtime.getRuntime().availableProcessors()*poolSize);
    }
   
   /**
    * @����߳�
    */
    public void fileService() {
    	while(true){
        try {
	            System.out.println("�ȴ��Է�����");	            
	            // �ȴ��Է�������
	            tempSocket = serSocket.accept();
	            filePool.execute(new HandlerFile(tempSocket));
	        } catch (Exception ex) {
	            System.out.println(ex.toString());
	            ex.printStackTrace();
	            return;
	        }
    	}
    }
    public void msgService() {
    	while(true){
            try {
    	            System.out.println("�ȴ��Է�����");	            
    	            // �ȴ��Է�������
    	            tempSocket = serSocket.accept();
    	            filePool.execute(new HandlerMsg(tempSocket));
    	        } catch (Exception ex) {
    	            System.out.println(ex.toString());
    	            ex.printStackTrace();
    	            return;
    	        }
        	}
        }

    
    /**
    * @���Է���
    * @param args
     * @throws Exception 
    */
    public static void main(String args[]) throws Exception {
         String filePath1 = "D:/kk";
         GetFile  getFile1 = new GetFile(filePath1);
         getFile1.fileService();

    }

}
class HandlerMsg implements Runnable{
	private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String msg;
    public HandlerMsg(Socket socket) throws IOException{
    	this.socket = socket;
    	in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	
    }
	public void run() {
		// TODO Auto-generated method stub
    	String line =null;
		try {
			
			line=in.readLine();
            //System.out.println("Client send is:"+line);
            
            //������������Ϣ�����ͻ���               
            out=new PrintWriter(socket.getOutputStream(),true);
            out.println("ServerGetInfo-->getInfo:Your Message Received!");
            System.out.println(line);
            out.close();
            in.close();
            socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
		
	}
}
class HandlerFile implements Runnable{
	private Socket socket;
	InputStream inSocket;
    byte byteBuffer[] = new byte[1024];
    RandomAccessFile inFile = null;
    public static int i = 0;
	public HandlerFile(Socket soc) throws IOException{
		socket = soc;
	    inSocket = socket.getInputStream();          
	    File savedFile = new File("D:\\kk"+i);
	    i++;
        inFile = new RandomAccessFile(savedFile, "rw");    
	}
	public void run() {
		// TODO Auto-generated method stub
		int amount;
	    try {
			while ((amount = inSocket.read(byteBuffer)) != -1) {
	            inFile.write(byteBuffer, 0, amount);
	        }
	        // �ر���
	        inSocket.close();
	        System.out.println("Get OK");
	        System.out.println("�������!");
	        // �ر��ļ�
	        inFile.close();
	        // �ر���ʱ�׽���
	        socket.close();      
	       
	    } catch (IOException e) {
	        System.out.println(e.toString());
	        e.printStackTrace();
	    }
	}
	
}

