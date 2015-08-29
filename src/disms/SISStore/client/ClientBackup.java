package disms.SISStore.client;
import java.io.*;
import java.net.Socket;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JFileChooser;


public class ClientBackup{
	private  String userPath = "D:\\SIS\\";
	private  String fileName = "";
	private  byte[] headHash;
	private  byte[] midHash;
	private  byte[] tailHash;
	private  byte[] g_Hash;
	private  long fileSize = 0;
	private  String fileType = "";
	private  String filePath = "";
	private  UUID luid;
	private  final long MinFileSize = 20000;
	private  String strDATE = null;
	private  String strTemp = null;
	private  boolean isLocalDedup = false;
	private  String fileInfo = null;
	private  String[] receiveInfo = null;
	private  String[] commandLines = null;
	private  boolean smallSize;
	private  File fileObj = null;
	private  String userID = null;
	private String host="localhost";
	private int port=8000;
	private Socket socket;
	/*
	 * ��Byte������ǰ8��Ԫ��ת��Ϊlong��
	 * 
	 */
	public static String bytesToHexString(byte[] src){
	       StringBuilder stringBuilder = new StringBuilder("");
	       if (src == null || src.length <= 0) {
	           return null;
	       }
	       for (int i = 0; i < src.length; i++) {
	           int v = src[i] & 0xFF;
	           String hv = Integer.toHexString(v);
	           if (hv.length() < 2) {
	               stringBuilder.append(0);
	           }
	           stringBuilder.append(hv);
	       }
	       return stringBuilder.toString();
	   }
	public  long byteToLongLOW(byte[] b) {
		long s = 0; 
		long s0 = b[0] & 0xff;// ���λ
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// ���λ
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;
		// s0����
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	} 
	/*
	 * ��Byte�����к�8��Ԫ��ת��Ϊlong��
	 */
	public  long byteToLongHIGH(byte[] b) {
		long s = 0; 
		long s0 = b[8] & 0xff;// ���λ
		long s1 = b[9] & 0xff;
		long s2 = b[10] & 0xff;
		long s3 = b[11] & 0xff;
		long s4 = b[12] & 0xff;// ���λ
		long s5 = b[13] & 0xff;
		long s6 = b[14] & 0xff;
		long s7 = b[15] & 0xff;
		// s0����
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	} 
	/*
	 * ��������:��commands���д���������commandLines����ʱΪ������commandLineS����ռ�
	 */
	public  void resolveCommand(String[] commands){		
		commandLines = commands;
		userID = commandLines[0];
		filePath = commandLines[2];
	}
	/*
	 * ����ļ���С�Ƿ���ϱ�׼
	 */
	public  boolean checkValidFile(String FilePath){
		String SRCFilePath = FilePath;
		//If this file exists
		System.out.println(SRCFilePath);
		File CheckIt = new File(SRCFilePath);
		if( CheckIt.exists()){		
	       	smallSize = false;
	       	if(CheckIt.length() < MinFileSize){
		    	System.out.println("ClientBackup-->checkValidFile:The size of the file is too small");
		    	System.out.println(CheckIt.length());
		    	SISgraphics.jl.append(FilePath+" is too small to backup"+'\n');
	       		smallSize = true;
	       	}
	       	else{
		    	System.out.println("ClientBackup-->checkValidFile:The size of the file is fitable");
	       	}	       		
	    	return smallSize;
	    }
	    else{
	    	System.out.println("ClientBackup-->checkValidFile:The path of the file is invalid");
	    	SISgraphics.jl.append(FilePath+" is invalid"+'\n');
	    	return false;
		}
	}
	/*
	 * ���ִ�������ʱ�䣬��ȷ������
	 */
	public  void getTime(){
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd.HH:mm:ss:SSS");      
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��      
		strDATE = formatter.format(curDate);
		System.out.println("ClientBackup-->getTime:the current time is "+strDATE);
	}
	/*
	 *�����ļ���HASH�� 
	 */
	public  void calcuHashArray(File fileOBJ) throws NoSuchAlgorithmException, DigestException, IOException{
		CalculateFileHash cal = new CalculateFileHash();
		headHash = cal.MD5HashValueFirst(fileOBJ);
		midHash = cal.MD5HashValueSecond(fileOBJ);
		tailHash = cal.MD5HashValueThird(fileOBJ);
		g_Hash = cal.MD5HashValueGlobal(fileOBJ);
		System.out.println("ClientBackup-->calcuHashArray:the hashes have been calculated");
	}

