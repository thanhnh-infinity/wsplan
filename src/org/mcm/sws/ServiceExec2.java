/*
 * $Header: /cvsroot/wsplan/wsplan/src/org/mcm/sws/ServiceExec2.java,v 1.2 2004/12/01 16:14:51 joepeer Exp $
 * $Date: 2004/12/01 16:14:51 $
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

package org.mcm.sws;

import java.util.*;
import java.io.*;
import java.net.*;

import org.mcm.sws.ui.*;
import org.mcm.sws.util.*;
import org.mcm.sws.pddl.*;
import org.mcm.sws.plan.*;
import org.mcm.sws.wsdl.*;
import org.mcm.sws.annotation.*;
import org.mcm.sws.xmlschema.*;

import javax.xml.soap.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import org.jdom.xpath.*;


import org.apache.log4j.Logger;

import org.apache.commons.jexl.*;
import org.apache.commons.lang.StringUtils;

/**
 * This class handles the low level operations involved in
 * calling web services. It reads the WSDL information (Binding, address, etc.)
 * to correctly create an outgoing message; it parses requests and
 * creates the results (i.e. materialized effects) in the factbase.
 *
 * The class can be used in context of planning (e.g. execution of plan, sensing
 * sub-plans) but it can also be used directly for invokation of Web Services,
 * i.e. as a high level web service execution facility.
 *
 * @author    Joachim Peer
 */
public class ServiceExec2 {

	private static Logger log = Logger.getLogger(ServiceExec2.class);

	/*
	use cases/contexts for callWSOperation
	a) as a standalone utility function:
	   parameters are the operation name, opDef, and a map of parameters

	b) as a utitlity function used for plan execution
	   each plan step is represented by an ActionInstance
	*/

	public static void executePlan(Plan p, WSPlanUI callback) {
		// the execution environment holds formulas made true by the effects of the
		// operation called, with concrete variable bindings (where available)
		// e.g (have-session-id site001 3432432432)
		// or (and (possess-stock IBM 344) (posess-stock SUNW 44))
		//List factBase = ExecEnvironment.getFactBase();

		// currently, we assume linear (sequencial plan)
		for (Iterator iter = p.getActionInstances().iterator(); iter.hasNext(); ) {
			ActionInstance ai = (ActionInstance) iter.next();
			if(ai.getOperation()==null) continue; // initial/goal
			callWSOperation(ai, new HashMap(), new HashMap(), callback);
		}
	}


	public static void callWSOperation(ActionInstance ai, Map bindingsFromCL, Map allBindings, WSPlanUI callback) {
		// determine values of the variables involved in this operation
		OpDef opDef = ai.getOpDef();
		Map varBindings = opDef.determineBindings(ai, bindingsFromCL);
		varBindings.putAll(bindingsFromCL);

		/*
		for (Iterator iter = varBindings.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			//VarDef vd = (VarDef) me.getKey();
			String var = (String) me.getKey();
			//String path = (String) me.getKey();
			String value = (String) me.getValue();
			log.info("callWSOperation - paramBinding: <" + var + "," + value + ">");
		}*/


		Operation op=ai.getOperation();
		Port port = op.getParent().getFirstPort();

		callWSOperation(port, op, ai.getOpDef(), varBindings, allBindings, callback);
	}


	public static void callWSOperation(String serviceURI, String serviceName, String opName, Map params) {
		callWSOperation(serviceURI, serviceName, opName, params, new HashMap(), null);
	}

	public static void callWSOperation(OpDef opDef, Map params) {
		Annotation anno = opDef.getParentAnnotation();
		String serviceURI = anno.getServiceNamespace();
		String serviceName = anno.getServiceName();
		callWSOperation(serviceURI, serviceName, opDef.getName(), params, new HashMap(), null);
	}

	public static void callWSOperation(String serviceURI, String serviceName, String opName, Map params, Map allBindings, WSPlanUI callback) {
		Registry registry = Registry.getInstance();
		WSDLDocument doc = (WSDLDocument) registry.getObject(Registry.WSDL_DOC, WSDLDocument.createURI(serviceURI));
		Port port = doc.getFirstPort();
		PortType pt = port.getBinding().getPortType();
		Operation op = pt.getOperation(opName);
		OpDef opDef = (OpDef) registry.getObject(Registry.ANNO_OPDEF, OpDef.createURI(serviceURI, opName));

		callWSOperation(port, op, opDef, params, allBindings, callback);
	}

