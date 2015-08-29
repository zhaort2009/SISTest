package disms.SISStore.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
public class SISSERVER{
	/////////////////////////////
	private int port=8000;
	private ServerSocket serverSocket;
	private ExecutorService executorService; //�̳߳�
	private final int POOL_SIZE=1;  //����CPUʱ�̳߳��й����̵߳���Ŀ	  
	private int portForShutdown=8001;  //���ڼ����رշ���������Ķ˿�
	private ServerSocket serverSocketForShutdown;
	private boolean isShutdown=false; //�������Ƿ��Ѿ��ر�
	private final static int poolSize = 2;
	public static ThreadPool filePool = new ThreadPool(Runtime.getRuntime().availableProcessors()*poolSize);
	/////////////////////////////
	
	private String receivedMSG = null;
	private String[] rceMSG = null;
	private String command = null;
    public  static Socket socket;
    private static BufferedReader in;
    public  static PrintWriter out;
    private static ServerSocket ss;
	public static ServerSocket serSocket;
	//////////////////////////////////
    private Thread shutdownThread=new Thread(){   //����رշ��������߳�
        public void start(){
          this.setDaemon(true);  //����Ϊ�ػ��̣߳�Ҳ��Ϊ��̨�̣߳�
          super.start();
        }

        public void run(){
          while (!isShutdown) {
            Socket socketForShutdown=null;
            try {
              socketForShutdown= serverSocketForShutdown.accept();
              BufferedReader br = new BufferedReader(
                                new InputStreamReader(socketForShutdown.getInputStream()));
              String command=br.readLine();
             if(command.equals("shutdown")){
                long beginTime=System.currentTimeMillis(); 
                socketForShutdown.getOutputStream().write("���������ڹر�\r\n".getBytes());
                isShutdown=true;
                //����ر��̳߳�
    //�̳߳ز��ٽ����µ����񣬵��ǻ����ִ���깤�����������е�����
                executorService.shutdown();  
                
                //�ȴ��ر��̳߳أ�ÿ�εȴ��ĳ�ʱʱ��Ϊ30��
                while(!executorService.isTerminated())
                  executorService.awaitTermination(30,TimeUnit.SECONDS); 
                
                serverSocket.close(); //�ر���EchoClient�ͻ�ͨ�ŵ�ServerSocket 
                long endTime=System.currentTimeMillis(); 
                socketForShutdown.getOutputStream().write(("�������Ѿ��رգ�"+
                    "�رշ���������"+(endTime-beginTime)+"����\r\n").getBytes());
                socketForShutdown.close();
                serverSocketForShutdown.close();
                
              }else{
                socketForShutdown.getOutputStream().write("���������\r\n".getBytes());
                socketForShutdown.close();
              }  
            }catch (Exception e) {
               e.printStackTrace();
            } 
          } 
        }
      };
      
	SISSERVER() throws Exception{		
		///////////////////////////////////////////////////////////////////////////
		serSocket = new ServerSocket(11000);
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(60000); //�趨�ȴ��ͻ����ӵĳ���ʱ��Ϊ60��
		serverSocketForShutdown = new ServerSocket(portForShutdown);

		//�����̳߳�
		executorService= Executors.newFixedThreadPool( 
		Runtime.getRuntime().availableProcessors() * POOL_SIZE);
		    
		shutdownThread.start(); //��������رշ��������߳�
		System.out.println("����������");
		/////////////////////////////////////////////////////////////////////////
		getInfo();
	}

