package org.mcm.sws.process;

/**
 * a case element that is part of a swich (selection) element. 
 *
 * @author    Joachim Peer
 */

import org.mcm.sws.ExecEnvironment;

public class Case {
	protected String ref;
	protected String lang;
	protected	String expression;	
	
	/**
	 * Returns the value of ref.
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Sets the value of ref.
	 * @param ref The value to assign ref.
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}

	/**
	 * Returns the value of lang.
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Sets the value of lang.
	 * @param lang The value to assign lang.
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

	/**
	 * Returns the value of expression.
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the value of expression.
	 * @param expression The value to assign expression.
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	public boolean isSatisfied(ProcessInstance pi) throws ExecutionException {
		if(lang == null) throw new ExecutionException("no language specified in switch/case statement");
		
		if(LocalActivity.BEANSHELL.equals(lang)) {
			bsh.Interpreter bsh = new bsh.Interpreter();
			try {
				// make some essential data structures accessile to beanshell code
				bsh.set("pi", pi);
				bsh.set("env", ExecEnvironment.getInstance());
				
				Object o = bsh.eval(expression);
				if(!(o instanceof java.lang.Boolean)) throw new ExecutionException("invalid expression in case statement - it does not evaluate to boolean!");
				
				return ((Boolean) o).booleanValue();				
			} catch(bsh.EvalError ee) {
				throw new ExecutionException(ee.getMessage());
			}			
		} else {
			throw new ExecutionException("language '"+lang+"' is currently not supported");
		}				
	}
	
}
