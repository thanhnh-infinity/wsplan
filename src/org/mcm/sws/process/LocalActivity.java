package org.mcm.sws.process;

import org.mcm.sws.ExecEnvironment;

/**
 * An activity that is executed locally. The behavior of the acitivity
 * is defined by source code in some programming/scripting language (currently
 * supported is Beanshell/Java)
 *
 * @author    Joachim Peer
 */

public class LocalActivity extends AtomicActivity {

	protected static final String BEANSHELL = "beanshell";
	protected static final String JAVA = "java";	
	
	protected String lang;
	protected String code;
	
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public String getLang() {
		return this.lang;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}	
	
	public void execute(ProcessInstance pi) throws ExecutionException {
		System.out.println("About to execute Local activity"+getId());
		
		if(lang == null) throw new ExecutionException("no language specified in local activity "+getId());
		
		if(BEANSHELL.equals(lang)) {
			bsh.Interpreter bsh = new bsh.Interpreter();
			try {
				bsh.set("env", ExecEnvironment.getInstance());
				bsh.eval(code);
				pi.addCompletedActivity(this, ProcessInstance.SUCCESS);
			} catch(bsh.EvalError ee) {
				throw new ExecutionException(ee.getMessage());
			}			
		} else {
			throw new ExecutionException("language '"+lang+"' is currently not supported");
		}
		
	}	

}