	public void getInfo() throws Exception {
	    while (!isShutdown) {
	        Socket socket=null;
	        try {
	          socket = serverSocket.accept();  //���ܻ��׳�SocketTimeoutException��SocketException
	          socket.setSoTimeout(0);  //�ѵȴ��ͻ��������ݵĳ�ʱʱ����Ϊ60��          
	          executorService.execute(new Handler(socket));  //���ܻ��׳�RejectedExecutionException
	        }catch(SocketTimeoutException e){
	           //���ش���ȴ��ͻ�����ʱ���ֵĳ�ʱ�쳣
	        }catch(RejectedExecutionException e){
	           try{
	             if(socket!=null)socket.close();
	           }catch(IOException x){}
	           return;
	        }catch(SocketException e) {
	           //�����������ִ��serverSocket.accept()����ʱ��
	           //ServerSocket��ShutdownThread�̹߳رն����µ��쳣�����˳�service()����
	           if(e.getMessage().indexOf("socket closed")!=-1)return;
	         }catch(IOException e) {
	           e.printStackTrace();
	        }
	      }
    }
	public static void main(String[] args) throws Exception{
		new SISSERVER();
	}
}
class Handler implements Runnable{
	  /////////////////////////////////////
	  private String receivedMSG = null;
	  private String[] rceMSG = null;
	  private String userID = null;//�û���ID��д�뵽�ļ�֮��
	  private String command = null;
	  private String fileInfo = null;
	  private String strDATE = null;
	  /////////////////////////////////////
	  public  Socket socket;
	  private Socket fileSoc; 
	  public static BufferedReader br;
	  public static PrintWriter pw;
	 ///////////////////////////////////////////
	  public void handleFileInfo(){
			rceMSG = receivedMSG.split("[#]");
			userID = rceMSG[0];
			command = rceMSG[1];
			fileInfo = rceMSG[2];
			if(command.equalsIgnoreCase("backup"))
			{
				strDATE = rceMSG[3];
			}
		}
	  public void initService(Socket socket) throws Exception{
			if (command.equalsIgnoreCase("backup"))
			{
				initBackup(fileInfo,strDATE,socket);
			}
			else if(command.equalsIgnoreCase("restore"))
			{
				initRestore(fileInfo,socket);
			}
			else if(command.equalsIgnoreCase("delete"))
			{
				initDelete(fileInfo,socket);
			}
		}

		/*
		 * �������ݷ���
		 */	
		public void initBackup(String Info,String time,Socket socket) throws Exception{
			System.out.println("SISSERVER-->initBackup:start the backup process in the server");
			new ServerBackup(Info,userID,time,socket);
		}
		/*
		 * ������ԭ����
		 */
		public void initRestore(String Info,Socket socket) throws Exception{
			System.out.println("SISSERVER-->initRestore:start the Restore process in the server");
			new ServerRestore(Info,userID,socket);
		}
		/*
		 * ����ɾ������
		 */
		public void initDelete(String Info,Socket socket) throws Exception{
			System.out.println("SISSERVER-->initDelete:start the delete process in the server");
			new ServerDelete(Info,userID,socket);
		}
	  
	  public Handler(Socket socket) throws IOException{
	    this.socket=socket;
//	    serSocket = new ServerSocket(10000);
	  }
	  private PrintWriter getWriter(Socket socket)throws IOException{
	    OutputStream socketOut = socket.getOutputStream();
	    return new PrintWriter(socketOut,true);
	  }
	  private BufferedReader getReader(Socket socket)throws IOException{
	    InputStream socketIn = socket.getInputStream();
	    return new BufferedReader(new InputStreamReader(socketIn));
	  }
	  public String echo(String msg) {
	    return "echo:" + msg;
	  }
	  public void run(){
	    try {
	      System.out.println("New connection accepted " +
	      socket.getInetAddress() + ":" +socket.getPort());
	      br =getReader(socket);
	      pw = getWriter(socket);

	      String msg = null;
	      msg = br.readLine();
		  System.out.println(msg);
		  if(msg!=null)
		  if(!msg.equalsIgnoreCase("done")){
	         receivedMSG = msg;
	         handleFileInfo();
	         try {
				 initService(socket);
			 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
         }
	    }catch (IOException e) {
	       e.printStackTrace();
	    }finally {
	       try{
	         if(socket!=null)socket.close();
	       }catch (IOException e) {e.printStackTrace();}
	    }
	  }
	}
class HandlerTrans implements Runnable{
		private Socket socket;
		InputStream inSocket;
	    byte byteBuffer[] = new byte[1024];
	    RandomAccessFile inFile = null;
	    public static int i = 0;
		public HandlerTrans(Socket soc,String filePath) throws IOException{
			System.out.println("into HandlerTrans");
			socket = soc;
		    inSocket = socket.getInputStream();          
		    File savedFile = new File(filePath);
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