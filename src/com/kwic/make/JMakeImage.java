package com.kwic.make;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.rendering.ImageType;
import org.dom4j.Element;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.kwic.exception.DefinedException;
import com.kwic.html.Html2Pdf;
import com.kwic.image.ImageConvert;
import com.kwic.license.KwicLicense;
import com.kwic.license.exception.LicenseException;
import com.kwic.parser.JXParser;
import com.kwic.util.CommonVariable;
import com.kwic.util.Json2Map;
import com.kwic.util.VersionUtil;

public class JMakeImage {
	private static JMakeImage instance;

	public JMakeImage(){
	}
	
	public static JMakeImage getInstance(){
		synchronized(JMakeImage.class){
			if(instance == null){
				instance	= new JMakeImage();
			}
			return instance;
		}
	}
	
	
	public Map<String,Object> make2File(String xmlPath, String pdfPath, String imgPath, String fileName) throws FileNotFoundException, ParseException, IOException, Exception {
		return make2File(xmlPath, pdfPath, imgPath, fileName, false);
	}
	
	public Map<String,Object> make2File(String xmlPath, String pdfPath, String imgPath, String fileName, boolean isMac) throws FileNotFoundException, ParseException, IOException, Exception {
		return make2File(xmlPath, pdfPath, imgPath, fileName, isMac, ImageType.BINARY);
	}
	
