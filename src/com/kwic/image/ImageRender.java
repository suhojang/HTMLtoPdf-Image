package com.kwic.image;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

public class ImageRender {
	public static final String _TIF		= "tif";
	public static final String _JPEG	= "jpeg";
	public static final String _JPG		= "jpg";
	
	private static final String COMPRESS_CCITT_T6	= "CCITT T.6";
	private static final String COMPRESS_LZW		= "LZW";
	
	private static final String _TIFFIFD			= "TIFFIFD";
	
	private static final String _META_TIFFField			= "TIFFField";
	private static final String _META_TIFFRationals		= "TIFFRationals";
	private static final String _META_TIFFRational		= "TIFFRational";
	private static final String _META_TIFFShorts		= "TIFFShorts";
	private static final String _META_TIFFShort			= "TIFFShort";
	private static final String _META_TIFFLongs			= "TIFFLongs";
	private static final String _META_TIFFLong			= "TIFFLong";
	private static final String _META_TIFFAsciis		= "TIFFAsciis";
	private static final String _META_TIFFAscii			= "TIFFAscii";
	
	private static final String _META_NUMBER		= "number";
	private static final String _META_NAME			= "name";
	private static final String _NODE_VAL			= "value";
	
	private static final String _SOFTWARE			= "KWIC";
	
