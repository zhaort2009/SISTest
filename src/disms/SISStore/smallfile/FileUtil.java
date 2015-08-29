package disms.SISStore.smallfile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
/**
* ����Ŀ¼�µ��ļ�����������Ŀ¼����ָ��Ŀ¼������ͬ��Ŀ¼һ���ƹ�ȥ��
*
* @param targetFile
* @param path
*/
public static void copyFileFromDir(String targetDir, String path) {
	File file = new File(path);
	createFile(targetDir, false);
	if (file.isDirectory()) {
		copyFileToDir(targetDir, listFile(file));
}
}
/**
* ����Ŀ¼�µ��ļ�����������Ŀ¼����Ŀ¼��ֻ����Ŀ¼�µ��ļ�����ָ��Ŀ¼��
*
* @param targetDir
* @param path
*/
public static void copyFileOnly(String targetDir, String path) {
	File file = new File(path);
	File targetFile = new File(targetDir);
	if (file.isDirectory()) {
		File[] files = file.listFiles();
		for (File subFile : files) {
		if (subFile.isFile()) {
			copyFile(targetFile, subFile);
			}
		}
	}
}
/**
* ����Ŀ¼��ָ��Ŀ¼��targetDir��Ŀ��Ŀ¼��path��ԴĿ¼��
* �÷����Ὣpath�Լ�path�µ��ļ�����Ŀ¼ȫ�����Ƶ�Ŀ��Ŀ¼
*
* @param targetDir
* @param path
*/
public static void copyDir(String targetDir, String path) {
	File targetFile = new File(targetDir);
	createFile(targetFile, false);
	File file = new File(path);
	if (targetFile.isDirectory() && file.isDirectory()) {
		copyFileToDir(targetFile.getAbsolutePath() + "/" + file.getName(),
		listFile(file));
	}
}
/**
* ����һ���ļ���ָ��Ŀ¼��targetDir��Ŀ��Ŀ¼��filePath����Ҫ���Ƶ��ļ�·��
*
* @param targetDir
* @param filePath
*/
public static void copyFileToDir(String targetDir, String... filePath) {
	if (targetDir == null || "".equals(targetDir)) {
	System.out.println("��������Ŀ��·������Ϊ��");
	return;
	}
	File targetFile = new File(targetDir);
	if (!targetFile.exists()) {
	targetFile.mkdir();
	} else {
		if (!targetFile.isDirectory()) {
		System.out.println("��������Ŀ��·��ָ��Ĳ���һ��Ŀ¼��");
		return;
		}
	}
	for (String path : filePath) {
		File file = new File(path);
		if (file.isDirectory()) {
		copyFileToDir(targetDir + "/" + file.getName(), listFile(file));
		} else {
		copyFileToDir(targetDir, file, "");
		}
	}
}
/**
* �����ļ���ָ��Ŀ¼��targetDir��Ŀ��Ŀ¼��file��Դ�ļ�����newName�������������֡�
*
* @param targetFile
* @param file
* @param newName
*/
public static void copyFileToDir(String targetDir, File file, String newName) {
	String newFile = "";
	if (newName != null && !"".equals(newName)) {
	newFile = targetDir + "/" + newName;
	} else {
	newFile = targetDir + "/" + file.getName();
	}
	File tFile = new File(newFile);
	copyFile(tFile, file);
}
/**
* �����ļ���targetFileΪĿ���ļ���fileΪԴ�ļ�
*
* @param targetFile
* @param file
*/
public static void copyFile(File targetFile, File file) {
	if (targetFile.exists()) {
	System.out.println("�ļ�" + targetFile.getAbsolutePath()
	+ "�Ѿ����ڣ��������ļ���");
	return;
	} else {
	createFile(targetFile, true);
	}
	System.out.println("�����ļ�" + file.getAbsolutePath() + "��"
	+ targetFile.getAbsolutePath());
	try {
	InputStream is = new FileInputStream(file);
	FileOutputStream fos = new FileOutputStream(targetFile);
	byte[] buffer = new byte[1024];
	while (is.read(buffer) != -1) {
	fos.write(buffer);
	}
	is.close();
	fos.close();
	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
}
public static String[] listFile(File dir) {
	String absolutPath = dir.getAbsolutePath();
	String[] paths = dir.list();
	String[] files = new String[paths.length];
	for (int i = 0; i < paths.length; i++) {
	files[i] = absolutPath + "/" + paths[i];
	}
	return files;
}
public static void createFile(String path, boolean isFile){
	createFile(new File(path), isFile);
}
public static void createFile(File file, boolean isFile) {
	if (!file.exists()) {
			if (!file.getParentFile().exists()) {
			createFile(file.getParentFile(), false);
			} else {
				if (isFile) {
				try {
				file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				} else {
					file.mkdir();
				}
			}
		}
	}
}