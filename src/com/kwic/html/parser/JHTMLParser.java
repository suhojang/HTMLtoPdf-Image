package com.kwic.html.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.FormControl;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

/**
 * <pre>
 * Title		: JHTMLParser
 * Description	: HTML Parser
 * Date			: 
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC.
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈
 * @version	
 * @since 
 */
public class JHTMLParser {
	private Source src;
	
	public JHTMLParser(String htmlString){
		src	= new Source(htmlString);
	}
	public JHTMLParser(String filePath,boolean isURL) throws FileNotFoundException, IOException{
		src	= new Source(new FileInputStream(new File(filePath)));
	}
	public List<Element> getElementsByTagName(String tagName){
		return src.getAllElements(tagName);
	}
	public Element getElementById(String id){
		return src.getElementById(id);
	}
	
	public List<FormControl> getFormControl(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").equals(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='"+name+"']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}
	
	public List<FormControl> getFormControlStartsWith(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").startsWith(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='"+name+"...']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}
	public List<FormControl> getFormControlEndsWith(String name) throws Exception{
		List<FormControl> rsts	= new ArrayList<FormControl>();
		
		List<FormControl> forms	= src.getFormControls();
		for(int i=0;i<forms.size();i++){
			if(forms.get(i)==null || forms.get(i).getAttributesMap()==null || forms.get(i).getAttributesMap().get("name")==null)
				continue;
			if(forms.get(i).getAttributesMap().get("name").endsWith(name)){
				rsts.add(forms.get(i));
			}
		}
		if(rsts.size()<1)
			throw new Exception("[name='..."+name+"']에 해당하는 ELEMENT가 없습니다.");

		return rsts;
	}

	public String getFormValue(String elementName) throws Exception{
		return getFormValue(elementName,0);
	}
	
	public String getFormValue(String elementName,int idx) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		
		FormControl control	= list.get(idx);
		return control.getAttributesMap().get("value");
	}
	
	public String getFormValue(FormControl control) throws Exception{
		return control.getAttributesMap().get("value");
	}
	
	public String getFormType(String elementName) throws Exception{
		return getFormType(elementName,0);
	}
	public String getFormType(String elementName,int idx) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");

		FormControl control	= list.get(idx);
		return control.getAttributesMap().get("type");
	}
	public String getFormAttribute(String elementName,String attrName) throws Exception{
		return getFormAttribute(elementName,0,attrName);
	}
	public String getFormAttribute(String elementName,int idx,String attrName) throws Exception{
		List<FormControl> list	= getFormControl(elementName);
		if(list.size()<=idx)
			throw new Exception("[name='"+elementName+"']의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");

		FormControl control	= list.get(idx);
		return control.getAttributesMap().get(attrName);
	}
	public String getTdValue(int tbIdx,int trIdx,int tdIdx) throws Exception{
		List<Element> list	= src.getAllElements(HTMLElementName.TABLE);
		if(list.size()<=tbIdx)
			throw new Exception("TABLE의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element table	= list.get(tbIdx);
		
		list	= table.getAllElements(HTMLElementName.TR);
		if(list.size()<=trIdx)
			throw new Exception("TABLE["+tbIdx+"]-TR의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element tr	= list.get(trIdx);

		list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TABLE["+tbIdx+"]-TR["+trIdx+"]-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}
	
	public String getTdValue(Element tr,int tdIdx) throws Exception{
		List<Element> list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}

	public List<Element> getTRList(int tbIdx) throws Exception{
		List<Element> list	= src.getAllElements(HTMLElementName.TABLE);
		if(list.size()<=tbIdx)
			throw new Exception("TABLE의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element table	= list.get(tbIdx);
		
		list	= table.getAllElements(HTMLElementName.TR);
		
		return list;
	}
	
	public String getTdValue(String tbId,int trIdx,int tdIdx) throws Exception{
		Element table	= getElementById(tbId);

		List<Element> list	= table.getAllElements(HTMLElementName.TR);
		if(list.size()<=trIdx)
			throw new Exception("TABLE[id='"+tbId+"']-TR의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element tr	= list.get(trIdx);
		
		list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TABLE[id='"+tbId+"']-TR["+trIdx+"]-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		Element td	= list.get(tdIdx);
		
		return td.getContent().toString();
	}
	public String getTdValue(String trId,int tdIdx) throws Exception{
		Element tr	= getElementById(trId);

		List<Element> list	= tr.getAllElements(HTMLElementName.TD);
		if(list.size()<=tdIdx)
			throw new Exception("TR[id='"+trId+"']-TD의 index는 최대 ["+(list.size()-1)+"]까지 입니다.");
		
		Element td	= tr.getAllElements(HTMLElementName.TD).get(tdIdx);
		return td.getContent().toString();
	}
	public String getTdValue(String tdId){
		Element td	= getElementById(tdId);
		return td.getContent().toString();
	}
	
	public static void main(String[] args) throws Exception{
		StringBuffer sb	= new StringBuffer();
		
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"ko\" lang=\"ko\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<title>매출전자세금계산서합계표</title>");
		sb.append("");
		sb.append("");
		sb.append("<style type=\"text/css\">");
		sb.append("body {padding-left:1px;}");
		sb.append("body, h1, h2, h3, th, td, div, p {font-size: 13px; font-family: \"Malgun Gothic\", sans-serif;color: #000; font-weight:normal;}");
		sb.append("img {border:none;}");
		sb.append("table {border-collapse: collapse;  width: 100%; }");
		sb.append("th, td, li, p {}");
		sb.append("em {font-style:normal;}");
		sb.append("dl, ul, ol, menu, li {list-style: none;}      ");
		sb.append("* {margin: 0; padding: 0;}");
		sb.append("");
		sb.append("");
		sb.append(".table_area {/*border:2px solid #000;*/ width:695px;}");
		sb.append(".table_area tr td {padding:15px;}");
		sb.append(".t_tit {font-size:16px; font-weight:bold; margin-bottom:3px}");
		sb.append(".marbt30 {margin-bottom:30px;}");
		sb.append(".board_table01.pad3 tbody th.pdtb_none, .board_table01.pad3 tbody td.pdtb_none {padding:0 3px 0 3px !important;}");
		sb.append("");
		sb.append(".tearm {font-size:12px; font-weight:normal;}");
		sb.append(".title_03 .print_date {font-size:11px; font-weight:normal; text-align:left; padding-top:0}");
		sb.append(".title_03 .page_num {font-size:11px; font-weight:normal; text-align:right; padding-top:0}");
		sb.append(".w100 {width:100% !important}");
		sb.append(" ");
		sb.append("/***************board_table01***************/");
		sb.append(".board_area_01 {width:695px; border-left:2px solid #000; border-right:2px solid #000; border-bottom:2px solid #000;}");
		sb.append(".board_area_01.bd_01 {border-left:0 !important; border-right:0 !important;}");
		sb.append(".board_table01 {width: 695px;}");
		sb.append(".board_table01.bd_01 {border-top:2px solid #000; border-left:2px solid #000; border-right:2px solid #000;}");
		sb.append(".board_table01.bd_02 {border-left:2px solid #000; border-right:2px solid #000;}");
		sb.append(".board_table01.bd_03 {border-top:2px solid #000;}");
		sb.append(".board_table01.bd_04 {border-bottom:2px solid #000;}");
		sb.append(".board_table01.bd_05 {border:2px solid #000;}");
		sb.append(".marbt4 {margin-bottom:4px;}");
		sb.append(".board_table01.w220 {width:220px !important}");
		sb.append(".board_table01.w240 {width:240px !important}");
		sb.append(".board_table01 thead th {text-align:center; height: 21px;padding: 7px 0 7px 3px; color: #000000; font-size:13px ; font-weight:normal;border: #000 1px solid;}");
		sb.append(".board_table01 tbody th {text-align:left; padding: 7px 0 7px 5px; color: #000000; font-size: 13px; font-weight:normal;border: 1px solid #000;}");
		sb.append("");
		sb.append(".board_table01 tbody th.line_B_none, .board_table01 tbody td.line_B_none  {border-bottom:0 !important}");
		sb.append(".board_table01 tbody th.line_T_none, .board_table01 tbody td.line_T_none  {border-top:0 !important}");
		sb.append(".board_table01 tbody th.line_L_none, .board_table01 tbody td.line_L_none  {border-left:0 !important}");
		sb.append(".board_table01 tbody th.line_R_none, .board_table01 tbody td.line_R_none  {border-right:0 !important}");
		sb.append(".board_table01 tbody td {font-size:13px; text-align: left; padding: 7px 0 7px 5px; background: #fff; border: 1px solid #000;}");
		sb.append(".board_table01 tbody td.center, .board_table01 tbody th.center {text-align:center;}");
		sb.append(".board_table01 tbody td.sum {text-align: right; padding-right:3px;}");
		sb.append(".board_table01 tbody th.bggray, .board_table01 tbody td.bggray {background:#eeeeee !important}");
		sb.append(".board_table01 thead th.bggray, .board_table01 tbody td.bggray {background:#eeeeee !important}");
		sb.append(".board_table01 tbody tr.padtb5 th, .board_table01 tbody tr.padtb5 td {padding:5px 0 5px 5px !important} ");
		sb.append(".board_table01 tbody td.right {text-align:right;}");
		sb.append("");
		sb.append(".board_table01 tbody tr td.none table tr td {border:0;}");
		sb.append(".board_table01.b_none tbody tr td {border:0;}");
		sb.append(".board_table01.pad2 tbody td.padnone {padding:0;}");
		sb.append(".board_table01.pad2 tbody td.bdnone {border:0 !important;}");
		sb.append(".board_table01 tbody th b.padL {padding-left:16px !important; display:inline-block}");
		sb.append(".board_table01.pad2 tbody th, .board_table01.pad2 tbody td {padding: 0 3px 0 3px !important;}");
		sb.append("");
		sb.append(".board_table01.pad3 tbody th, .board_table01.pad3 tbody td {line-height:12px; padding: 7px 3px 7px 3px !important;}");
		sb.append(".board_table01.pad3 tbody tr.tit11 th, .board_table01.pad3 tbody tr.tit11 td {font-size:12px;}");
		sb.append("");
		sb.append(".top_tit {font-size:24px; font-weight:bold; margin-bottom:3px}");
		sb.append(".top_tit_02 {font-size:20px; font-weight:bold; margin-bottom:3px}");
		sb.append(".top_tit_02.marbt20 {margin-bottom:20px !important}");
		sb.append(".top_tit_03 {font-size:26px; font-weight:bold; margin-bottom:15px}");
		sb.append(".top_tit_s {font-size:16px; font-weight:bold}");
		sb.append(".marL30 {margin-left:30px; display:inline-block}");
		sb.append(".bt_sign {vertical-align:bottom; text-align:right; padding:0 35px 15px 0; font-size:16px}");
		sb.append(".bt_sign_02 {vertical-align:bottom; text-align:right; padding:0 35px 50px 0; font-size:16px}");
		sb.append(".date {margin-right:90px; margin-bottom:20px;}");
		sb.append(".h80 {height:80px;}");
		sb.append(".btdot {border-bottom:1px dotted #666 !important;}");
		sb.append("");
		sb.append("/**** board_table07 ****/");
		sb.append(".board_table07 {width:695px; border-left:2px solid #000; border-right:2px solid #000; border-bottom:2px solid #000;}");
		sb.append(".board_table07.bdbt {border-bottom:1px solid #000 !important;}");
		sb.append(".board_table07 thead th {text-align:center; height: 21px;padding: 7px 3px 7px 3px; color: #000000; font-size: 100%; font-weight:normal;border: #000 1px solid;}");
		sb.append(".board_table07 body th {text-align:center; height: 21px;padding: 7px 3px 7px 3px; color: #000000; font-size: 100%; font-weight:normal;border: #000 1px solid;}");
		sb.append(".board_table07 tbody td {font-size:100%; text-align: center; padding: 7px 3px 7px 3px; background: #fff;border: #000 1px solid;}");
		sb.append(".board_table07 tbody td.center {text-align:center;}");
		sb.append(".board_table07 tbody td.sum {text-align: right; padding-right:3px;}");
		sb.append(".board_table07 tfoot th {text-align:center; height: 21px;padding: 2px 0 2px 3px; color: #000000; font-size: 100%; font-weight:normal;border: #000 1px solid;}");
		sb.append("");
		sb.append(".board_table07 tbody th.left, .board_table07 tfoot th.left {text-align:left; padding-left:3px;}");
		sb.append(".board_table07 tbody td.num {text-align:right; padding-right:3px;}");
		sb.append(".board_table07 tfoot th.sum {text-align:right; padding-right:3px;  font-weight:bold;}");
		sb.append("");
		sb.append(".board_table07 tbody th.line_B_none, .board_table07 tbody td.line_B_none  {border-bottom:0 !important}");
		sb.append(".board_table07 tbody th.line_T_none, .board_table07 tbody td.line_T_none  {border-top:0 !important}");
		sb.append(".board_table07 tbody th.line_L_none, .board_table07 tbody td.line_L_none, .board_table07 thead th.line_L_none  {border-left:0 !important}");
		sb.append(".board_table07 tbody th.line_R_none, .board_table07 tbody td.line_R_none, .board_table07 thead th.line_R_none  {border-right:0 !important}");
		sb.append("");
		sb.append("");
		sb.append("/**** board_table08 ****/");
		sb.append(".board_table08 {width:695px; border:1px solid #000;}");
		sb.append(".board_table08 thead th {text-align:center; padding: 3px 3px 3px 3px; color: #000000; font-size: 12px; font-weight:normal;border: #000 1px solid;}");
		sb.append(".board_table08 tbody th {text-align:center; padding: 1px 3px 1px 3px; color: #000000; font-size: 11px; font-weight:normal;border: #000 1px solid;}");
		sb.append(".board_table08 tbody td {font-size:11px; text-align: center; padding: 1px 3px 1px 3px; background: #fff;border: #000 1px solid;}");
		sb.append(".board_table08 tbody td.center {text-align:center;}");
		sb.append(".board_table08 tbody td.sum {text-align: right; padding-right:3px;}");
		sb.append(".board_table08 tfoot th {text-align:center; height: 21px;padding: 2px 0 2px 3px; color: #000000; font-size: 100%; font-weight:normal;border: #000 1px solid;}");
		sb.append("");
		sb.append(".board_table08 tbody th.left, .board_table08 tfoot th.left {text-align:left; padding-left:3px;}");
		sb.append(".board_table08 tbody td.num {text-align:right; padding-right:3px;}");
		sb.append("");
		sb.append(".board_table08 tbody td.right {text-align: right;}");
		sb.append("");
		sb.append(".board_table08 tbody th.line_B_none, .board_table08 tbody td.line_B_none  {border-bottom:0 !important}");
		sb.append(".board_table08 tbody th.line_T_none, .board_table08 tbody td.line_T_none  {border-top:0 !important}");
		sb.append(".board_table08 thead th.line_L_none, .board_table08 tbody th.line_L_none, .board_table08 tbody td.line_L_none, .board_table07 table thead th.line_L_none  {border-left:0 !important}");
		sb.append(".board_table08 thead th.line_R_none, .board_table08 tbody th.line_R_none, .board_table08 tbody td.line_R_none, .board_table07 table thead th.line_R_none  {border-right:0 !important}");
		sb.append("");
		sb.append(".board_table08 thead th, .board_table08 tbody th, .board_table08 tbody td {font-size:11px !important}");
		sb.append(".ft11 {font-size:10px !important;}");
		sb.append("");
		sb.append("");
		sb.append(".pre {white-space:pre;}");
		sb.append(".fontS12 {font-size:12px !important}");
		sb.append(".fontS11 {font-size:11px !important}");
		sb.append(".va_top {vertical-align:top;}");
		sb.append("");
		sb.append(".table_bt_txt .bt_txt_01 {text-align:center; padding-top:10px;}");
		sb.append(".table_bt_txt .bt_txt_02 {padding-top:10px; padding-left:5px}");
		sb.append(".table_bt_txt .bt_txt_03 {text-align:right; padding-top:10px; padding-right:120px}");
		sb.append(".table_bt_txt .bt_txt_04 {text-align:center; padding-top:30px;}");
		sb.append(".table_bt_txt .bt_txt_05 {text-align:center; padding-top:10px; padding-bottom:30px;}");
		sb.append(".table_bt_txt .bt_txt_06 {padding:20px 5px;}");
		sb.append(".top_num_table {width:695px; margin-bottom:3px}");
		sb.append(".top_num_table .top_num {font-size:11px;}");
		sb.append(".fsize14 {font-size:14px !important}");
		sb.append("");
		sb.append(".won {width:695px; border-left:2px solid #000; border-right:2px solid #000}");
		sb.append(".won td {text-align:right; padding:2px 2px 2px 0;}");
		sb.append(".chkbox01 {display:inline-block; margin-right:20px;}");
		sb.append(".chkbox02 {display:inline-block;}");
		sb.append(".chkbox01 input, .chkbox02 input {vertical-align:middle; display:inline-block; margin-right:8px;}");
		sb.append(".h30 {height:28px;}");
		sb.append(".title {text-align:center; font-size:26px; font-weight:bold; margin-top:10px; padding-bottom:10px; width:695px;}");
		sb.append(".title_02 {text-align:center; font-size:26px; margin-top:10px; padding-bottom:20px; width:695px;}");
		sb.append(".txtR_02 {display:block; text-align:right; margin-right:10px}");
		sb.append(".txtright {text-align:right !important;}");
		sb.append(".d_block {display:block}");
		sb.append(".ex_txt {padding:0 10px}");
		sb.append(".ex_txt .ex01 {padding-left:10px; display:block}");
		sb.append(".ex_txt .ex02 {padding-left:20px; display:block}");
		sb.append(".use_txt {text-align:center !important; font-size:18px !important}");
		sb.append(".bd_bt {border-bottom:0}");
		sb.append(".use_tit {text-align:center !important; font-size:22px !important;}");
		sb.append("");
		sb.append(".title_03 {width:695px;}");
		sb.append(".title_03 tbody tr td {text-align: center; font-size: 18px; font-weight: bold; padding-top: 10px; padding-bottom: 15p;}");
		sb.append(".fontS11 {font-size:11px !important;}");
		sb.append(".txt05 {margin-bottom:10px; padding:0 10px}");
		sb.append(".txt06 {padding:0 10px}");
		sb.append(".bt_pv {margin-top:15px}");
		sb.append("");
		sb.append(".taxpayer {text-align:right !important; font-size:11px !important}");
		sb.append(".txt_none {text-align:center; font-size:14px; font-weight:bold; border-bottom:1px solid #000; padding-bottom:4px;}");
		sb.append(".add {margin-top:10px}");
		sb.append(".add dt, .add dd {font-weight:bold; margin-bottom:5px;}");
		sb.append(".martp20 {margin-top:20px;}");
		sb.append("");
		/*sb.append("body {");
		sb.append("	page-break-after:always;");
		sb.append("}");*/
		sb.append("");
		sb.append(".pageNo::after{");
		sb.append("	counter-increment : page;");
		sb.append("	content : counter(page);");
		sb.append("}");
		sb.append("");
		sb.append("</style>");
		sb.append("");
		sb.append("</head>");
		sb.append("");
		sb.append("<body>");
		sb.append("	<table>");
		sb.append("		<!-- 테이블의 header 부분 -->");
		sb.append("		<thead>");
		sb.append("			<tr>");
		sb.append("				<td>");
		sb.append("					<table class=\"title_03\">");
		sb.append("					 <tbody>");
		sb.append("						<tr>");
		sb.append("							<td colspan=\"2\">매출 전자세금계산서 합계표<br /><span class=\"tearm\">(2019-01-23~2019-01-29)</span></td>");
		sb.append("						</tr>");
		sb.append("						<tr>");
		sb.append("							<td class=\"print_date\" style=\"padding-left:15px;\">출력일자 : 2019년 1월 29일</td>");
		sb.append("							<td class=\"page_num\" style=\"padding-right:15px;\">페이지: <span class=\"pageNo\"></span></td>");
		sb.append("						</tr>");
		sb.append("					 </tbody>   ");
		sb.append("				  </table>");
		sb.append("				</td>");
		sb.append("			</tr>");
		sb.append("		</thead>");
		sb.append("");
		sb.append("");
		sb.append("		<!-- 페이지 content 부분 -->");
		sb.append("		<tbody>");
		sb.append("			<tr>");
		sb.append("				<td>");
		sb.append("					<table class=\"table_area\">");
		sb.append("						<tr>");
		sb.append("							<td>");
		sb.append("								  <p class=\"t_tit\">인적사항</p>");
		sb.append("								  <table summary=\"\" class=\"board_table01 bd_05 pad3 marbt30 w100\">");
		sb.append("										   <colgroup>");
		sb.append("											  <col width=\"12%\" />");
		sb.append("											  <col width=\"15%\" />");
		sb.append("											  <col width=\"10%\" />");
		sb.append("											  <col width=\"12%\" />");
		sb.append("											  <col width=\"13%\" />");
		sb.append("											  <col width=\"38%\" />");
		sb.append("											  ");
		sb.append("										   </colgroup>");
		sb.append("										   <tbody>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray\">사업자번호</th>");
		sb.append("													<td class=\"center\">214-81-59394</td>");
		sb.append("													<th class=\"center bggray pdtb_none\">종사업장<br />번호</th>");
		sb.append("													<td>&nbsp;</td>");
		sb.append("													<th class=\"center bggray\">상호(법인명)</th>");
		sb.append("													<td>기웅정보통신(주)</td>");
		sb.append("												 </tr>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray\">성명(대표자)</th>");
		sb.append("													<td colspan=\"3\">최병인</td>");
		sb.append("													<th class=\"center bggray\">사업장소재지</th>");
		sb.append("													<td class=\"pdtb_none\">서울특별시 금천구 가산디지털2로 98(가산동,롯데IT캐슬 제6층613호~615호)</td>");
		sb.append("												 </tr>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray\">거래기간</th>");
		sb.append("													<td colspan=\"5\">2019-01-23 ~ 2019-01-29</td>");
		sb.append("												 </tr>");
		sb.append("										   </tbody>");
		sb.append("								 </table>");
		sb.append("								 ");
		sb.append("								 <p class=\"t_tit\">매출 전자세금계산서 총합계</p>");
		sb.append("								 <table summary=\"\" class=\"board_table01 bd_05 pad3 marbt30 w100\">");
		sb.append("										   <colgroup>");
		sb.append("											  <col width=\"16.6%\" />");
		sb.append("											  <col width=\"16.6%\" />");
		sb.append("											  <col width=\"16.6%\" />");
		sb.append("											  <col width=\"16.6%\" />");
		sb.append("											  <col width=\"16.6%\" />");
		sb.append("											  <col width=\"16.6%\" />");
		sb.append("										   </colgroup>");
		sb.append("										   <tbody>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray\">구분</th>");
		sb.append("													<th class=\"center bggray\">매출처수</th>");
		sb.append("													<th class=\"center bggray\">매수</th>");
		sb.append("													<th class=\"center bggray\">공급가액</th>");
		sb.append("													<th class=\"center bggray\">세액</th>");
		sb.append("													<th class=\"center bggray\">합계금액</th>");
		sb.append("												 </tr>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray\">합계</th>");
		sb.append("													<td class=\"right\">78</td>");
		sb.append("													<td class=\"right\">84</td>");
		sb.append("													<td class=\"right\">37,167,101</td>");
		sb.append("													<td class=\"right\">3,716,809</td>");
		sb.append("													<td class=\"right\">40,884,910</td>");
		sb.append("												 </tr>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray pdtb_none\">사업자등록번호<br />발급분</th>");
		sb.append("													<td class=\"right\">78</td>");
		sb.append("													<td class=\"right\">84</td>");
		sb.append("													<td class=\"right\">37,167,101</td>");
		sb.append("													<td class=\"right\">3,716,809</td>");
		sb.append("													<td class=\"right\">40,884,910</td>");
		sb.append("												 </tr>");
		sb.append("												 <tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray pdtb_none\">주민등록번호<br />발급분</th>");
		sb.append("													<td class=\"right\">0</td>");
		sb.append("													<td class=\"right\">0</td>");
		sb.append("													<td class=\"right\">0</td>");
		sb.append("													<td class=\"right\">0</td>");
		sb.append("													<td class=\"right\">0</td>");
		sb.append("												 </tr>");
		sb.append("										   </tbody>");
		sb.append("								 </table>");
		sb.append("								 ");
		sb.append("								 <table summary=\"\" class=\"board_table01 bd_05 pad3\" style=\"width:100%\">");
		sb.append("										   <colgroup>");
		sb.append("											  <col width=\"5%\" />");
		sb.append("											  <col width=\"17%\" />");
		sb.append("											  <col width=\"16%\" />");
		sb.append("											  <col width=\"10%\" />");
		sb.append("											  <col width=\"17%\" />");
		sb.append("											  <col width=\"17%\" />");
		sb.append("											  <col width=\"17%\" />");
		sb.append("										   </colgroup>");
		sb.append("										   <thead>");
		sb.append("												<tr class=\"tit11\">");
		sb.append("													<th class=\"center bggray\">번호</th>");
		sb.append("													<th class=\"center bggray pdtb_none\">공급받는자<br />등록번호</th>");
		sb.append("													<th class=\"center bggray\">상호(법인명)</th>");
		sb.append("													<th class=\"center bggray\">매수</th>");
		sb.append("													<th class=\"center bggray\">공급가액</th>");
		sb.append("													<th class=\"center bggray\">세액</th>");
		sb.append("													<th class=\"center bggray\">합계금액</th>");
		sb.append("												 </tr>");
		sb.append("										   </thead>");
		sb.append("										   <tbody>");
		
		for (int i = 0; i < 30; i++) {
			sb.append("												 <tr class=\"tit11\" name=\"tr_arr\">");
			sb.append("													<td class=\"center\">"+(i+1)+"</td>");
			sb.append("													<td class=\"center\">208-81-06344</td>");
			sb.append("													<td>(주)웅진</td>");
			sb.append("													<td class=\"right\">1</td>");
			sb.append("													<td class=\"right\">70,000</td>");
			sb.append("													<td class=\"right\">7,000</td>");
			sb.append("													<td class=\"right\">77,000</td>");
			sb.append("												 </tr>");
		}
		
		sb.append("										   </tbody>");
		sb.append("								 </table>");
		sb.append("								 ");
		sb.append("							 </td>");
		sb.append("						 </tr>");
		sb.append("					 </table><!--//table_area-->");
		sb.append("				</td>");
		sb.append("			 </tr>");
		sb.append("		</tbody>");
		sb.append("");
		sb.append("		<!-- 테이블의 footer 부분 -->");
		sb.append("		");
		sb.append("		 <tfoot>");
		sb.append("			<tr>");
		sb.append("				<td>");
		sb.append("					<p style=\"text-align:right; width:695px;\">페이지 계속</p>");
		sb.append("					<!--<p style=\"text-align:right; width:695px;padding-top:15px;\">페이지 계속</p>-->");
		sb.append("				</td>");
		sb.append("			</tr>");
		sb.append("		 </tfoot>");
		sb.append("		");
		sb.append("	</table>");
		sb.append("</body>");
		sb.append("</html>");
		;
		
		JHTMLParser parser	= new JHTMLParser(sb.toString());
		
		List<Element> trs	= parser.getTRList(5);
		for (int i = 0; i < trs.size(); i++) {
			Element tr	= trs.get(i);
			List<Element> tds	= tr.getChildElements();
			for (int j = 0; j < tds.size(); j++) {
				System.out.println(tds.get(j).getContent());
			}
		}
	}
	
}
