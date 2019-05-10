package org.mcm.sws.process;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;

import org.apache.commons.digester.*;
import org.xml.sax.SAXException;

import org.apache.log4j.Logger;

import org.mcm.sws.util.*;

/**
 * This class represents am executable SESMA process. This class also provides a
 * parser of SESMA XML process descriptions
 *
 * @author    Joachim Peer
 */

public class Process extends Activity {
	private static Logger log = Logger.getLogger(Process.class);
		
	protected Map namespaces;
	protected LinkedHashMap activities;
	protected List directChildren;	
	protected List processInputs;
	
	public Process() {
		namespaces = new HashMap();
		activities = new LinkedHashMap();
		directChildren = new ArrayList();
		processInputs = new ArrayList();
	}
	
	public void addNamespace(String prefix, String uri) {
		System.out.println("Process: addNamespace "+prefix+","+uri);
		namespaces.put(prefix, uri);
	}
	
	public void addNamespaces(List l) {
		for(Iterator iter = l.iterator(); iter.hasNext(); ) {
			Namespace ns = (Namespace) iter.next();
			addNamespace(ns.getPrefix(), ns.getURI());
		}
	}
	
	public Map getNamespaces() {
		return namespaces;
	}
	
	public void addActivity(Activity activity) {
		directChildren.add(activity);
	}
	
	public Activity getActivity(String[] qName) {
		if(targetNamespace.equals(qName[0])) { 
			return (Activity) activities.get(qName[1]);
		} else return null;
	}
	
	public Activity getActivity(String actQName) {
		String[] qName = XMLUtils.splitQName(actQName, targetNamespace, namespaces);
		
		if(targetNamespace.equals(qName[0])) { 
			return (Activity) activities.get(qName[1]);
		} else {
			Process otherProcess = ProcessRegistry.getProcess(qName[0]);
			if(otherProcess != null) {
				return otherProcess.getActivity(qName);
			} else return null;
		}
	}		
	
	public void addProcessInput(ProcessInput pi) {
		processInputs.add(pi);
	}
	
	public List getProcessInputs() {
		return processInputs;
	}
	
	public void register(Activity activity) {
		activity.setRootProcess(this);
		activity.setTargetNamespace(this.targetNamespace);
		activities.put(activity.getId(), activity);
	}
	
	public void execute() throws ExecutionException {
		execute(new ProcessInstance(this));
	}
	
	public void execute(ProcessInstance pi) throws ExecutionException {
		System.out.println("About to execute Process "+getId());
		
		Activity firstActivity = (Activity) directChildren.get(0);
		if(firstActivity!=null) {
			firstActivity.execute(pi);
		} else 
			throw new ExecutionException("No activity to execute in this process!");
	}	
	

	public static Process parse(String input) 
	throws IOException {	
		Process process = new Process();

		InputStream is = Files.getInputStream(input);
		
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(input);		
			Element root = doc.getRootElement();
			Namespace rootns = root.getNamespace();
			process.addNamespace(rootns.getPrefix(), rootns.getURI());	
			process.addNamespaces(root.getAdditionalNamespaces());		
			is.close();
		} catch(JDOMException jde) {
			throw new IOException(jde.getMessage());
		} 

		//process.setAdditionalNamespaces(additionalNamespaces);
				
		// Initialize a new Digester instance
		org.mcm.sws.util.NSDigester digester = new org.mcm.sws.util.NSDigester();
		digester.setNamespaceAware(true);
		digester.setLogger(new org.apache.commons.logging.impl.Log4JLogger(log));
		digester.push(process);

		// set schema attributes
		digester.addSetProperties("*/process", "id", "id");
		digester.addSetProperties("*/process", "targetNamespace", "targetNamespace");		

		digester.addCallMethod("*/import", "importProcess", 1);
		digester.addCallParam("*/import", 0, "url");

		// var inputs

		/*
			<input>
				<label>enter ean number</label>
				<for activity="buy" var="?ean">
			</input>		
		*/
		
		digester.addObjectCreate("*/input", "org.mcm.sws.process.ProcessInput");
		digester.addSetProperties("*/input", "id", "id");
		digester.addCallMethod("*/input/label", "setLabel", 0);
		digester.addCallMethod("*/input/for", "addFor", 2);
		digester.addCallParam("*/input/for", 0, "activity");
		digester.addCallParam("*/input/for", 1, "var");
		digester.addSetNext("*/input", "addProcessInput");

		
		
