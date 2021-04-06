package com.kwic.html;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.kwic.io.IoUtil;
import com.kwic.util.CommonVariable;
import com.kwic.util.StringUtil;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

public class Html2Pdf {
	public static boolean makeHTML2PDF(String pdfPath, String htmlString) throws Exception {
		return makeHTML2PDF(pdfPath, htmlString, true);
	}
	
	public static boolean makeHTML2PDF(String pdfPath, String htmlString, boolean bold) throws FileNotFoundException, DocumentException, IOException, Exception {
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
			html	= StringUtil.replace(html, "&lt;br","<br");
			html	= StringUtil.replace(html, "br&gt;", "br/>");
			html	= StringUtil.replace(html, "<br>", "<br/>");
			html	= StringUtil.replace(html, "&lt;del&gt;", "<del>");
			html	= StringUtil.replace(html, "&lt;/del&gt;", "</del>");
			
			try{
				renderer = new ITextRenderer();
				
				renderer.getFontResolver().addFont(CommonVariable._FONT_RESOURCE_PATH, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				if (bold)
					renderer.getFontResolver().addFont(CommonVariable._FONT_BOLD_RESOURCE_PATH, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				
				System.out.println("html :: "+html);
				
				renderer.setDocumentFromString(html);
				renderer.layout();
				renderer.createPDF(os);
				
				os.close();
			}catch(Exception ex){
				if (os!=null){os.close();}
				if (new File(pdfPath).exists()){new File(pdfPath).delete();}
				throw ex;
			}
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
}
