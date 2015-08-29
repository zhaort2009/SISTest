package disms.SISStore.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class ClientLocalDedup{
  
	 public static boolean findLocalDedup(String fileInfo,String path){  
	        //Ҫ��ѯ���ַ���   
	        String str = fileInfo;   
	        //���ö�ȡ�ļ�����ʱ���ļ�����   
	        String encoding = "utf8";   
	        //ĳ���ļ�������   
	        String content = "";   
	        //�����ļ��ĵ�ַ   
	        Set fileNameSet = SearchStr.fileNameSet(path);   
	           
	        for (Iterator i = new TreeSet(fileNameSet).iterator() ; i.hasNext() ; )      
	        {      
	            String filePath = i.next().toString();   
	            filePath = filePath.replaceAll("\\\\", "\\\\\\\\");   
	            
	            //System.out.println(filePath);   
	            //if(filePath.length() > 5 && ".txt".equals(filePath.substring(filePath.length()-5, filePath.length()))){   
	                try {   
	                    content = SearchStr.readTxt(filePath, encoding);	                 
	                    if(content.indexOf(str) != -1){   
	                        System.out.println("ClientLocalDedup-->findLocalDedup:found matching information");   
	                        //System.out.println(filePath);
	                        return true;  
	                    }
	                    else{
	                        System.out.println("ClientLocalDedup-->findLocalDedup:not found matching information");
	                        return false;  	
	                    }
	                       
	                } catch (IOException e) {   
	                    e.printStackTrace();   
	                }   
	          	}   
	       // }
			return false;
			  
	    }   
	       
	       
	       
	    /**  
	     * ��ȡ�ı��ļ�����  
	     *   
	     * @param filePathAndName  
	     *            ������������·�����ļ���  
	     * @param encoding  
	     *            �ı��ļ��򿪵ı��뷽ʽ  
	     * @return �����ı��ļ�������  
	     */  
	    public static String readTxt(String filePathAndName, String encoding)   
	            throws IOException {   
	        encoding = encoding.trim();   
	        StringBuffer str = new StringBuffer("");   
	        String st = "";   
	        try {   
	            FileInputStream fs = new FileInputStream(filePathAndName);   
	            InputStreamReader isr;   
	            if (encoding.equals("")) {   
	                isr = new InputStreamReader(fs);   
	            } else {   
	                isr = new InputStreamReader(fs, encoding);   
	            }   
	            BufferedReader br = new BufferedReader(isr);   
	            try {   
	                String data = "";   
	                while ((data = br.readLine()) != null) {   
	                    str.append(data + " ");   
	                }   
	            } catch (Exception e) {   
	                str.append(e.toString());   
	            }   
	            st = str.toString();   
	        } catch (IOException es) {   
	            st = "";   
	        }   
	        return st;   
	    }   
	       
	       
	    /**  
	     * ��һ���ļ��м��������ļ����а������ļ���ȡ����  
	     * @param path ���Ե�ַ������"c:\\test\\"  
	     * @return  
	     */  
	    public static Set fileNameSet(String path)      
	    {      
	        File directory = new File(path);      
	        List fileList = listAllFiles(directory);      
	     
	        Set fileNameSet = new HashSet(fileList.size());      
	        for (int i = 0 ; i< fileList.size() ; i++)      
	        {      
	            File file = (File)fileList.get(i);      
	            fileNameSet.add(file.getAbsolutePath());      
	        }      
	     
	        return fileNameSet;   
	     
	    }   
	       
	    /**  
	     * fileNameSet �����ĸ�������  
	     * @param directory  
	     * @return  
	     */  
	    private static List listAllFiles(File directory)      
	    {      
	        if (directory == null || !directory.isDirectory()){      
	            return null;      
	        }      
	        List fileList = new ArrayList();      
	        addSubFileList(directory, fileList);      
	     
	        return fileList;      
	    }      
	     
	    /**  
	     * fileNameSet �����ĸ�������  
	     * @param file  
	     * @param fileList  
	     */  
	    private static void addSubFileList(File file,List fileList){      
	        File[] subFileArray = file.listFiles();      
	        if (subFileArray == null     
	            || subFileArray.length == 0     
	        ){      
	            return;      
	        }      
	     
	        for (int i = 0 ; i < subFileArray.length ; i++)      
	        {      
	            File subFile = subFileArray[i];      
	            if (subFile == null     
	            ){      
	                continue;      
	            }      
	            if (subFile.isFile()      
	            ){      
	                fileList.add(subFile);      
	                continue;      
	            }      
	            else if (subFile.isDirectory()      
	            ){      
	                addSubFileList(subFile, fileList);      
	            }      
	        }      
	    }    
}