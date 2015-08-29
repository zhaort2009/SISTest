package disms.SISStore.smallfile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.apache.commons.io.FileUtils;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Durability;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.apache.commons.io.FileUtils;   
import org.apache.commons.io.FilenameUtils;   

public class SISProcess{
	Environment myDbEnvironment = null;
	Database myDatabase = null;
	EnvironmentConfig envConfig = null;
	DatabaseConfig dbConfig = null;
	String filePath = new String();
	String srcDB = "D:/SIS/dbEnv";
	String desDB = "E:/SIS/dbEnv";
	boolean exist = false;
	public void createDB(String dbPath){
			try {    
			    // Open the environment. Create it if it does not already exist.    
			    envConfig = new EnvironmentConfig();    
			    envConfig.setAllowCreate(true); 
			    envConfig.setTransactional(true);
			    envConfig.setDurability(Durability.COMMIT_SYNC);
			    myDbEnvironment = new Environment(new File(dbPath), envConfig);    
			    
			    // Open the database. Create it if it does not already exist.    
			    dbConfig = new DatabaseConfig();    
			    dbConfig.setAllowCreate(true); 

			    myDatabase = myDbEnvironment.openDatabase(null,"sampleDatabase", dbConfig); 
			} 
			catch (DatabaseException dbe) {    // Exception handling goes here}
			}
	}
	public void closeDB(){
			try {
		        if (myDatabase != null) {
		            myDatabase.close();
		        }
		        if (myDbEnvironment != null) {
		            myDbEnvironment.close();
		        }
		} catch (DatabaseException dbe) {
		    // ¥ÌŒÛ¥¶¿Ì
		}
	}
	public void cursorDB(){
		Cursor cursor = null;
		 // Open the cursor. 
		try {

	    cursor = myDatabase.openCursor(null, null);

	    // Cursors need a pair of DatabaseEntry objects to operate. These hold
	    // the key and data found at any given position in the database.
	    DatabaseEntry foundKey = new DatabaseEntry();
	    DatabaseEntry foundData = new DatabaseEntry();

	    // To iterate, just call getNext() until the last database record has been 
	    // read. All cursor operations return an OperationStatus, so just read 
	    // until we no longer see OperationStatus.SUCCESS
	    while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) ==
	        OperationStatus.SUCCESS) {
	        // getData() on the DatabaseEntry objects returns the byte array
	        // held by that object. We use this to get a String value. If the
	        // DatabaseEntry held a byte array representation of some other data
	        // type (such as a complex object) then this operation would look 
	        // considerably different.
	        String keyString = new String(foundKey.getData());
	        String dataString = new String(foundData.getData());
	        deletePairs(keyString);
	        System.out.println("Key - Data : " + keyString + " - " + dataString + "");
	    }
	} catch (DatabaseException de) {
	    System.err.println("Error accessing database." + de);
	} finally {
	    // Cursors must be closed.
	    cursor.close();
	}
	}
	public void addElement(String key,String data){
		String aKey = key;
		String aData = data;
		try {
		DatabaseEntry theKey = new DatabaseEntry(aKey.getBytes("UTF-8"));
		DatabaseEntry theData = new DatabaseEntry(aData.getBytes("UTF-8"));
		myDatabase.put(null, theKey, theData);
		} catch (Exception e) {
		// Exception handling goes here
		}
		
	}
	public void deletePairs(String key){
		try {
		    String aKey = key;
		    DatabaseEntry theKey = new DatabaseEntry(aKey.getBytes("UTF-8"));
		    
		    // Perform the deletion. All records that use this key are
		    // deleted.
		    myDatabase.delete(null, theKey); 
		    System.out.println("delete successfully!");
		} catch (Exception e) {
		    // Exception handling goes here
		}
	}
	public boolean getValue(String key){
		 try { 
		      // Create a pair of DatabaseEntry objects. theKey
		      // is used to perform the search. theData is used 
		      // to store the data returned by the get() operation.
		     DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
		     DatabaseEntry theData = new DatabaseEntry();
		      
		     // Perform the get.
		      if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) { 
		            // Recreate the data String.
		             byte[] retData = theData.getData();
		             String foundData = new String(retData);
//		             System.out.println("For key: '" + key + "' found data: '" + foundData + "'.");
		             return true;
		      } else {
//		          System.out.println("No record found for key '" + key + "'."); 
		          return  false;
		     }
		  } catch (Exception e) {
			  // Exception handling goes here}
			  return false;
		  }
	}
	public void chooseFile(Vector fs){
		JFileChooser jfc = new JFileChooser("E:\\linux‘¥¬Î");
		jfc.setMultiSelectionEnabled(true);
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.showOpenDialog(new javax.swing.JFrame());
		File[] sf = jfc.getSelectedFiles();
		for(int i = 0; i < sf.length;i++ ){
			if(!sf[i].isDirectory())
				fs.addElement(sf[i]);
			else
				fileList(sf[i],fs);
		}
		System.out.println(fs);
	}
	public void fileList(File file,Vector vt) {
        File[] files = file.listFiles();
        if (files != null) {
              for (File f : files) {
            	  if(!f.isDirectory())
//                    System.out.println(f.getPath());
            	  	vt.addElement(f.getPath());
                    fileList(f,vt);
              }
        }
   }
	
	public static void main(String[] args) throws NoSuchAlgorithmException, DigestException, IOException{
		Vector fs = new Vector();
		SISProcess sp = new SISProcess();
		sp.chooseFile(fs);
		for(int i = 0;i< fs.size();i++){			
			String srcFile = fs.elementAt(i).toString();
			File src = new File(srcFile);
			long srcSize = src.length();
			String desFile = "E:/SIS/ALL.txt";
			File des = new File(desFile);
			long desSize = des.length();
			String location = String.valueOf(desSize) + '*' +String.valueOf(srcSize);
			String srcDB = "D:/SIS/dbEnv";
			String desDB = "E:/SIS/dbEnv";
			CalHash ch = new CalHash();
			byte[] hash = ch.calcuMD5(src);
			String hashStr = ch.calHexString(hash);
			System.out.println(hashStr);
			String uuid = UUID.randomUUID().toString();
			String luid = UUID.randomUUID().toString();
			sp.createDB(srcDB);
			sp.addElement(luid, hashStr);
			sp.closeDB();
			sp.createDB(desDB);
			if(!sp.getValue(hashStr)){
				sp.addElement(hashStr, location);
				try {		            
		            byte[] bytes=FileUtils.readFileToByteArray(src); 
		            FileOutputStream writer = new FileOutputStream(des, true);   
		            writer.write(bytes);   
		            writer.close();   
	
		        } catch (IOException e) {   
		            e.printStackTrace();   
		        }	
			}
			sp.closeDB();	
		}
	/*	SISProcess sp = new SISProcess();
		String srcDB = "D:/SIS/dbEnv";
		String desDB = "E:/SIS/dbEnv";
		sp.createDB(srcDB);
		sp.cursorDB();
		sp.closeDB();
		sp.createDB(desDB);
		sp.cursorDB();
		sp.closeDB();*/	
}
}