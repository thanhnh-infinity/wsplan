package org.mcm.sws.xmlschema.test;

import java.util.*;
import java.io.*;

import org.mcm.sws.xmlschema.*;
import org.mcm.sws.util.*;
import org.jdom.*;
import org.jdom.output.*;

public class Test2 {

	public static void main(String[] args) {

		String testFile = "examples/xs/po.xsd"; 
		
		try {
			InputStream is = Files.getInputStream(testFile);
			XMLSchemaParser parser = new XMLSchemaParser();
			XSSchema schema = parser.parseSchema(is);

			System.out.println("--- PARSING RESULT ---\n"+schema.toString());
			
			HashMap values = new HashMap();
			values.put("purchaseOrder/shipTo/name", "Joe Peer");
			values.put("purchaseOrder/shipTo/street", "Mainstreet 2");
			values.put("purchaseOrder/shipTo/@country", "CH");
			
			/*
			Element elem = new Element("somePOMessage");
			XSElement po = schema.getElement("purchaseOrder");
			po.instantiate(elem, values, "", "");
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			System.out.println("** Instantiation **:");
			out.output(elem, System.out); */
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
