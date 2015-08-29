package disms.SISStore.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;



public class ServerDelete{
	private  String filePath = "E:\\SISServer\\";
	private  String[] link_Info = null;
	private  String[] info_TMP = null;
	private  String info_Link= null;
	private  String deletefilePath= null;
	private  File fileOBJ = null;
	private  String infoFilePath = null;
	private  String returnInfo = null;
	private Socket socket;
	

	public  String readInfo(String info,String path) throws Exception{	       
	       FileReader fr = new FileReader(path);
	       BufferedReader buf = new BufferedReader(fr);

	       String str = "NOFILE";
	       while((str = buf.readLine()) != null){
	           if(str.contains(info)){
	    	       return str;
	           }
	       }
	       return null;
	}
	public  void processMSG(String command,String userID) throws Exception{
		link_Info = command.split("[*]");
		long hash1 = Long.parseLong(link_Info[6])%128;
		long hash2 = Long.parseLong(link_Info[8])%128;
		long hash3 = Long.parseLong(link_Info[10])%128;
		long hash4 = Long.parseLong(link_Info[12])%128;
		
		deletefilePath = filePath + hash1 +"\\"+hash2+"\\"+hash3+"\\"+hash4+"\\files\\"+link_Info[1]+"."+link_Info[4];
		infoFilePath = filePath + hash1 +"\\"+hash2+"\\"+hash3+"\\"+hash4+"\\"+"fileInfo.txt";
		filePath = filePath + hash1 +"\\"+hash2+"\\"+hash3+"\\"+hash4+"\\"+"linkInfo.txt";
		System.out.println(filePath);
		/*for(int count = 0;count < link_Info.length;count++){
			System.out.println(link_Info[count]);
		}*/
		info_Link = readInfo(link_Info[0],filePath);
		System.out.println(info_Link);
	}
	public  void deleteInfo() throws Exception{
		System.out.println("���ҵ�");
		new DeleteLine(link_Info[0],filePath);
	}
	public  void deleteFile() throws Exception{
		new DeleteLine(link_Info[1],infoFilePath);
		System.out.println(infoFilePath);
		System.out.println("����ȫ��ɾ����ɾ��ԭ�ļ�");
		boolean success = (new File(deletefilePath)).delete();
		if(success)
			System.out.println("ɾ���ɹ�");
		else
			System.out.println("�ļ��Ѿ�ɾ��");
	}
	private PrintWriter getWriter(Socket socket)throws IOException{
	    OutputStream socketOut = socket.getOutputStream();
	    return new PrintWriter(socketOut,true);
	}
	private BufferedReader getReader(Socket socket)throws IOException{
	    InputStream socketIn = socket.getInputStream();
	    return new BufferedReader(new InputStreamReader(socketIn));
	}
	public  void transInfo() throws IOException{
//        SISSERVER.out=new PrintWriter(SISSERVER.socket.getOutputStream(),true);
//        SISSERVER.out.println(returnInfo);
		BufferedReader br;
	    PrintWriter pw;
	    br=getReader(socket);
	    pw=getWriter(socket);
        pw.println(returnInfo);
	}
	ServerDelete(String command,String userID,Socket socket) throws Exception{
		this.socket= socket; 	   
		processMSG(command, userID);
		if((info_Link!=null)&&
		   (info_Link.indexOf(link_Info[0]) != -1)&&
		   (info_Link.indexOf(link_Info[4]) != -1)&&
		   (info_Link.indexOf(link_Info[5]) != -1))  
		{
			deleteInfo();
			if(readInfo(link_Info[1],filePath)==null){
				deleteFile();
			}
			else{
				System.out.println("���������ļ�");
			}
//			returnInfo = "ɾ���ɹ�";
//			transInfo();
		}
		else
		{
			System.out.println("û���ҵ�");
//			returnInfo = "ûɾ���ɹ�";
//			transInfo();
		}

    	
		

	}
}