package disms.SISStore.client;

import java.io.*;

/*************************************
1. ͨ����׺������exe,jpg,bmp,rar,zip�ȵȡ�

2. ͨ����ȡ�ļ�����ȡ�ļ���Content-type���жϡ�

3. ͨ����ȡ�ļ����������ļ������ض���һЩ�ֽڱ�ʶ�����ֲ�ͬ���͵��ļ���

4. ����ͼƬ����ͨ���������жϣ��������ŵ�ΪͼƬ�������Ե����ǡ�
***************************************/

public class JudgeFileType{
	public static String getEXTName(String FileName){
		File file =new File(FileName);
		String fileName=file.getName();
		String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
		return prefix;
	}
	public static void main(String args[]){
	    File f =new File("TileTest.java.exe");
	    String fileName=f.getName();
	    String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
	    System.out.println(prefix);
	}
}