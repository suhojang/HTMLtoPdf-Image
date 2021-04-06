package com.kwic.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import com.kwic.io.IoUtil;
import com.kwic.util.CommonVariable;

public class ImageConvert {
	public static Map<String,Object> convertTiff(String imgPath, String pdfPath) throws IOException, Exception {
		return convertTiff(imgPath, pdfPath, ImageType.BINARY);
	}
	
	public static Map<String,Object> convertTiff(String imgPath, String pdfPath, ImageType type) throws IOException, Exception {
		return convertTiff(imgPath, pdfPath, type, CommonVariable.IMG_DPI_300);
	}
	
	public static Map<String,Object> convertTiff(String imgPath, String pdfPath, ImageType type, int dpi) throws IOException, Exception {
		return convertTiff(imgPath, pdfPath, type, dpi, false);
	}
	
	public static Map<String,Object> convertTiff(String imgPath, String pdfPath, ImageType type, int dpi, boolean delyn) throws IOException, Exception {
		Map<String,Object> map	= new HashMap<String,Object>();

		PDDocument pDoc			= null;
		File image				= null;
		PDFRenderer renderer	= null;
		BufferedImage bim		= null;
		
		String ext		= null;
		String iPath	= null;
		String fileName	= null;
				
		try{
			IoUtil.makFolder(imgPath);
			
			image		= new File(imgPath);
			pDoc		= PDDocument.load(new File(pdfPath));
			renderer	= new PDFRenderer(pDoc);
			
			map.put("type", 	String.valueOf(type));
			map.put("dpi", 		dpi);
			map.put("delyn", 	delyn);
			map.put("page", 	pDoc.getNumberOfPages());
			
	    	for (int i = 0; i < pDoc.getNumberOfPages(); i++) {
	    		bim		= renderer.renderImageWithDPI(i, dpi, type);
				
				ext		= image.getName().substring(image.getName().lastIndexOf(".") + 1);
				iPath	= image.getAbsolutePath().substring(0, image.getAbsolutePath().lastIndexOf("."));
				
				fileName = iPath + "-" +(i + 1) + "." + ext;
				
				ImageIOUtil.writeImage(bim, fileName, dpi);
				
				map.put("file-" + (i + 1), fileName);
			}
			
	    	pDoc.close();
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw e;
		}finally{
			try{ if(delyn){if (new File(pdfPath).exists()) new File(pdfPath).delete();} }catch(Exception e){throw e;}
		}
		
		return map;
	}
	
	public static Map<String,Object> convertTiff(String imgPath, String pdfPath, ImageType type, int dpi, int wDpi, int hDpi, boolean delyn, int width, int height) throws IOException, Exception {
		Map<String,Object> map	= new HashMap<String,Object>();

		PDDocument pDoc			= null;
		File image				= null;
		PDFRenderer renderer	= null;
		BufferedImage bim		= null;
		
		String ext		= null;
		String iPath	= null;
		String fileName	= null;
		
		try{
			IoUtil.makFolder(imgPath);
			
			image		= new File(imgPath);
			pDoc		= PDDocument.load(new File(pdfPath));
			renderer	= new PDFRenderer(pDoc);
			
			map.put("type", 	String.valueOf(type));
			map.put("dpi", 		dpi);
			map.put("wDpi", 	wDpi);
			map.put("hDpi", 	hDpi);
			map.put("delyn", 	delyn);
			map.put("width", 	width);
			map.put("height", 	height);
			map.put("page", 	pDoc.getNumberOfPages());
			
	    	for (int i = 0; i < pDoc.getNumberOfPages(); i++) {
	    		bim		= renderer.renderImageWithDPI(i, dpi, type);
				
				ext		= image.getName().substring(image.getName().lastIndexOf(".") + 1);
				iPath	= image.getAbsolutePath().substring(0, image.getAbsolutePath().lastIndexOf("."));
				
				fileName = iPath + "-" +(i + 1) + "." + ext;
				
				ImageRender.writeImage(bim, ImageRender._TIF, new FileOutputStream(new File(fileName)), wDpi, hDpi, width, height, 1.0F, "");
				
				map.put("file-" + (i + 1), fileName);
			}
			
	    	pDoc.close();
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw e;
		}finally{
			try{ 
				if(delyn){
					if (new File(pdfPath).exists()) new File(pdfPath).delete();
				}else{
					moveFile(new File(pdfPath).getAbsolutePath(), new File(pdfPath).getParentFile().toString() + File.separator + "backup" + File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date()) + File.separator + new File(pdfPath).getName());
				}
			}catch(Exception e){
				throw e;
			}
		}
		
		return map;
	}
	
	private static final void moveFile(String src, String dest) throws Exception{
		File file		= null;
		File fileToMove	= null;
		try {
			IoUtil.makFolder(dest);
			
			file		= new File(src);
			fileToMove	= new File(dest);
			
			file.renameTo(fileToMove);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
