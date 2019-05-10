/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/wsdl/WSDLDocument.java,v 1.2 2004/12/01 16:14:53 joepeer Exp $
 * $Date: 2004/12/01 16:14:53 $
 *
 * WSPlan - Automatic Web Service Composition
 * Copyright (C) MCM institute, University of St. Gallen
 * Written by Joachim Peer
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.mcm.sws.wsdl;

import java.io.*;
import java.util.*;

import org.mcm.sws.*;
import org.mcm.sws.util.*;
import org.mcm.sws.xmlschema.*;


import org.apache.log4j.Logger;

import org.apache.commons.digester.*;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;


/**
 * This class models the WSDL &lt;Definitions&gt; root-element (see also
 * http://www.w3.org/TR/wsdl12) and provides a parser method.
 *
 * @author    Joachim Peer
 */
public class WSDLDocument
		 implements URIIdentifiable {

	private static Logger log = Logger.getLogger(WSDLDocument.class);

	public static final Namespace NS_WSDL_HTTP = Namespace.getNamespace("http://schemas.xmlsoap.org/wsdl/http/");
	public static final Namespace NS_WSDL_SOAP = Namespace.getNamespace("http://schemas.xmlsoap.org/wsdl/soap/");
	public static final Namespace NS_WSDL_SOAPENC = Namespace.getNamespace("http://schemas.xmlsoap.org/soap/encoding/");
	public static final Namespace NS_WSDL_MIME = Namespace.getNamespace("http://schemas.xmlsoap.org/wsdl/mime/");
	public static final Namespace NS_WSDL = Namespace.getNamespace("http://schemas.xmlsoap.org/wsdl/");

	protected List xsdSchemas;
	protected LinkedHashMap messages;
	protected LinkedHashMap portTypes;
	protected LinkedHashMap bindings;
	protected LinkedHashMap services;
	protected String name;
	protected org.jdom.Namespace rootNamespace;
	protected String targetNamespace;
	protected HashMap additionalNamespaces;
	protected String uri;


	/**
	 *Constructor for the WSDLDocument object
	 */
	public WSDLDocument() {
		messages = new LinkedHashMap();
		portTypes = new LinkedHashMap();
		bindings = new LinkedHashMap();
		services = new LinkedHashMap();
		additionalNamespaces = new HashMap();
		xsdSchemas = new ArrayList();
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setRootNamespace(org.jdom.Namespace rootNamespace) {
		this.rootNamespace = rootNamespace;
	}

	public org.jdom.Namespace getRootNamespace() {
		return rootNamespace;
	}

	public void addAdditionalNamespace(org.jdom.Namespace additionalNamespace) {
		this.additionalNamespaces.put(additionalNamespace.getPrefix(), additionalNamespace);
	}

	public HashMap getAdditionalNamespaces() {
		return additionalNamespaces;
	}

	public String getAdditionalNamespace(String prefix) {
		org.jdom.Namespace ns = (org.jdom.Namespace) additionalNamespaces.get(prefix);
		return (ns != null) ? ns.getURI() : null;
	}


	public void setXSDSchemas(List xsdSchemas) {
		this.xsdSchemas = xsdSchemas;
	}

	public void addXSDSchema(XSSchema xsSchema) {
		this.xsdSchemas.add(xsSchema);
	}

	public List getXSDSchemas() {
		return xsdSchemas;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addMessage(Message m) {
		messages.put(m.getName(), m);
	}

	public Message getMessage(String message) {
		return (Message) messages.get(message);
	}

	public LinkedHashMap getMessages() {
		return messages;
	}

	public void addPortType(PortType pt) {
		portTypes.put(pt.getName(), pt);
	}

	public LinkedHashMap getPortTypes() {
		return portTypes;
	}

	public PortType getPortType(String name) {
		return (PortType) portTypes.get(name);
	}

	public void addBinding(Binding binding) {
		bindings.put(binding.getName(), binding);
	}

	public Binding getBinding(String name) {
		return (Binding) bindings.get(name);
	}

	public void addService(Service service) {
		services.put(service.getName(), service);
	}

	public Map getServices() {
		return services;
	}

	public Service getFirstService() {
		return (Service) ((Map.Entry) services.entrySet().iterator().next()).getValue();
	}

	public Port getFirstPort() {
		Service s = (Service) ((Map.Entry) services.entrySet().iterator().next()).getValue();
		return (Port) ((Map.Entry) s.getPorts().entrySet().iterator().next()).getValue();
	}

	public static String createURI(String tns) {
		return StrUtils.slashed(tns);
	}

	public String getURI() {
		if (uri == null) {
			uri = createURI(targetNamespace);
		}
		return uri;
	}

	private String expandQName(String qName) {
		int protoPos = qName.indexOf("://");
		if(protoPos != -1) return qName;

		int colonPos = qName.indexOf(':');
		if(colonPos == -1) return qName; // there is nothing we can do... maybe throw exception?

		String prefix = qName.substring(0, colonPos);
		Namespace baseURI = (Namespace) getAdditionalNamespaces().get(prefix);
		if(baseURI == null) return qName; // there is nothing we can do... maybe throw exception?
		return StrUtils.slashed(baseURI.getURI()) + qName.substring(colonPos+1);
	}

	private String[] splitQName(String qName) {
		String[] result = new String[2];

		int protoPos = qName.indexOf("://");
		if(protoPos != -1) {
			int crossPos = qName.indexOf('#');
			if(crossPos != -1) {
				result[0] = qName.substring(0,crossPos);
				result[1] = qName.substring(crossPos+1);
			} else {
				int lastSlashPos = qName.lastIndexOf('/');
				result[0] = qName.substring(0,lastSlashPos);
				result[1] = qName.substring(lastSlashPos+1);
			}
		} else { // prefixed foo:bar or name only
			int colonPos = qName.indexOf(':');
			if(colonPos != -1) {
				Namespace baseURI = (Namespace) getAdditionalNamespaces().get(qName.substring(0,colonPos));
				result[0] = (baseURI == null) ? null : baseURI.getURI();
				result[1] = qName.substring(colonPos+1);
			} else {
				result[0] = null;
				result[1] = qName;
			}
		}
		return result;
	}

	private String[] splitQName(String qName, String defaultNamespace) {
		String[] result = splitQName(qName);
		if(result[0] == null) result[0] = defaultNamespace;
		return result;
	}

	public String getNamespaceOfQName(String qName) {
		return splitQName(qName)[0];
	}

	public String getLocalOfQName(String qName) {
		return splitQName(qName)[1];
	}



	// -------------------- parser methods -------------------

	/**
	<soap:body use="encoded" namespace="http://example.com/stockquote"
                          encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"/>
	*/
	private static SOAPBodyDef parseSOAPBodyDef(Element wrapper, WSDLDocument parent, SOAPOperationBinding opBdg) {
		Element soapBodyDefElem = wrapper.getChild("body", NS_WSDL_SOAP);
		if(soapBodyDefElem == null) return null;

	  SOAPBodyDef soapBodyDef = new SOAPBodyDef(parent, opBdg);
		soapBodyDef.setUse("literal".equalsIgnoreCase(soapBodyDefElem.getAttributeValue("use")) ? SOAPBodyDef.USE_LITERAL : SOAPBodyDef.USE_ENCODED);
		soapBodyDef.setNamespace(soapBodyDefElem.getAttributeValue("namespace"));
		soapBodyDef.setEncodingStyle(soapBodyDefElem.getAttributeValue("encodingStyle"));

		return soapBodyDef;
	}

	/**
 <soap:header message="qname" part="nmtoken" use="literal|encoded"
                            encodingStyle="uri-list"? namespace="uri"?>*
  */
	private static List parseSOAPHeaderDefs(Element wrapper, WSDLDocument parent, SOAPOperationBinding opBdg) {
		ArrayList result = null;
		for(Iterator iter = wrapper.getChildren("header", NS_WSDL_SOAP).iterator(); iter.hasNext(); ) {
			Element soapHeaderDefElem = (Element) iter.next();
			if(soapHeaderDefElem == null) return null;

			SOAPHeaderDef soapHeaderDef = new SOAPHeaderDef(parent, opBdg);
			soapHeaderDef.setUse("literal".equalsIgnoreCase(soapHeaderDefElem.getAttributeValue("use")) ? SOAPHeaderDef.USE_LITERAL : SOAPHeaderDef.USE_ENCODED);
			soapHeaderDef.setNamespace(soapHeaderDefElem.getAttributeValue("namespace"));
			soapHeaderDef.setEncodingStyle(soapHeaderDefElem.getAttributeValue("encodingStyle"));

			String[] msgDef = parent.splitQName(soapHeaderDefElem.getAttributeValue("message"), parent.getTargetNamespace());
			if(msgDef[0] != null && msgDef[1] != null) {
				Message msg = (Message) Registry.getInstance().getObject(Registry.WSDL_MESSAGE, Message.createURI(msgDef[0], msgDef[1]));
				if(msg == null) throw new WSDLParserException("Error - SOAPHeaderDef: message not found.");
				soapHeaderDef.setMessage(msg);

				Part part = msg.getPart(soapHeaderDefElem.getAttributeValue("part"));
				if(part == null) throw new WSDLParserException("Error - SOAPHeaderDef: part not found.");
				soapHeaderDef.setPart(part);
			}

			if(result==null) result = new ArrayList();
			result.add(soapHeaderDef);
		}

		return result;
	}


	/**
	 * This method parses the document at the specified location.
	 *
	 * @param  input  Description of the Parameter
	 * @return        Description of the Return Value
	 */
	public static WSDLDocument parseWSDL(String input) throws WSDLParserException {

	  try {

			WSDLDocument wsdlDoc = null;

			wsdlDoc = new WSDLDocument();

			SAXBuilder builder = new SAXBuilder();
			log.info("WSDL: loading..." + input);
			Document doc = builder.build(Files.getInputStream(input));

			// Get the root element
			Element root = doc.getRootElement();

			// Namespaces
			Namespace rootns = root.getNamespace();
			wsdlDoc.setRootNamespace(rootns);

			List nss = root.getAdditionalNamespaces();
			for (Iterator iter = nss.iterator(); iter.hasNext(); ) {
				Namespace ns = (Namespace) iter.next();
				wsdlDoc.addAdditionalNamespace(ns);
			}

			// XML Schema Type defintions

			// first load utility schemas, which may be needed even in absence
			// of an explicite XSD schema defintion
			XMLSchemaParser parser = new XMLSchemaParser();

			HashMap addNS = new HashMap();
			addNS.put("xsd", Namespace.getNamespace("xsd", XSSchema.NS_XSD));
			XSSchema datatypes = parser.parseSchema(Files.getInputStream("examples/xs/xs_datatypes.xsd"), addNS);
			XSSchema soapEncoding = parser.parseSchema(Files.getInputStream("examples/xs/soapencoding.xsd"), addNS);

			// now read the schema definitions of the WSDL documen (usually, only one..)
			Element types = root.getChild("types", rootns);
			if (types != null) {
				Iterator itr = (types.getChildren()).iterator();
				while (itr.hasNext()) {
					// copying XML Schema definition to stream
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					XMLOutputter outputter = new XMLOutputter();
					outputter.output((Element) itr.next(), os);
					String s = os.toString();

					XSSchema xs = parser.parseSchema(s, wsdlDoc.getAdditionalNamespaces());
					log.debug("--- XML SCHEMA PARSING RESULT ---\n"+xs.toString());
					wsdlDoc.addXSDSchema(xs);
				}
			}

			Registry registry = Registry.getInstance();

			wsdlDoc.setName(root.getAttributeValue("name"));
			wsdlDoc.setTargetNamespace(StrUtils.slashed(root.getAttributeValue("targetNamespace")));

			/*
			<message name="GetLastTradePriceOutput">
					<part name="body" element="xsd1:TradePrice"/>
			</message>
			*/
			for(Iterator iter=root.getChildren("message", NS_WSDL).iterator(); iter.hasNext(); ) {
				Element messageElem = (Element) iter.next();
				Message m = new Message(wsdlDoc);
				m.setName(messageElem.getAttributeValue("name"));
				wsdlDoc.addMessage(m);

				List partList = messageElem.getChildren("part", NS_WSDL);
				for(Iterator iter2=partList.iterator(); iter2.hasNext();) {
					Element partElem = (Element) iter2.next();

					Part part = new Part(wsdlDoc, m);
					part.setName(partElem.getAttributeValue("name"));

					String partXSDElement = partElem.getAttributeValue("element");
					String[] partXSDDef = null;
					if(partXSDElement != null) {
						partXSDDef = wsdlDoc.splitQName(partXSDElement, wsdlDoc.getTargetNamespace());
						part.setNamespace(partXSDDef[0]);
						part.setElementName(partXSDDef[1]);
						//part.setElementName(partXSDElement);
					}	else {
						if(partList.size() > 1) log.warn("WARNING: Violation of Sect. 2.3.1 of WSDL 1.1 spec: if type is used, only one msg part may be specified.");

						String partXSDType = partElem.getAttributeValue("type");
						partXSDDef = wsdlDoc.splitQName(partXSDType, wsdlDoc.getTargetNamespace());
						part.setNamespace(partXSDDef[0]);
						part.setTypeName(partXSDDef[1]);
						//part.setTypeName(partXSDType);
					}
					m.addPart(part);
				}

				m.register();
			}

			/*
			<portType name="StockQuotePortType">
					<operation name="GetLastTradePrice">
						 <input message="tns:GetLastTradePriceInput"/>
						 <output message="tns:GetLastTradePriceOutput"/>
					</operation>
			</portType>
			*/
			for(Iterator iter=root.getChildren("portType", NS_WSDL).iterator(); iter.hasNext(); ) {
				Element ptElem = (Element) iter.next();
				PortType portType = new PortType(wsdlDoc);
				portType.setName(ptElem.getAttributeValue("name"));
				wsdlDoc.addPortType(portType);

				for(Iterator iter2 = ptElem.getChildren("operation", NS_WSDL).iterator(); iter2.hasNext(); ) {
					Element opElem = (Element) iter2.next();
					Operation operation = new Operation(wsdlDoc, portType);
					operation.setName(opElem.getAttributeValue("name"));
					portType.addOperation(operation);

					Element inputElem = opElem.getChild("input", NS_WSDL);
					if(inputElem != null) {
						String[] msgDef = wsdlDoc.splitQName(inputElem.getAttributeValue("message"), wsdlDoc.getTargetNamespace());
						if(msgDef[0] != null && msgDef[1] != null) {
							Message inputMsg = (Message) registry.getObject(Registry.WSDL_MESSAGE, Message.createURI(msgDef[0], msgDef[1]));
							if(inputMsg == null) throw new WSDLParserException("Error: message '"+msgDef+"' not found.");
							operation.setInput(msgDef[0], msgDef[1], inputMsg);
						}
					}

					Element outputElem = opElem.getChild("output", NS_WSDL);
					if(outputElem != null) {
						String[] msgDef = wsdlDoc.splitQName(outputElem.getAttributeValue("message"), wsdlDoc.getTargetNamespace());
						if(msgDef[0] != null && msgDef[1] != null) {
							Message outputMsg = (Message) registry.getObject(Registry.WSDL_MESSAGE, Message.createURI(msgDef[0], msgDef[1]));
							if(outputMsg == null) throw new WSDLParserException("Error: message '"+msgDef+"' not found.");
							operation.setOutput(msgDef[0], msgDef[1], outputMsg);
						}
					}

					Element faultElem = opElem.getChild("fault", NS_WSDL);
					if(faultElem != null) {
						String[] msgDef = wsdlDoc.splitQName(faultElem.getAttributeValue("message"), wsdlDoc.getTargetNamespace());
						if(msgDef[0] != null && msgDef[1] != null) {
							Message faultMsg = (Message) registry.getObject(Registry.WSDL_MESSAGE, Message.createURI(msgDef[0], msgDef[1]));
							if(faultMsg == null) throw new WSDLParserException("Error: message '"+msgDef+"' not found.");
							operation.setFault(msgDef[0], msgDef[1], faultMsg);
						}
					}
				}
				portType.register(); // recursivly registers pt and its op's
			}

			/*
			<binding name="StockQuoteSoapBinding" type="tns:StockQuotePortType">
					<soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>
					<operation name="GetLastTradePrice">
						 <soap:operation soapAction="http://example.com/GetLastTradePrice"/>
						 <input>
								 <soap:body use="literal"/>
						 </input>
						 <output>
								 <soap:body use="literal"/>
						 </output>
					</operation>
			</binding>
			*/
			for(Iterator iter=root.getChildren("binding", NS_WSDL).iterator(); iter.hasNext(); ) {
				Element bindingElem = (Element) iter.next();
				String bindingName = bindingElem.getAttributeValue("name");

				String bdgPortTypeStr = bindingElem.getAttributeValue("type");
				String[] bdDef = wsdlDoc.splitQName(bdgPortTypeStr, wsdlDoc.getTargetNamespace());
				PortType bdgPortType = (PortType) registry.getObject(Registry.WSDL_PORTTYPE, PortType.createURI(bdDef[0], bdDef[1]));
				if(bdgPortType == null) throw new WSDLParserException("Error: PortType '"+bdgPortTypeStr+"' not found in binding '"+bindingName+"'.");

				Element soapBindingElem = (Element) bindingElem.getChild("binding", NS_WSDL_SOAP);
				if(soapBindingElem == null) {
					log.warn("Skipping this binding, currently we only support SOAP bindings");
					continue; // currently we only support SOAP bindings
				}

				SOAPBinding soapBinding = new SOAPBinding(wsdlDoc, bdgPortType);
				soapBinding.setName(bindingName);
				// TODO: handle as boolean constant
				soapBinding.setStyle("rpc".equalsIgnoreCase(soapBindingElem.getAttributeValue("style"))
														? SOAPBinding.STYLE_RPC : SOAPBinding.STYLE_DOCUMENT);
				soapBinding.setTransport(soapBindingElem.getAttributeValue("transport"));

				//<operation .... >
				for(Iterator iter2 = bindingElem.getChildren("operation", NS_WSDL).iterator(); iter2.hasNext(); ) {

					Element opBdgElem = (Element) iter2.next();
					SOAPOperationBinding opBdg = new SOAPOperationBinding(wsdlDoc, soapBinding);
					String opName = opBdgElem.getAttributeValue("name");

					log.debug("parsing SOAP binding for operation: "+opName);

					String[] opNameDef = wsdlDoc.splitQName(opName, wsdlDoc.getTargetNamespace());
					Operation op = (Operation) registry.getObject(Registry.WSDL_OPERATION, Operation.createURI(bdgPortType.getName(), opNameDef[0], opNameDef[1]));
					if(op == null) throw new WSDLParserException("Error: Operation '"+opName+"' not found in binding '"+bindingName+"'");
					opBdg.setOperation(op);
					soapBinding.addOperationBinding(opBdg);


					//<soap:operation soapAction="uri"? style="rpc|document"?>?
					Element soapOpBdgElem = (Element) opBdgElem.getChild("operation", NS_WSDL_SOAP);
					opBdg.setSoapAction(soapOpBdgElem.getAttributeValue("soapAction"));
					/*
					cf. Sect. 3.4 of WSDL 1.1 spec
					*/
					String soapOpStyleStr = soapOpBdgElem.getAttributeValue("style");
					if(soapOpStyleStr == null)
						opBdg.setStyle(soapBinding.getStyle()); //
					else
						opBdg.setStyle(soapOpStyleStr.equalsIgnoreCase("rpc") ? SOAPBinding.STYLE_RPC : SOAPBinding.STYLE_DOCUMENT);

					//<input>
					Element inputBdgElem = (Element) opBdgElem.getChild("input", NS_WSDL);
					if(inputBdgElem != null) {
						// for now, skip header def's...
						SOAPBodyDef soapBodyDef = parseSOAPBodyDef(inputBdgElem, wsdlDoc, opBdg);
						// enforce WSDL specification
						if(!soapBodyDef.isConsistentWithMessageParts(opBdg.getOperation().getInputMsg())) throw new WSDLParserException("Error: violation of Sect. 3.5 - binding not consistent with message parts");
						opBdg.setInputSOAPBodyDef(soapBodyDef);

						// now parse SOAPHeader defs (if existant)
						opBdg.setInputSOAPHeaderDefs(parseSOAPHeaderDefs(inputBdgElem, wsdlDoc, opBdg));
					}

					//<output>
					Element outputBdgElem = (Element) opBdgElem.getChild("output", NS_WSDL);
					if(outputBdgElem != null) {
						// for now, skip header def's...
						SOAPBodyDef soapBodyDef = parseSOAPBodyDef(inputBdgElem, wsdlDoc, opBdg);
						// enforce WSDL specification
						if(!soapBodyDef.isConsistentWithMessageParts(opBdg.getOperation().getOutputMsg())) throw new WSDLParserException("Error: violation of Sect. 3.5 - binding not consistent with message parts");
						opBdg.setOutputSOAPBodyDef(soapBodyDef);

						// now parse SOAPHeader defs (if existant)
						opBdg.setOutputSOAPHeaderDefs(parseSOAPHeaderDefs(inputBdgElem, wsdlDoc, opBdg));
					}


				}
				wsdlDoc.addBinding(soapBinding);
				soapBinding.register();
			}

			/*
			<service name="StockQuoteService">
					<documentation>My first service</documentation>
					<port name="StockQuotePort" binding="tns:StockQuoteBinding">
						 <soap:address location="http://example.com/stockquote"/>
					</port>
			</service>
			*/

			for(Iterator iter=root.getChildren("service", NS_WSDL).iterator(); iter.hasNext(); ) {
				Element serviceElem = (Element) iter.next();

				Service service = new Service(wsdlDoc);
				service.setName(serviceElem.getAttributeValue("name"));

				for(Iterator iter2=serviceElem.getChildren("port", NS_WSDL).iterator(); iter2.hasNext(); ) {
					Element portElem = (Element) iter2.next();

					Port port = new Port(wsdlDoc, service);
					port.setName(portElem.getAttributeValue("name"));

					String bindingStr = portElem.getAttributeValue("binding");
					String[] bindingNameDef = wsdlDoc.splitQName(bindingStr, wsdlDoc.getTargetNamespace());
					Binding binding = (Binding) registry.getObject(Registry.WSDL_BINDING, Binding.createURI(bindingNameDef[0], bindingNameDef[1]));
					if(binding == null) throw new WSDLParserException("Binding '"+bindingStr+"' not found in service '"+service.getName()+"'");
					port.setBinding(binding);

					// currently, only SOAP binding supported
					Element soapAddressElem = portElem.getChild("address", NS_WSDL_SOAP);
					port.setAddress(soapAddressElem.getAttributeValue("location"));

					port.register();
					service.addPort(port);
				}
				wsdlDoc.addService(service);
			}

			Registry.getInstance().addObject(Registry.WSDL_DOC, wsdlDoc);
			return wsdlDoc;
		} catch(Exception e) {
			e.printStackTrace();
			throw new WSDLParserException(e.getMessage());
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("WSDL name=");
		buf.append(name);
		buf.append(", TargetNamespace=");
		buf.append(targetNamespace);
		buf.append("\n");

		for (Iterator iter = messages.entrySet().iterator(); iter.hasNext(); ) {
			Message m = (Message) ((Map.Entry) iter.next()).getValue();
			buf.append("Message:");
			buf.append(m.toString());
			buf.append("\n");
		}

		for (Iterator iter = portTypes.entrySet().iterator(); iter.hasNext(); ) {
			PortType pt = (PortType) ((Map.Entry) iter.next()).getValue();
			buf.append("PortType:");
			buf.append(pt.toString());
			buf.append("\n");
		}

		for (Iterator iter = bindings.entrySet().iterator(); iter.hasNext(); ) {
			Binding binding = (Binding) ((Map.Entry) iter.next()).getValue();
			buf.append("Binding:");
			buf.append(binding.toString());
			buf.append("\n");
		}

		for (Iterator iter = services.entrySet().iterator(); iter.hasNext(); ) {
			Service service = (Service) ((Map.Entry) iter.next()).getValue();
			buf.append("Service:");
			buf.append(service.toString());
			buf.append("\n");
		}

		return buf.toString();
	}


}
