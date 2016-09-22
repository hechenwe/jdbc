package com.sooncode.jdbc.sql.xml;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * 解析xml字符串 
 * 注意. 根节点为:"xml"
 * @author pc
 *
 */
class ParaXml {
	private String xmlString;

	private Document document;

	public ParaXml(String xmlString) {
		this.xmlString = xmlString;

		InputSource inputSource = new InputSource(new StringReader(this.xmlString));
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
		} catch (SAXException | IOException | ParserConfigurationException e) {

			e.printStackTrace();
		}

	}

	 
	
	
	public  String getValue(String id) {
		
		NodeList root = document.getElementsByTagName("sqls");
		 
		Node node = root.item(0); // NodeList中的某一个节点
		NodeList list =node.getChildNodes();
		
		for(int i = 0;i<list.getLength();i++){
			
			    String ke = list.item(i).getNodeName();
			    if( ! ke.equals("#text")){
			    	
			    	Element element = (Element) list.item(i); 
			    	String thisId = element.getAttribute("id");
			    	if(id.equals(thisId)){
			    		String value =  list.item(i).getTextContent();
			    		return value;
			    	}
			    }
		}
		return null;
	}
	public  String getValue(String id,Object... objes) {
		
		NodeList root = document.getElementsByTagName("sqls");
		
		Node node = root.item(0); // NodeList中的某一个节点
		NodeList list =node.getChildNodes();
		
		for(int i = 0;i<list.getLength();i++){
			
			String ke = list.item(i).getNodeName();
			if( ! ke.equals("#text")){
				
				Element element = (Element) list.item(i); 
				String thisId = element.getAttribute("id");
				if(id.equals(thisId)){
					String value =  list.item(i).getTextContent();
					
					return value;
				}
			}
		}
		return null;
	}
 
}
