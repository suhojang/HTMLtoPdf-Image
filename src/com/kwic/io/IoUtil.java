package com.kwic.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * /**
 * <pre>
 * Title		: IoUtil
 * Description	: IoUtil
 * Copyright	: Copyright	(c)	2019
 * Company		: kwic
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @version	1.0
 * @since 1.0
 */
public class IoUtil {
	public static final void fileWrite(String path,byte[] bytes) throws Exception{
		FileOutputStream fos	= null;
		try{
			makFolder(path);
			
			fos	= new FileOutputStream(new File(path));
			fos.write(bytes);
			fos.flush();
			
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fos!=null)fos.close();}catch(Exception ex){}
		}
	}
	
	public static final void copyFile(String opath,String npath) throws Exception{
		FileInputStream fis		= null;
		FileOutputStream fos	= null;
		byte[] bytes	= new byte[1024];
		int size	= -1;
		try{
			makFolder(npath);
			
			fis	= new FileInputStream(new File(opath));
			fos	= new FileOutputStream(new File(npath));
			
			while( (size=fis.read(bytes))>=0 ){
				fos.write(bytes, 0, size);
				fos.flush();
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fis!=null)fis.close();}catch(Exception ex){}
			try{if(fos!=null)fos.close();}catch(Exception ex){}
		}
	}
	
	public static final void copyFile(InputStream input, String npath) throws Exception{
		FileOutputStream fos	= null;
		byte[] bytes	= new byte[1024];
		int size	= -1;
		try{
			makFolder(npath);
			
			fos	= new FileOutputStream(new File(npath));
			
			while( (size=input.read(bytes))>=0 ){
				fos.write(bytes, 0, size);
				fos.flush();
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fos!=null)fos.close();}catch(Exception ex){}
		}
	}
	
	public synchronized static void makFolder(String path) throws Exception{
		if(!new File(path).getParentFile().exists())
			new File(path).getParentFile().mkdirs();
	}
}
