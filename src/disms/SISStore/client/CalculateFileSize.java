package disms.SISStore.client;

import java.io.File;
import java.text.DecimalFormat;
import java.io.FileInputStream;
public class CalculateFileSize
{
    public long getFileSizes(File f) throws Exception{//ȡ���ļ���С
        long s=0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
           s= fis.available();
        } else {
            f.createNewFile();
            System.out.println("�ļ�������");
        }
        return s;
    }
    // �ݹ�
    public long getFileSize(File f)throws Exception//ȡ���ļ��д�С
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory())
            {
                size = size + getFileSize(flist[i]);
            } else
            {
                size = size + flist[i].length();
            }
        }
        return size;
    }
    public String FormetFileSize(long fileS) {//ת���ļ���С
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
   
    public long getlist(File f){//�ݹ���ȡĿ¼�ļ�����
        long size = 0;
        File flist[] = f.listFiles();
        size=flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
    }
   
    public  void main(String args[]) throws Exception
    {
    	CalculateFileSize g = new CalculateFileSize();
    	String path = "D:\\table.zip";
        File ff = new File(path);
        long l = 0;
        l = g.getFileSizes(ff);
        System.out.println(l+"bytes");
        /*long startTime = System.currentTimeMillis();
        try
        {
            long l = 0;
            String path = "C:\\WINDOWS";
            File ff = new File(path);
            if (ff.isDirectory()) { //���·�����ļ��е�ʱ��
                System.out.println("�ļ�����           " + g.getlist(ff));
                System.out.println("Ŀ¼");
                l = g.getFileSize(ff);
                System.out.println(path + "Ŀ¼�Ĵ�СΪ��" + g.FormetFileSize(l));
            } else {
                System.out.println("     �ļ�����           1");
                System.out.println("�ļ�");
                l = g.getFileSizes(ff);
                System.out.println(path + "�ļ��Ĵ�СΪ��" + g.FormetFileSize(l));
            }
           
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("�ܹ�����ʱ��Ϊ��" + (endTime - startTime) + "����...");*/
    }
}