		// sequence
		digester.addObjectCreate("*/sequence", "org.mcm.sws.process.Sequence");
		digester.addSetProperties("*/sequence", "id", "id");
		digester.addSetRoot("*/sequence", "register");
		digester.addSetNext("*/sequence", "addActivity");
		

		// fork
		digester.addObjectCreate("*/fork", "org.mcm.sws.process.Fork");
		digester.addSetProperties("*/fork", "id", "id");
		digester.addSetRoot("*/fork", "register");		
		digester.addSetNext("*/fork", "addActivity");				

		// goal
		digester.addObjectCreate("*/goal", "org.mcm.sws.process.GoalActivity");
		digester.addSetProperties("*/goal", "id", "id");		
		digester.addCallMethod("*/goal", "setSpecification", 0);
		digester.addSetRoot("*/goal", "register");				
		digester.addSetNext("*/goal", "addActivity");		
		
		// invoke
		digester.addObjectCreate("*/invoke", "org.mcm.sws.process.InvokeActivity");
		digester.addSetProperties("*/invoke", "id", "id");
		digester.addSetProperties("*/invoke", "op", "op");
		digester.addSetRoot("*/invoke", "register");				
		digester.addSetNext("*/invoke", "addActivity");

		// receive
		digester.addObjectCreate("*/receive", "org.mcm.sws.process.ReceiveActivity");
		digester.addSetProperties("*/receive", "id", "id");
		digester.addSetProperties("*/receive", "op", "op");
		digester.addSetRoot("*/receive", "register");				
		digester.addSetNext("*/receive", "addActivity");	

		// reply
		digester.addObjectCreate("*/reply", "org.mcm.sws.process.ReplyActivity");
		digester.addSetProperties("*/reply", "id", "id");
		digester.addSetProperties("*/reply", "op", "op");
		digester.addSetRoot("*/reply", "register");				
		digester.addSetNext("*/reply", "addActivity");
	
		// var/params
		digester.addCallMethod("*/var", "addVar", 3);
		digester.addCallParam("*/var", 0, "name");
		digester.addCallParam("*/var", 1, "value");
		digester.addCallParam("*/var", 2, "ref");
	
		// local
		digester.addObjectCreate("*/local", "org.mcm.sws.process.LocalActivity");
		digester.addSetProperties("*/local", "id", "id");
		digester.addSetProperties("*/local", "lang", "lang");		
		digester.addCallMethod("*/local", "setCode", 0);
		digester.addSetRoot("*/local", "register");				
		digester.addSetNext("*/local", "addActivity");			

		// jump
		digester.addObjectCreate("*/jump", "org.mcm.sws.process.Jump");
		digester.addSetProperties("*/jump", "id", "id");
		digester.addSetProperties("*/jump", "ref", "ref");
		digester.addSetRoot("*/jump", "register");				
		digester.addSetNext("*/jump", "addActivity");			

		// jump
		digester.addObjectCreate("*/wait", "org.mcm.sws.process.WaitActivity");
		digester.addSetProperties("*/wait", "id", "id");
		digester.addSetProperties("*/wait", "ms", "ms");
		digester.addSetRoot("*/wait", "register");				
		digester.addSetNext("*/wait", "addActivity");				
		
		/*
		<switch>
		  <case ref="buy" lang="beanshell">getSomeValue() > 20</case>
			<case ref="end" lang="beanshell">getSomeValue() < 20</case>
		</switch>
		*/
		
		// switch 
		digester.addObjectCreate("*/switch", "org.mcm.sws.process.Switch");
		digester.addSetProperties("*/switch", "id", "id");					
		digester.addSetRoot("*/switch", "register");				
		digester.addSetNext("*/switch", "addActivity");			
		// case
		digester.addObjectCreate("*/switch/case", "org.mcm.sws.process.Case");
		digester.addCallMethod("*/switch/case", "setExpression", 0);
		digester.addSetProperties("*/switch/case", "ref", "ref");		
		digester.addSetProperties("*/switch/case", "lang", "lang");		
		digester.addSetNext("*/switch/case", "addCase");
		
		try {
			is = Files.getInputStream(input);
			digester.parse(is);
			ProcessRegistry.addProcess(process, input);
			System.out.println("adding process at ns="+process.getTargetNamespace());
			return process;
		} catch (org.xml.sax.SAXException e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException(e1.toString());
		}
		

	}
	
	public void importProcess(String url) throws IOException {
		if(!ProcessRegistry.isDocumentLoaded(url)) {
			Process p = parse(url);
			ProcessRegistry.addProcess(p, url);
		}
	}
	
	// TO DO : import annotation
	
}
