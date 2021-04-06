package com.kwic.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.kwic.io.IoUtil;
import com.kwic.util.StringUtil;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class Html2Pdf2 {
	/**
	 * html 문자열을 pdf파일로 변환한다
	 * itext 7이상 버전에서 사용
	 * 
	 * @param pdfPath
	 * @param htmlString
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean makeHTMLtoPDF(String pdfPath, String htmlString) throws FileNotFoundException, IOException {
		boolean result		= false;

		/*ConverterProperties properties	= null;
		FontProvider dfp 				= null;
		
		try {
			properties 	= new ConverterProperties();
			dfp 		= new DefaultFontProvider(true, false, false);
			
			dfp.addFont(CommonVariable._FONT_RESOURCE_PATH);
			dfp.addFont(CommonVariable._FONT_BOLD_RESOURCE_PATH);
			properties.setFontProvider(dfp);
			
			HtmlConverter.convertToPdf(htmlString, new FileOutputStream(pdfPath), properties);
			
			result	= true;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}*/
		
		return result;
	}
	
	public static boolean makeHTML2PDF(String pdfPath, String htmlString, String fontPath) throws Exception {
		return makeHTML2PDF(pdfPath, htmlString, fontPath, true);
	}
	
	public static boolean makeHTML2PDF(String pdfPath, String htmlString, String fontPath, boolean bold) throws FileNotFoundException, DocumentException, IOException, Exception {
		boolean result		= false;
		
		OutputStream os		= null;
		String html			= null;
		
		ITextRenderer renderer = null;
		
		try {
			IoUtil.makFolder(pdfPath);
			
			os = new FileOutputStream(pdfPath);

			html	= StringUtil.replace(htmlString,"<img","<JPV-IMG");
			html	= StringUtil.replace(html,"<img","<JPV-IMG");
			
			String[] checkHTMLTag	= new String[]{
					"JPV-IMG"
					,"input"
					,"link"
					,"meta"
			};
			
			StringBuffer sb	= new StringBuffer().append(html);
			for (int i = 0; i < checkHTMLTag.length; i++) {
				int idx		= -1;
				int idx2	= -1;
				while((idx=sb.indexOf("<"+checkHTMLTag[i], idx+1))>=0){
					idx2	= sb.indexOf(">",idx);
					if(idx2<0)
						break;
					if(!"/".equals(sb.substring(idx2-1,idx2))){
						sb.insert(idx2, '/');
					}
				}
			}
			
			html	= sb.toString();
			html	= StringUtil.replace(html, "<JPV-IMG", "<img");
			html	= StringUtil.replace(html, "<col>", "<col/>");
			html	= StringUtil.replace(html, "></img>", ">");
			html	= StringUtil.replace(html, "></col>", ">");
			html	= StringUtil.replace(html, "&nbsp;", "&#160;");
			html	= StringUtil.replace(html, "<br>", "<br/>");
			
			renderer = new ITextRenderer();
			
			renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			if (bold) {
				renderer.getFontResolver().addFont(
						fontPath.substring(0, fontPath.lastIndexOf(".")) + "-bold" + fontPath.substring(fontPath.lastIndexOf(".")) 
						,BaseFont.IDENTITY_H
						,BaseFont.NOT_EMBEDDED
						);
			}
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(os);
		
			os.close();
			
			result	= true;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (DocumentException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
		return result;
	}
	
	public boolean reWritePDF(String src, String dest, int pageNo){
		Document document		= new Document(PageSize.A4);
	    PdfWriter writer 		= null;
	    PdfReader reader		= null;
	    PdfContentByte cb		= null;
	    PdfImportedPage pPage 	= null;
	    FileInputStream is		= null;
	    
	    try{
			is		= new FileInputStream(new File(src));
			reader	= new PdfReader(is);
			
	        writer	= PdfWriter.getInstance(document, new FileOutputStream(dest));
	        writer.setStrictImageSequence(true);
	        Rectangle pageSize = reader.getPageSizeWithRotation(1);
		    document.open();
		    
		    cb		= writer.getDirectContent();
		    
			for(int i=1;i<=reader.getNumberOfPages();i++){
				pPage	= writer.getImportedPage(reader, i);
				if (i==pageNo) {
					document.setPageSize(pageSize.rotate());
					Image image	= Image.getInstance(pPage);
					image.setRotationDegrees(270);
					image.setAbsolutePosition(0, 0);
					document.add(image);
				} else {
					document.setPageSize(PageSize.A4);
					document.newPage();
					cb.addTemplate(pPage, 0, 0);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{if(document!=null)document.close();}catch(Exception e){}
			try{if(reader!=null)reader.close();}catch(Exception e){}
			try{if(writer!=null)writer.close();}catch(Exception e){}
			try{if(is!=null)is.close();}catch(Exception e){}
		}
		
		return false;
	}
}