	public static boolean writeImage(BufferedImage image, String formatName, OutputStream output, int wDpi, int hDpi, int width, int height, float compressionQuality, String compressionType) throws IOException {
		Image tmp			= null;
		BufferedImage dimg	= null;
		Graphics2D g2d		= null;
		try {
			tmp		= image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			dimg	= new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
			
			g2d		= dimg.createGraphics();
			g2d.drawImage(tmp, 0, 0, null);
			g2d.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return writeImage(dimg, formatName, output, wDpi, hDpi, compressionQuality, compressionType);
	}
	
	private static boolean writeImage(BufferedImage image, String formatName, OutputStream output, int wDpi, int hDpi, float compressionQuality, String compressionType) throws IOException {
		ImageOutputStream imageOutput 	= null;
		ImageWriter writer 				= null;
		Iterator<ImageWriter> writers 	= null; 
		try {
			ImageWriteParam param	= null;
			IIOMetadata metadata	= null;
			
			writers = ImageIO.getImageWritersByFormatName(formatName);
			
			while (writers.hasNext()){
				if (writer != null)
					writer.dispose();

				writer	= (ImageWriter)writers.next();
				if (writer != null){
					param 		= writer.getDefaultWriteParam();
					metadata 	= writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), param);
					
					if ((metadata != null) && (!metadata.isReadOnly()) && (metadata.isStandardMetadataFormatSupported()))
						break;
				}
			}
			
			if (writer == null){
				StringBuilder sb 			= new StringBuilder();
				String[] writerFormatNames 	= ImageIO.getWriterFormatNames();
				for (String fmt : writerFormatNames){
					sb.append(fmt);
					sb.append(' ');
				}
				return false;
			}
			
			if ((param != null) && (param.canWriteCompressed())){
				param.setCompressionMode(2);
				if (formatName.toLowerCase().startsWith(_TIF)){
					if ("".equals(compressionType)){
						if ((image.getType() == BufferedImage.TYPE_BYTE_BINARY) && (image.getColorModel().getPixelSize() == BufferedImage.TYPE_INT_RGB))
							param.setCompressionType(COMPRESS_CCITT_T6);
						else
							param.setCompressionType(COMPRESS_LZW);
					}
				}else{
					param.setCompressionType(compressionType);
					if (compressionType != null)
						param.setCompressionQuality(compressionQuality);
				}
			}else{
				param.setCompressionType(param.getCompressionTypes()[0]);
				param.setCompressionQuality(compressionQuality);
			}
			
			if (formatName.toLowerCase().startsWith(_TIF)){
				String metaDataFormat 	= metadata.getNativeMetadataFormatName();
				IIOMetadataNode root 	= new IIOMetadataNode(metaDataFormat);
				IIOMetadataNode ifd		= null;
				
				if (root.getElementsByTagName(_TIFFIFD).getLength() == 0) {
					ifd 	= new IIOMetadataNode(_TIFFIFD);
					root.appendChild(ifd);
				} else {
					ifd 	= (IIOMetadataNode)root.getElementsByTagName(_TIFFIFD).item(0);
				}
				
				ifd.appendChild(createRationalField(282, "XResolution", wDpi, 1));
				ifd.appendChild(createRationalField(283, "YResolution", hDpi, 1));
				ifd.appendChild(createShortField(296, "ResolutionUnit", 2));
				ifd.appendChild(createLongField(278, "RowsPerStrip", image.getHeight()));
				ifd.appendChild(createAsciiField(305, "Software", _SOFTWARE));
				
				if ((image.getType() == BufferedImage.TYPE_BYTE_BINARY) && (image.getColorModel().getPixelSize() == BufferedImage.TYPE_INT_RGB))
					ifd.appendChild(createShortField(262, "PhotometricInterpretation", 0));
				
				metadata.mergeTree(metaDataFormat, root);
				
			}else if ((_JPEG.equals(formatName.toLowerCase())) || (_JPG.equals(formatName.toLowerCase()))){
			}else if ((metadata != null) && (!metadata.isReadOnly()) && (metadata.isStandardMetadataFormatSupported())){
			}
			
			imageOutput 	= ImageIO.createImageOutputStream(output);
			
			writer.setOutput(imageOutput);
			writer.write(null, new IIOImage(image, null, metadata), param);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.dispose();
			if (imageOutput != null)
				imageOutput.close();
			if (output != null)
				output.close();
		}
		return true;
	}
	
	private static IIOMetadataNode createRationalField(int number, String name, int numerator, int denominator){
		IIOMetadataNode field 		= null;
		IIOMetadataNode arrayNode	= null;
		IIOMetadataNode valueNode 	= null; 
		try {
			field 		= new IIOMetadataNode(_META_TIFFField);
			field.setAttribute(_META_NUMBER, 	Integer.toString(number));
			field.setAttribute(_META_NAME, 		name);
			
			arrayNode 	= new IIOMetadataNode(_META_TIFFRationals);
			field.appendChild(arrayNode);
			
			valueNode = new IIOMetadataNode(_META_TIFFRational);
			arrayNode.appendChild(valueNode);
			valueNode.setAttribute(_NODE_VAL, numerator + "/" + denominator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}
	
	private static IIOMetadataNode createShortField(int tiffTagNumber, String name, int val) {
		IIOMetadataNode field 		= null;
		IIOMetadataNode arrayNode 	= null;
		IIOMetadataNode valueNode 	= null;
		try {
			field 		= new IIOMetadataNode(_META_TIFFField);
			field.setAttribute(_META_NUMBER, 	Integer.toString(tiffTagNumber));
			field.setAttribute(_META_NAME, 		name);

			arrayNode 	= new IIOMetadataNode(_META_TIFFShorts);
			field.appendChild(arrayNode);
			
			valueNode 	= new IIOMetadataNode(_META_TIFFShort);
			arrayNode.appendChild(valueNode);
			valueNode.setAttribute(_NODE_VAL, Integer.toString(val));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}
	
	private static IIOMetadataNode createLongField(int number, String name, long val) {
		IIOMetadataNode field 		= null;
		IIOMetadataNode arrayNode 	= null;
		IIOMetadataNode valueNode 	= null;
		try {
			field 		= new IIOMetadataNode(_META_TIFFField);
			field.setAttribute(_META_NUMBER, 	Integer.toString(number));
			field.setAttribute(_META_NAME, 		name);
			
			arrayNode = new IIOMetadataNode(_META_TIFFLongs);
			field.appendChild(arrayNode);
			
			valueNode 	= new IIOMetadataNode(_META_TIFFLong);
			arrayNode.appendChild(valueNode);
			valueNode.setAttribute(_NODE_VAL, Long.toString(val));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}
	
	private static IIOMetadataNode createAsciiField(int number, String name, String val) {
		IIOMetadataNode field 		= null;
		IIOMetadataNode arrayNode 	= null;
		IIOMetadataNode valueNode 	= null;
		try {
			field 		= new IIOMetadataNode(_META_TIFFField);
			field.setAttribute(_META_NUMBER, Integer.toString(number));
			field.setAttribute(_META_NAME, name);
			
			arrayNode 	= new IIOMetadataNode(_META_TIFFAsciis);
			field.appendChild(arrayNode);
			
			valueNode 	= new IIOMetadataNode(_META_TIFFAscii);
			arrayNode.appendChild(valueNode);
			valueNode.setAttribute(_NODE_VAL, val);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return field;
	}
}