	/*
	 * ���ļ�����Ϣд�뵽�ļ�֮��
	 */
	public  void saveFileInfo() throws IOException{
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd");      
		Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��      
		String today = formatter.format(curDate);
		String tempPath = userPath + userID + "\\FileInfo\\"+today+".txt";
		File dir = new File(userPath + userID + "\\"); 
        if(!dir.exists())
        	dir.mkdir();
		File dirFile = new File(userPath + userID + "\\FileInfo\\"); 
        if(!dirFile.exists())
        	dirFile.mkdir();
		File f = new File(filePath);
		FileWriter fosTempCONS = new FileWriter(tempPath,true);
		fosTempCONS.write(strDATE+'*');
		fosTempCONS.write(luid.toString()+'*');
		SISTaskBackup1.vec.addElement(luid.toString()+'*'+strDATE+'*'+fileName.toString());
		fosTempCONS.write(receiveInfo[1].toString()+'*');
		fosTempCONS.write(receiveInfo[2].toString()+'*');
		fosTempCONS.write(fileName.toString()+'*');
		fosTempCONS.write(filePath.toString()+'*');
		fosTempCONS.write(fileType.toString()+'*');
		strTemp = String.valueOf(fileSize); 
		fosTempCONS.write(strTemp+'*');
		fosTempCONS.write(String.valueOf(bytesToHexString(headHash))+'*');
		fosTempCONS.write(String.valueOf(bytesToHexString(midHash))+'*');
		fosTempCONS.write(String.valueOf(bytesToHexString(tailHash))+'*');
		fosTempCONS.write(String.valueOf(bytesToHexString(g_Hash))+'\n');
		fosTempCONS.close();
		System.out.println("ClientBackup-->saveFileInfo:the information of the file has been written into a  file");
		
	}
	/*
	 * �����û��Ƿ��Ѿ��洢�����ļ�
	 */
	public  boolean localDedup(){new ClientLocalDedup();
		isLocalDedup = ClientLocalDedup.findLocalDedup(fileInfo,"D:\\SIS\\"+userID+"\\FileInfo\\");
		if(isLocalDedup){
			System.out.println("ClientBackup-->localDedup:duplicate file,the information will be sent to server for further examination");	
		}
		else{
			System.out.println("ClientBackup-->localDedup:new file,the information will be sent to server for further examination");	
		}
		return isLocalDedup;
	}
	/*
	 * ����ļ�����
	 */
	public  void getFileObj(){
		if(!filePath.isEmpty()){	
			fileObj = new File(filePath);
		}
	}
	/*
	 * ��ȡ�ļ���Ϣ
	 */
	public  void getFileInfo() throws Exception{
		//1.��õ�ǰʱ��
		getTime();			
		//2.�������ļ�LUIDֵ
		luid = UUID.randomUUID();			
		//3.�����ļ���С
		CalculateFileSize calFileSize = new CalculateFileSize();
		fileSize = calFileSize.getFileSizes(fileObj);			
		//4.�ж��ļ�����
		fileName = fileObj.getName();
		fileType = JudgeFileType.getEXTName(filePath);			
		//5.����HASHֵ
		calcuHashArray(fileObj);	
	}
	/*
	 * ������Ҫ���ݵ����ļ���Ϣ���������˽��м��
	 */
	public  void sendFileInfo() throws IOException{
		BufferedReader br;
	    PrintWriter pw;
	    br=getReader(socket);
	    pw=getWriter(socket);
		fileInfo = fileType.toString()+'*'+fileSize+'*'+byteToLongLOW(headHash)+'*'+byteToLongHIGH(headHash)+'*'+byteToLongLOW(midHash)+'*'+byteToLongHIGH(midHash)+'*'+byteToLongLOW(tailHash)+'*'+byteToLongHIGH(tailHash)+'*'+byteToLongLOW(g_Hash)+'*'+byteToLongHIGH(g_Hash);			
//		comTO.sendMSG(userID+'#'+"backup"+'#'+ fileInfo+'#'+strDATE);
		pw.println(userID+'#'+"backup"+'#'+ fileInfo+'#'+strDATE);
		String line = br.readLine();
		System.out.println("ClientBackup-->sendFileInfo:the request has been sent to the server for further examinnation");
		
		System.out.println(line);
		if(!line.equalsIgnoreCase("")){
			receiveInfo = line.split("[*]");//today
			System.out.println("ClientBackup-->receiveFileInfo:the server has sent the results to the client");
			System.out.println(receiveInfo[0]);
		}
		else{
			System.out.println("ClientBackup-->receiveFileInfo:the server fail to send the results to the client");
		}
	}
	/*
	 * ��÷������˵ĵ�ִ����Ϣ
	 */
//	public  void receiveFileInfo() throws IOException{
//		String line = br.readLine();
//		if(!line.equalsIgnoreCase("")){
//			receiveInfo = line.split("[*]");//today
//			System.out.println("ClientBackup-->receiveFileInfo:the server has sent the results to the client");
//			System.out.println(receiveInfo[0]);
//		}
//		else{
//			System.out.println("ClientBackup-->receiveFileInfo:the server fail to send the results to the client");
//		}
//	}
//	public  void createCom(){
//		comTO = new ClientTransInfo();			
//		comTO.conn();
//	}
	private PrintWriter getWriter(Socket socket)throws IOException{
	    OutputStream socketOut = socket.getOutputStream();
	    return new PrintWriter(socketOut,true);
	}
	private BufferedReader getReader(Socket socket)throws IOException{
	    InputStream socketIn = socket.getInputStream();
	    return new BufferedReader(new InputStreamReader(socketIn));
	}
	public ClientBackup(String[] commands) throws Exception{
		socket=new Socket(host,port); 	   
		resolveCommand(commands);
		if(!checkValidFile(filePath))
		{
			getFileObj();
			getFileInfo();
			//saveFileTMPInfo();
			//localDedup();
			//createCom();
			sendFileInfo();
			//receiveFileInfo();
			if(receiveInfo[0].equalsIgnoreCase("completed")){				
				saveFileInfo();
				System.out.println("ClientBackup-->ClientBackup:global duplicate file,clientBackup mission comleted");
			}
			else if(receiveInfo[0].equalsIgnoreCase("undone"))
			{
				System.out.println("ClientBackup-->ClientBackup:complete new File,Send the file");
//				new SendFile("127.0.0.1", 10002, fileObj).run();
				SendFile sf1 = new SendFile("127.0.0.1",11000,fileObj.toString());
				sf1.start();
//				SISCLIENT.sf.sendTheFile("127.0.0.1", fileObj.getPath(), 10004);
//				SISCLIENT.sf.run();
				saveFileInfo();
			}
//			comTO.sendMSG("done");
//			pw.println("done");
//			comTO.close();
	    	SISgraphics.jl.append(filePath+" has been backuped successfully"+'\n');
		}		
	}
}