	/*
	structure of params: ?varname => value

	strucutre of allBindings: ?varname = {set of values}
	*/
	public static void callWSOperation(Port port, Operation op, OpDef opDef, Map params, Map allBindings, WSPlanUI callback) {
		// retrieve binding information, currently we only handle SOAP

		// add default values to input varbindings
		for(Iterator iter=opDef.getInputVarDefs().entrySet().iterator(); iter.hasNext(); ) {
			VarDef vd = (VarDef) ((Map.Entry) iter.next()).getValue();
			String defVal = vd.getDefaultValue();
			if((defVal!=null) && (params.get(vd.getName())==null)) {
				params.put(vd.getName(), vd.getDefaultValue());
			}
		}

		// strucutre of allBindings: ?varname = {set of values}
		for(Iterator iter = params.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String paramName = (String) me.getKey();
			String paramValue = (String) me.getValue();
			Set s = (Set) allBindings.get(paramName);
			if(s == null) {
				s = new HashSet();
			}
			s.add(paramValue);
			allBindings.put(paramName, s);
		}

		Object o = port.getBinding();
		if(!(o instanceof SOAPBinding)) {
			throw new IllegalArgumentException("currently only SOAP bindings supported!");
		}
		SOAPBinding soapBinding = (SOAPBinding) o;
		OperationBinding operationBinding = soapBinding.getOperationBinding(op.getName());
		if(operationBinding == null) {
			throw new IllegalArgumentException("no binding found for operation '"+op.getName()+"'");
		}
		SOAPOperationBinding soapOpBinding = (SOAPOperationBinding) operationBinding;
		SOAPBodyDef inputSOAPBodyDef = soapOpBinding.getInputSOAPBodyDef();

		try {
			// prepare SOAP infrastructure
			//SOAPConnectionFactory scFactory = SOAPConnectionFactory.newInstance();
			//SOAPConnection con = scFactory.createConnection();

			// Create a message factory.
			MessageFactory mf = MessageFactory.newInstance();

			// Create a message from the message factory.
			SOAPMessage msg = mf.createMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope envelope = sp.getEnvelope();

			// Create a soap body from the envelope.
			SOAPBody bdy = envelope.getBody();
			SOAPHeader hdr = envelope.getHeader();
			SOAPElement messageRoot = (SOAPElement) bdy;

			// WSDL 1.1 spec, Sect. 3.5
			if(soapOpBinding.getStyle() == SOAPBinding.STYLE_RPC) {
				String ns = inputSOAPBodyDef.getNamespace();
				if(ns == null) ns = op.getParent().getTargetNamespace();

				// "each part (...) appears inside a wrapper element within the body"
				javax.xml.soap.Name nameOp = envelope.createName(op.getName(), "n1", ns);
				SOAPBodyElement be = bdy.addBodyElement(nameOp);

				messageRoot = be;
			}

			addPayLoad(op, opDef, params, envelope, messageRoot, soapBinding, soapOpBinding);
			addHeaderData(op, opDef, params, envelope, messageRoot, soapBinding, soapOpBinding);

			// Create an endpoint for the recipient of the message.
			log.debug("soap:address="+port.getAddress());
			URL urlEndpoint = new URL(port.getAddress());

			String logfileSuffix = "" + new Date().getTime() ;
			logMessage(msg, "log", "msg_" + logfileSuffix + "_sent.xml");

			// make SOAP call and wait for reply (we assume two way messages)
			FileInputStream fis = new FileInputStream(new File("log", "msg_" + logfileSuffix + "_sent.xml"));
			SOAPMessage reply = SOAPSender.call(fis, urlEndpoint.toString(), soapOpBinding.getSoapAction());
			fis.close();

			if(reply != null) {
				handleServiceReply(reply, opDef, logfileSuffix, params, allBindings, soapOpBinding, callback);
			}

			//con.close();

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void handleServiceReply(SOAPMessage reply, OpDef opDef,  String logfileSuffix, Map varBindings, Map allBindings, OperationBinding opBinding, WSPlanUI callback)
	throws javax.xml.soap.SOAPException {
		try {
				// stream out the reply
				File replyFile = new File("log", "msg_" + logfileSuffix + "_reply.xml");
				FileOutputStream replyFileOS = new FileOutputStream(replyFile);
				reply.writeTo(replyFileOS);
				replyFileOS.close();
				log.info("Reply logged in \"msg_" + logfileSuffix + "_reply.xml");

				//ByteArrayOutputStream bos = new ByteArrayOutputStream();
				//reply.writeTo(bos);
				Document replyDoc = (new SAXBuilder()).build(replyFile);
				//Jdom
				try {
					XMLUtils.stripNamespaces(replyDoc);
					XMLUtils.stripRefs(replyDoc);
					(new XMLOutputter(Format.getPrettyFormat())).output(replyDoc, new FileOutputStream(new File("log", "msg_" + logfileSuffix+"reply_b.xml")));
				} catch (Exception jde) {
					jde.printStackTrace();
				}
				Element root = replyDoc.getRootElement();
				// envelope
				Element body = root.getChild("Body", root.getNamespace());
				// NS would be http://schemas.xmlsoap.org/soap/envelope/

				Iterator it = body.getChildren().iterator();
				if (!it.hasNext()) {
					throw new RuntimeException("no message in body");
				}

				// TODO: the root element

				Element bodyElement = (Element) it.next();

				for(Iterator iter = opDef.getResults().iterator(); iter.hasNext(); ) {
					Result result = (Result) iter.next();
					Formula formula = result.getFormula();

					// extract information from service response (and add it to factBase)
					if(formula != null) {

						log.debug("MATERIALIZING EFFECT FORMULA:" + formula.toString());


						HashMap accumulatedBindings = new HashMap(); // to get value of "?result" or similar single, non-nested, output variables
						List facts = parseMessage(formula, bodyElement, "/", varBindings, accumulatedBindings, opBinding, opDef);
						for(Iterator iter2 = facts.iterator(); iter2.hasNext(); ) {
							log.info("F-A-C-T: "+iter2.next().toString());
						}

						log.debug("! accumulatedBindings = "+accumulatedBindings);

						log.debug("! varBindings = "+varBindings);

						// before we proceed, we check for conditions
						String effectCondition = result.getSuccessCondition();
						log.info("testing effect condition:" + effectCondition);
						if (effectCondition == null || checkEffectCondition(effectCondition, result.getSuccessConditionLang(), accumulatedBindings)) {
							log.info("got through success condition");
							// update factbase
							ExecEnvironment.getInstance().addAllToFactBase(facts);

							// adding to allBindings structure
							for(Iterator iter2 = accumulatedBindings.entrySet().iterator(); iter2.hasNext(); ) {
								Map.Entry me = (Map.Entry) iter2.next();
								String paramName = (String) me.getKey();
								String paramValue = (String) me.getValue();

								// add binding to "allBindings" structure:
								Set s = (Set) allBindings.get(paramName);
								if(s == null) {
									s = new HashSet();
								}
								s.add(paramValue);
								allBindings.put(paramName, s);
							}

						} else {
							log.info("SUCCESS CONDITION FAILED!");
						}

						// update the view
						if (callback != null) {
							callback.updateFactBaseView(ExecEnvironment.getInstance().getFactBase());
						}
					} else {
						log.error("formuala is null");
					}

				}
		} catch(Exception e) {
			e.printStackTrace();
			throw new javax.xml.soap.SOAPException(e.getMessage());
		}
	}

	private static void logMessage(SOAPMessage msg, String path, String name)
	throws IOException, javax.xml.soap.SOAPException {
			log.info("Sent message is logged in sent_" + path + "/" + name);
			File fl= new File(path, name);
			FileOutputStream sentFile = new FileOutputStream(fl);
			msg.writeTo(sentFile);
			sentFile.close();
	}




	// look up vardefs
	private static Map simpleMapForPart(Part p, OpDef opDef, Map m, int inOutFault) {

		HashMap result = new HashMap();

		for(Iterator iter=m.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String varName = (String) me.getKey();

			VarDef varDef = null;
			if(inOutFault == Operation.INPUT)
				varDef = opDef.getDefForInputVar(varName);
			else if(inOutFault == Operation.OUTPUT)
				varDef = opDef.getDefForOutputVar(varName);

			if(varDef == null) {
				log.warn("WARNING: simpleMapForPart: variable "+varName+" not found");
				continue;
			}

			if(p.getName().equals(varDef.getPart())) {
				// instead (?var,value) we now have (path,value) !
				String vdp = (varDef.getPath()!=null) ? varDef.getPath() : "";
				result.put(vdp , me.getValue());
			}
		}
		return result;
	}


	/**
	goal: for each message part, create an instance of the respective schema element
	and fill in values from the params map.
	*/
	protected static void addPayLoad(Operation op, OpDef opDef, Map params, SOAPEnvelope env, SOAPElement messageRoot, SOAPBinding soapBinding, SOAPOperationBinding soapOpBinding)
	throws SOAPException {
		SOAPBodyDef inputSOAPBodyDef = soapOpBinding.getInputSOAPBodyDef();

		log.info("== addPayLoad - opDEF="+opDef);

		CollUtils.printObjects(params.entrySet(), System.out);

		Message inputMsg = op.getInputMsg();

		// --- SOAP HEADER ---
		// go through all message parts
		for(Iterator iter = inputMsg.getParts().entrySet().iterator(); iter.hasNext(); ) {
			Part part = (Part)((Map.Entry) iter.next()).getValue();
			log.debug("addPayLoad: processing message part:" + inputMsg.getName() + " / " + part.getName());

			Map valueMap = simpleMapForPart(part, opDef, params, Operation.INPUT);
			for(Iterator iter2 = valueMap.entrySet().iterator(); iter2.hasNext(); ) {
				Map.Entry me = (Map.Entry) iter2.next();
				String key = (String) me.getKey();
				String value = (String) me.getValue();
				log.debug("simpleMapForPart key = "+key+", value="+value);
			}

			String xsNS = part.getNamespace();
			if(xsNS == null) xsNS = op.getParent().getTargetNamespace();
			log.debug("addPayLoad: looking up XS Schema: "+xsNS);
			XSSchema schema = XSRegistry.getSchema(xsNS);

			// ENCODED vs LITERAL
			String encodingStyle = (inputSOAPBodyDef.getUse() == SOAPBodyDef.USE_ENCODED) ?
			  inputSOAPBodyDef.getEncodingStyle() : null;
			// RPC vs DOCUMENT
			SOAPElement partRoot = (soapOpBinding.getStyle() == SOAPBinding.STYLE_RPC) ?
				messageRoot.addChildElement(part.getName()) : messageRoot;
			// TYPE vs ELEMENT
			if(part.getTypeName() != null) {
				XSType xsType = schema.getType(part.getTypeName());
				xsType.instantiate(env, partRoot, valueMap, "", encodingStyle);
			} else {
				if(inputSOAPBodyDef.getUse() == SOAPBodyDef.USE_ENCODED)
					log.warn("addPayLoad: >>WARNING<< WSDL violation: msg type vs. element");
				XSElement xsElement = schema.getElement(part.getElementName());
				xsElement.instantiate(env, partRoot, valueMap, "", encodingStyle);
			}
			copyNamespaceDeclarations(op.getParent().getAdditionalNamespaces(), env);
		}
	}

	private static void copyNamespaceDeclarations(Map m, SOAPEnvelope env) throws javax.xml.soap.SOAPException  {
		log.debug("*** copyNamespaceDeclarations ***");
		if(m==null) return;
		for(Iterator iter=m.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry me = (Map.Entry) iter.next();
			String prefix = (String) me.getKey();
			Namespace n = (Namespace) me.getValue();
			String uri = n.getURI();
			env.addNamespaceDeclaration(prefix, uri);
		}
	}

	protected static void addHeaderData(Operation op, OpDef opDef, Map params, SOAPEnvelope env, SOAPElement messageRoot, SOAPBinding soapBinding, SOAPOperationBinding soapOpBinding)
	throws SOAPException {
		/*
		SOAPBodyDef inputSOAPBodyDef = soapOpBinding.getInputSOAPBodyDef();

		Message inputMsg = op.getInputMsg();

		// --- SOAP HEADER ---
		// go through all message parts
		List headerDefsList = soapOpDef.getInputSOAPHeaderDefs();
		if(headerDefsList != null) {
			for(Iterator iter = headerDefsList.iterator(); iter.hasNext(); ) {

			}
		}*/

	}


	/**
	 *goal: populate the variables of the effect condition (as defined in opDef)
	 *with the actual values from the SOAP message
	 *e.g. <loginResponse><sid>12345</sid></loginResponse>
	 *leads to
	 *(has-session-id site0232 12345)
	 *how to accomplish this:
	 *1. get variable bindings (TODO: support FORALL)
	 *2. create clone of postcondition (TODO: semantics of implications, etc? - no problems because not allowed in effects)
	 *3. add data to factbase
	 *and:
	 *- check if the effect condition (if we have some) is fulfilled
	 *- check if an exception occurred
	 *
	 * @param  formula      Description of the Parameter
	 * @param  body         Description of the Parameter
	 * @param  currentPath  Description of the Parameter
	 * @param  varBindings  Description of the Parameter
	 * @param  opDef        Description of the Parameter
	 * @return              Description of the Return Value
	 */
	public static List parseMessage(Formula formula, Element body, String currentPath, Map varBindings, Map accumulatedBindings, OperationBinding opBinding, OpDef opDef) {

		try {
			if (formula instanceof ForallFormula) {
				ArrayList result = new ArrayList();

				ForallFormula forallFormula = (ForallFormula) formula;
				Term t = (Term) forallFormula.getParameters().get(0);
				// for now, we assume only ONE param
				Variable quantifiedVar = (Variable) t;
				log.debug("FORALL - quantified var = " + quantifiedVar);
				VarDef vd = opDef.getDefForOutputVar(quantifiedVar.getString());
				log.debug("vd = "+vd);
				String xPathToVar = vd.getAbsPath(opBinding);

				log.debug("xPathToVarr = " + xPathToVar + ", curr elem=" + body.getName());

				XPath xPathEngine = (!xPathToVar.startsWith("//")) ?
				XPath.newInstance("/" + xPathToVar) : XPath.newInstance(xPathToVar);

				// a leading "//" to make us more fault tolerant
				Iterator elemIter = xPathEngine.selectNodes(body).iterator();

				while (elemIter.hasNext()) {
					// for every instance in the XML document (e.g. for every catalog item)
					Element bodySlice = (Element) elemIter.next();
					List tmp = parseMessage(forallFormula.getBody(), bodySlice, xPathToVar, varBindings, accumulatedBindings, opBinding, opDef);

					result.addAll(tmp);
				}

				return result;

			} else {
				// we can treat Conjunctions and negations and combinations thereof uniformally
				HashMap newVarBindings = new HashMap();

				String n = body.getName();

				// go through vardef's and see what info we can extract
				// from this body (or body slice)
				for (Iterator iter = opDef.getOutputVarDefs().values().iterator(); iter.hasNext(); ) {
					VarDef vd = (VarDef) iter.next();
					String vdMessage = vd.getMessage();

					String varName = vd.getName();
					String vdPart = vd.getPart();
					String vdPath = vd.getPath();
					String vdAbsPath = vd.getAbsPath(opBinding);
					String relPath = relativePath(vdAbsPath, currentPath);

					log.debug("extracting value for absolute path: " + vdAbsPath);
					log.debug("at current path: " + currentPath);
					log.debug("which correspondens to body: " + body.getName());
					log.debug("rel path would be:" + relPath);
					log.debug("parent is:" + body.getParent());

					XPath xPathEngine = XPath.newInstance(relPath);
					Iterator elemIter = xPathEngine.selectNodes(body).iterator();
					if (elemIter.hasNext()) {
						Object o = (Element) elemIter.next();

						if (o instanceof Element) {
							Element dataElement = (Element) o;
							String extractedValue = dataElement.getText();
							log.debug("extracted value for var " + varName + " from elem" + dataElement.getName() + " with value" + extractedValue);
							newVarBindings.put(varName, extractedValue);
						} else if (o instanceof Attribute) {
							Attribute dataAtt = (Attribute) o;
							String extractedValue = dataAtt.getValue();
							log.debug("extracted value for var " + varName + " from attr. " + dataAtt.getName() + " with value" + extractedValue);
							newVarBindings.put(varName, extractedValue);
						}
					}
				}

				log.debug("NEW VAR BINDINGS = "+newVarBindings);

				// now, having extracted all var-values we could, we try
				// to generate some facts

				HashMap mergedBindings = new HashMap();
				for (Iterator miter = varBindings.entrySet().iterator(); miter.hasNext(); ) {
					Map.Entry mme = (Map.Entry) miter.next();
					log.info("mm.getVAR="+mme.getValue().getClass().getName()+" "+mme.getValue());

					mergedBindings.put(mme.getKey(), mme.getValue());
					// from precondition / input
				}

				// now the new ones
				mergedBindings.putAll(newVarBindings);

				accumulatedBindings.putAll(newVarBindings);

				List newFacts = formula.cloneAndSubstitute(mergedBindings).expandToFacts();
				// update factbase
				//ExecEnvironment.getInstance().addAllToFactBase(newFacts);

				return newFacts;

			}
		} catch (JDOMException jde) {
			jde.printStackTrace();
		}

		//return newVarBindings;
		// however, this is not an exhaustive list of all bindings
		// but it should be possible to check for effect conditions
		//todo . no premature KB adding
		return null;
	}


	/**
	 *examples
	 *target = /foo/bar/mega, currentPath = /foo
	 *=> result = /bar/mega
	 *target = /foo/bar, currentPath = /foo/bar/mega/giga
	 *=> result = ../../ (or: parent::parent::)
	 *
	 * @param  targetPath   Description of the Parameter
	 * @param  currentPath  Description of the Parameter
	 * @return              Description of the Return Value
	 */
	protected static String relativePath(String targetPath, String currentPath) {
		StringTokenizer st1 = new StringTokenizer(targetPath, "/");
		ArrayList target = new ArrayList();
		while (st1.hasMoreTokens()) {
			target.add(st1.nextToken());
		}
		int targetSize = target.size();

		StringTokenizer st2 = new StringTokenizer(currentPath, "/");
		ArrayList current = new ArrayList();
		while (st2.hasMoreTokens()) {
			current.add(st2.nextToken());
		}
		int currentSize = current.size();

		int common;
		for (common = 0; common < targetSize; common++) {
			String e1 = (String) target.get(common);
			if (common < currentSize) {
				String e2 = (String) current.get(common);
				if (!e1.equals(e2)) {
					break;
				}
			} else {
				break;
			}
		}

		if ((common == targetSize) &&
				(common == currentSize)) {
			return ".";
		}

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < currentSize - common; i++) {
			buf.append("../");
			// abbrev. for "parent::"
		}

		for (int i = common; i < targetSize; i++) {
			buf.append((String) target.get(i));
			if (i < targetSize - 1) {
				buf.append("/");
			}
		}

		String result = buf.toString();
		return result;
	}


	// check if the condition is fulfilled
	/**
	 *  Description of the Method
	 *
	 * @param  effectCondition  Description of the Parameter
	 * @param  boundOutputVars  Description of the Parameter
	 * @return                  Description of the Return Value
	 */
	private static boolean checkEffectCondition(String effectCondition, String lang, Map boundOutputVars) {

		if(lang == null) {
			log.error("'lang' attribute of effect condition not set");
			throw new IllegalArgumentException("'lang' attribute of effect condition not set");
		}

		if(OpDef.BEANSHELL.equalsIgnoreCase(lang)) {

			bsh.Interpreter bsh = new bsh.Interpreter();
			try {
				bsh.set("output", boundOutputVars);

				Object o = bsh.eval(effectCondition);
				if(!(o instanceof java.lang.Boolean))
					throw new IllegalArgumentException("invalid expression in case statement - it does not evaluate to boolean!");

				return ((Boolean) o).booleanValue();
			} catch(bsh.EvalError ee) {
				throw new RuntimeException(ee);
			}

		} else if(OpDef.JEXL.equalsIgnoreCase(lang)) {

			// unfortunately commons JEXL does not support question marks in
			// variable names, so we have to do some preprocessing first
			effectCondition = StringUtils.replace(effectCondition, "?", "");
			log.debug("evaluating condition" + effectCondition);

			HashMap evalContext = new HashMap();
			for (Iterator iter = boundOutputVars.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry me = (Map.Entry) iter.next();
				String newkey = ((String) me.getKey()).substring(1);
				String newvalue = (String) me.getValue();
				log.debug("eval var/val pair: <" + newkey + "," + newvalue + ">");
				evalContext.put(newkey, newvalue);
			}

			try {

				Expression e = ExpressionFactory.createExpression(effectCondition);
				JexlContext jc = JexlHelper.createContext();
				jc.setVars(evalContext);

				Object o = e.evaluate(jc);

				if (o == null) {
					throw new IllegalArgumentException("Error in expression " + effectCondition);
				}

				if (o instanceof Boolean) {
					return ((Boolean) o).booleanValue();
				} else {
					throw new IllegalArgumentException("Expression is not BOOLEAN: " + effectCondition);
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			log.error("language not supported");
			throw new IllegalArgumentException();
		}

	}


}