	public Map<String,Object> make2File(String xmlPath, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType type) throws FileNotFoundException, ParseException, IOException, Exception {
		return make2File(xmlPath, pdfPath, imgPath, fileName, isMac, type, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make2File(String xmlPath, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType type, int dpi) throws FileNotFoundException, ParseException, IOException, Exception {
		return make2File(xmlPath, pdfPath, imgPath, fileName, isMac, type, dpi, false);
	}
	
	public Map<String,Object> make2File(String xmlPath, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType type, int dpi, boolean delyn) throws FileNotFoundException, ParseException, IOException, Exception {
		if(!KwicLicense.getInstance().right(isMac))
			throw new LicenseException("Invalid license. Please contact your business representative.");
		if (!VALIDATE(xmlPath))
			throw new DefinedException("required input [xml path] is missing.");
		if (!VALIDATE(pdfPath))
			throw new DefinedException("required input [pdf Path] is missing.");
		if (!VALIDATE(imgPath))
			throw new DefinedException("required input [image path] is missing.");
		if (!VALIDATE(fileName))
			throw new DefinedException("required input [fileName] is missing.");
		
		String xml	= null;
		
		Map<String,Object> map	= new HashMap<String,Object>();
		
		BufferedReader reader	= null;
		String line				= null;
		StringBuffer sb			= new StringBuffer();
		String ln				= System.getProperty("line.separator");
		try {
			reader	= new BufferedReader(new FileReader(xmlPath));
			while ((line = reader.readLine()) != null)
				sb.append(line + ln);
			
			if(reader != null)
				reader.close();
			
			xml		= sb.toString();
			map		= JMakeImage.getInstance().make(
					xml,
					pdfPath,
					imgPath,
					fileName
					);
			
		}catch (FileNotFoundException e) {
			throw e;
		}catch(ParseException e){
			throw e;
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		
		return map;
	}
	
	public Map<String,Object> make2Pdf(String xml, String type, String path, String fileName) throws ParseException, IOException, Exception {
		if (!VALIDATE(xml))
			throw new DefinedException("required input [xml] is missing.");
		if (!VALIDATE(path))
			throw new DefinedException("required input [Path] is missing.");
		if (!VALIDATE(fileName))
			throw new DefinedException("required input [fileName] is missing.");
		
		Map<String,Object> map		= new HashMap<String,Object>();
		
		Map<String, Object> scopes 	= new HashMap<String, Object>();
		JSONParser json				= new JSONParser();
		
		MustacheFactory mf 			= null;
		Mustache mustache			= null;
		StringWriter writer			= null; 
		
		byte[] pdf			= null;
		
		String tml			= null;
		String data			= null;
		
		JXParser parser			= null;
		JXParser parser2		= null;
		Element field 			= null;
		
		String value		= null;
		String enc			= null;
		
		try{
			if (VersionUtil.isReneringVersion())
				System.setProperty(VersionUtil.RENDERING_NAME, VersionUtil.RENDERING_VALUE);
			
			parser		= new JXParser(xml);
			
			if (CommonVariable._TYPE_TML.equals(type.toUpperCase(Locale.KOREA))) {
				field 		= parser.getElement(parser.getRootElement(), "//tml");
				value		= parser.getAttribute(field, "Value");
				enc			= parser.getAttribute(field, "enc");
				
				tml		= decode(value, enc);
				
				data	= xml.substring(0, xml.indexOf("<tml")) + xml.substring(xml.indexOf("<format"));
				parser2	= new JXParser(data);
				
				Element[] targets	= parser.getElements("//format/target");
				for (int i = 0; i < targets.length; i++) {
					String name		= parser.getAttribute(targets[i], "name");
					String rule		= parser.getAttribute(targets[i], "rule");
					String tType	= parser.getAttribute(targets[i], "type");
					String src		= parser.getAttribute(targets[i], "src");
					String desc		= parser.getAttribute(targets[i], "desc");
					
					Element[] target	= parser2.getElements("//" + name);
					for (int j = 0; j < target.length; j++) {
						String result	= "";
						String val 		= parser2.getAttribute(target[j], "Value");
						
						if ("A".equals(tType)) {
							if (val.length() != rule.replaceAll("[^(#)]","").length()) {
								result	= val;
								continue;
							}
							
							int idx			= 0;
							char[] chr1		= val.toCharArray();
							char[] chr2		= rule.toCharArray();
							for (int k = 0; k < chr2.length; k++) {
								if (chr2[k] != '#') {
									result += chr2[k];
									continue;
								}
								result += chr1[idx];
								idx++;
							}
						} else if ("B".equals(tType)) {
							if ("".equals(val) || val.indexOf(",") >= 0)
								continue;
							result	= NumberFormat.getInstance().format(Double.parseDouble(val));
						} else if ("C".equals(tType)) {
							result	= val.replace(src, desc);
						} else if ("D".equals(tType)) {
							String[] split	= val.split("(?<=\\G.{" + Integer.parseInt(rule) + "})");
							result	= StringUtils.join(split, "<br>");
						}
						
						parser2.setAttribute(target[j], "Value", result);
					}
					
				}
				
				data	= parser2.toString(null);
				scopes	= Json2Map.json2Map((JSONObject) json.parse(XML.toJSONObject(data).toString()));
				
				mf		= new DefaultMustacheFactory();
				writer	= new StringWriter();
				
				mustache	= mf.compile(new StringReader(tml), "");
				mustache.execute(writer, scopes);
				
				if (writer != null)	
					writer.close();
				
				fileName	= fileName.indexOf(".") >= 0 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
				path		= path + File.separator + fileName + CommonVariable._PDF;
				
				boolean result	= Html2Pdf.makeHTML2PDF(
						path, 
						writer.toString()
						);
				
				map.put("RESULT", result?"Y":"N");
				
			} else if (CommonVariable._TYPE_PDF.equals(type)) {
				field 		= parser.getElement(parser.getRootElement(), "//PDF");
				value		= field.getText();
				
				pdf	= Base64.getDecoder().decode(value.getBytes());
				
				File file	= null;
				FileOutputStream fos	= null;
				try{
					file	= new File(path + File.separator + fileName + ".pdf");
					fos		= new FileOutputStream(file);
					
					fos.write(pdf);
					fos.flush();
					fos.close();
					
					map.put("RESULT", "Y");
				}catch(Exception e){
					throw e;
				}
			}
			
		}catch(ParseException e){
			throw e;
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		
		return map;
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName, boolean isMac) throws ParseException, IOException, Exception {
		return make(xml, pdfPath, imgPath, fileName, isMac, ImageType.BINARY, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType type, int dpi) throws ParseException, IOException, Exception {
		return make(xml, pdfPath, imgPath, fileName, isMac, type, dpi, false);
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName) throws ParseException, IOException, Exception {
		return make(xml, pdfPath, imgPath, fileName, ImageType.BINARY, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName, ImageType type) throws ParseException, IOException, Exception {
		return make(xml, pdfPath, imgPath, fileName, type, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName, int dpi) throws ParseException, IOException, Exception {
		return make(xml, pdfPath, imgPath, fileName, ImageType.BINARY, dpi);
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName, ImageType type, int dpi) throws ParseException, IOException, Exception {
		return make(xml, pdfPath, imgPath, fileName, false, type, dpi, false);
	}
	
	public Map<String,Object> make(String xml, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType imageType, int dpi, boolean delyn) throws ParseException, IOException, Exception {
		if(!KwicLicense.getInstance().right(isMac))
			throw new LicenseException("Invalid license. Please contact your business representative.");
		if (!VALIDATE(xml))
			throw new DefinedException("required input [xml] is missing.");
		if (!VALIDATE(pdfPath))
			throw new DefinedException("required input [pdf Path] is missing.");
		if (!VALIDATE(imgPath))
			throw new DefinedException("required input [image path] is missing.");
		if (!VALIDATE(fileName))
			throw new DefinedException("required input [fileName] is missing.");
		
		Map<String,Object> map		= new HashMap<String,Object>();
		
		Map<String, Object> scopes 	= new HashMap<String, Object>();
		JSONParser json				= new JSONParser();
		
		MustacheFactory mf 			= null;
		Mustache mustache			= null;
		StringWriter writer			= null; 
		
		byte[] pdf			= null;
		
		String tml			= null;
		String data			= null;
		
		JXParser parser			= null;
		JXParser parser2		= null;
		Element field 			= null;
		
		String value		= null;
		String enc			= null;
		String type			= null;
		
		try{
			if (VersionUtil.isReneringVersion())
				System.setProperty(VersionUtil.RENDERING_NAME, VersionUtil.RENDERING_VALUE);
			
			parser		= new JXParser(xml);
			
			field 		= parser.getElement(parser.getRootElement(), "//tml");
			
			value		= parser.getAttribute(field, "Value");
			enc			= parser.getAttribute(field, "enc");
			type		= parser.getAttribute(field, "type");
			
			if (CommonVariable._TYPE_TML.equals(type.toUpperCase(Locale.KOREA))) {
				tml		= decode(value, enc);
				
				data	= xml.substring(0, xml.indexOf("<tml")) + xml.substring(xml.indexOf("<format"));
				parser2	= new JXParser(data);
				
				Element[] targets	= parser.getElements("//format/target");
				for (int i = 0; i < targets.length; i++) {
					String name		= parser.getAttribute(targets[i], "name");
					String rule		= parser.getAttribute(targets[i], "rule");
					String tType	= parser.getAttribute(targets[i], "type");
					String src		= parser.getAttribute(targets[i], "src");
					String desc		= parser.getAttribute(targets[i], "desc");
					
					Element[] target	= parser2.getElements("//" + name);
					for (int j = 0; j < target.length; j++) {
						String result	= "";
						String val 		= parser2.getAttribute(target[j], "Value");
						
						if ("A".equals(tType)) {
							if (val.length() != rule.replaceAll("[^(#)]","").length()) {
								result	= val;
								continue;
							}
							
							int idx			= 0;
							char[] chr1		= val.toCharArray();
							char[] chr2		= rule.toCharArray();
							for (int k = 0; k < chr2.length; k++) {
								if (chr2[k] != '#') {
									result += chr2[k];
									continue;
								}
								result += chr1[idx];
								idx++;
							}
						} else if ("B".equals(tType)) {
							if ("".equals(val) || val.indexOf(",") >= 0)
								continue;
							result	= NumberFormat.getInstance().format(Double.parseDouble(val));
						} else if ("C".equals(tType)) {
							result	= val.replace(src, desc);
						} else if ("D".equals(tType)) {
							String[] split	= val.split("(?<=\\G.{" + Integer.parseInt(rule) + "})");
							result	= StringUtils.join(split, "<br>");
						}
						
						parser2.setAttribute(target[j], "Value", result);
					}
					
				}
				
				data	= parser2.toString(null);
				scopes	= Json2Map.json2Map((JSONObject) json.parse(XML.toJSONObject(data).toString()));
				
				mf		= new DefaultMustacheFactory();
				writer	= new StringWriter();
				
				mustache	= mf.compile(new StringReader(tml), "");
				mustache.execute(writer, scopes);
				
				if (writer != null)	
					writer.close();
				
				fileName	= fileName.indexOf(".") >= 0 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
				pdfPath		= pdfPath + File.separator + fileName + CommonVariable._PDF;
				imgPath		= imgPath + File.separator + fileName + CommonVariable._TIF;
				
				boolean yn	= Html2Pdf.makeHTML2PDF(
						pdfPath, 
						writer.toString()
						);
				
				if (yn) {
					map	= ImageConvert.convertTiff(
							imgPath, 
							pdfPath,
							imageType,
							dpi,
							delyn
							);
				}
				
			} else if (CommonVariable._TYPE_PDF.equals(type)) {
				pdf	= Base64.getDecoder().decode(value.getBytes());
				
				File file	= null;
				FileOutputStream fos	= null;
				try{
					file	= new File(pdfPath + File.separator + fileName + ".pdf");
					fos		= new FileOutputStream(file);
					
					fos.write(pdf);
					fos.flush();
					fos.close();
					
					map	= ImageConvert.convertTiff(
							imgPath + File.separator + fileName + ".tif", 
							pdfPath + File.separator + fileName + ".pdf",
							ImageType.RGB,
							60
							);
					
				}catch(Exception e){
					throw e;
				}
			}
			
		}catch(ParseException e){
			throw e;
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		
		return map;
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName, boolean isMac) throws ParseException, IOException, Exception {
		return make2Image(tml, data, pdfPath, imgPath, fileName, isMac, ImageType.BINARY, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName) throws ParseException, IOException, Exception {
		return make2Image(tml, data, pdfPath, imgPath, fileName, ImageType.BINARY, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName, ImageType type) throws ParseException, IOException, Exception {
		return make2Image(tml, data, pdfPath, imgPath, fileName, type, CommonVariable.IMG_DPI_300);
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName, int dpi) throws ParseException, IOException, Exception {
		return make2Image(tml, data, pdfPath, imgPath, fileName, ImageType.BINARY, dpi);
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName, ImageType type, int dpi) throws ParseException, IOException, Exception {
		return make2Image(tml, data, pdfPath, imgPath, fileName, false, type, dpi, false);
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType type, int dpi) throws ParseException, IOException, Exception {
		return make2Image(tml, data, pdfPath, imgPath, fileName, isMac, type, dpi, false);
	}
	
	public Map<String,Object> make2Image(String tml, String data, String pdfPath, String imgPath, String fileName, boolean isMac, ImageType imageType, int dpi, boolean delyn) throws ParseException, IOException, Exception {
		if(!KwicLicense.getInstance().right(isMac))
			throw new LicenseException("Invalid license. Please contact your business representative.");
		if (!VALIDATE(tml))
			throw new DefinedException("required input [tml] is missing.");
		if (!VALIDATE(data))
			throw new DefinedException("required input [data] is missing.");
		if (!VALIDATE(pdfPath))
			throw new DefinedException("required input [pdf Path] is missing.");
		if (!VALIDATE(imgPath))
			throw new DefinedException("required input [image path] is missing.");
		if (!VALIDATE(fileName))
			throw new DefinedException("required input [fileName] is missing.");
		
		Map<String,Object> map		= new HashMap<String,Object>();
		
		Map<String, Object> scopes 	= new HashMap<String, Object>();
		JSONParser json				= new JSONParser();
		
		MustacheFactory mf 			= null;
		Mustache mustache			= null;
		StringWriter writer			= null; 
		
		try{
			if (VersionUtil.isReneringVersion())
				System.setProperty(VersionUtil.RENDERING_NAME, VersionUtil.RENDERING_VALUE);
			
			if (CommonVariable._TYPE_XML.equals(getDataType(data))) {
				data	= XML.toJSONObject(parseXML(data)).toString();
			} else if (CommonVariable._TYPE_JSON.equals(getDataType(data))) {
				data	= XML.toJSONObject(parseJSON(data)).toString();
			} else {
				throw new Exception("The data type is not in XML or JSON format.");
			}
			
			scopes	= Json2Map.json2Map((JSONObject) json.parse(data));
			
			mf		= new DefaultMustacheFactory();
			writer	= new StringWriter();
			
			mustache	= mf.compile(new StringReader(tml), "");
			mustache.execute(writer, scopes);
			
			if (writer != null)	
				writer.close();
			
			fileName	= fileName.indexOf(".") >= 0 ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
			pdfPath		= pdfPath + File.separator + fileName + CommonVariable._PDF;
			imgPath		= imgPath + File.separator + fileName + CommonVariable._TIF;
			
			boolean yn	= Html2Pdf.makeHTML2PDF(
					pdfPath, 
					writer.toString()
					);
			
			if (yn) {
				map	= ImageConvert.convertTiff(
						imgPath, 
						pdfPath,
						imageType,
						dpi,
						delyn
						);
			}
			
		}catch(ParseException e){
			throw e;
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw e;
		}
		
		return map;
	}
	
	private boolean VALIDATE(String val){
		if (val == null || "".equals(val))
			return false;
		return true;
	}
	
	private String decode(String enc, String type){
		byte[] bytes		= null;
		if (type.equals(CommonVariable._BASE64)) {
			bytes		= Base64.getDecoder().decode(enc.getBytes());
		}
		
		return new String(bytes);
	}
	
	private String getDataType(String data) throws Exception {
		String type		= null;
		if (data.trim().startsWith("<")) {
			type	= CommonVariable._TYPE_XML;
		} else if (data.trim().startsWith("{")) {
			type	=  CommonVariable._TYPE_JSON;
		}
		return type;
	}
	
	private String parseXML(String data) throws Exception {
		JXParser parser			= null;
		try {
			parser		= new JXParser(data);
			
			Element[] targets	= parser.getElements("//format/target");
			for (int i = 0; i < targets.length; i++) {
				String name		= parser.getAttribute(targets[i], "name");
				String rule		= parser.getAttribute(targets[i], "rule");
				String tType	= parser.getAttribute(targets[i], "type");
				String src		= parser.getAttribute(targets[i], "src");
				String desc		= parser.getAttribute(targets[i], "desc");
				
				Element[] target	= parser.getElements("//" + name);
				for (int j = 0; j < target.length; j++) {
					String result	= "";
					String val 		= parser.getAttribute(target[j], "Value");
					
					if ("A".equals(tType)) {
						if (val.length() != rule.replaceAll("[^(#)]","").length()) {
							result	= val;
							continue;
						}
						
						int idx			= 0;
						char[] chr1		= val.toCharArray();
						char[] chr2		= rule.toCharArray();
						for (int k = 0; k < chr2.length; k++) {
							if (chr2[k] != '#') {
								result += chr2[k];
								continue;
							}
							result += chr1[idx];
							idx++;
						}
					} else if ("B".equals(tType)) {
						if ("".equals(val) || val.indexOf(",") >= 0)
							continue;
						result	= NumberFormat.getInstance().format(Double.parseDouble(val));
					} else if ("C".equals(tType)) {
						result	= val.replace(src, desc);
					} else if ("D".equals(tType)) {
						String[] split	= val.split("(?<=\\G.{" + Integer.parseInt(rule) + "})");
						result	= StringUtils.join(split, "<br>");
					}
					
					parser.setAttribute(target[j], "Value", result);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		
		return parser.toString(null);
	}
	
	private String parseJSON(String data) throws Exception {
		JXParser parser		= null;
		try {
			org.json.JSONObject obj	= new org.json.JSONObject(data);
			String xml		= XML.toString(obj);
			
			data			= "<?xml version=\"1.0\"?>" + xml;
			
			parser			= new JXParser(data);
			
			Element[] targets	= parser.getElements("//format/target");
			for (int i = 0; i < targets.length; i++) {
				Element nameElement		= parser.getElement(targets[i], "name");  
				Element ruleElement		= parser.getElement(targets[i], "rule");  
				Element tTypeElement	= parser.getElement(targets[i], "type");  
				Element srcElement		= parser.getElement(targets[i], "src");  
				Element descElement		= parser.getElement(targets[i], "desc");
				
				String name		= nameElement != null ? nameElement.getText() : null;
				String rule		= ruleElement != null ? ruleElement.getText() : null;
				String tType	= tTypeElement != null ? tTypeElement.getText() : null;
				String src		= srcElement != null ? srcElement.getText() : null;
				String desc		= descElement != null ? descElement.getText() : null;
				
				Element[] target	= parser.getElements("//" + name);
				for (int j = 0; j < target.length; j++) {
					String result	= "";
					
					if (parser.getElement(target[j], "Value") == null)
						continue;
					
					String val 		= parser.getElement(target[j], "Value").getText();
					if ("A".equals(tType)) {
						if (val.length() != rule.replaceAll("[^(#)]","").length()) {
							result	= val;
							continue;
						}
						
						int idx			= 0;
						char[] chr1		= val.toCharArray();
						char[] chr2		= rule.toCharArray();
						for (int k = 0; k < chr2.length; k++) {
							if (chr2[k] != '#') {
								result += chr2[k];
								continue;
							}
							result += chr1[idx];
							idx++;
						}
					} else if ("B".equals(tType)) {
						if ("".equals(val) || val.indexOf(",") >= 0)
							continue;
						result	= NumberFormat.getInstance().format(Double.parseDouble(val));
					} else if ("C".equals(tType)) {
						result	= val.replace(src, desc);
					} else if ("D".equals(tType)) {
						String[] split	= val.split("(?<=\\G.{" + Integer.parseInt(rule) + "})");
						result	= StringUtils.join(split, "<br>");
					}
					parser.getElement(target[j], "Value").setText(result);
				}
			}
			
		} catch (Exception e) {
			throw e;
		}
		
		return parser.toString(null);
	}
}
