package org.mcm.sws.xmlschema.facets;

import java.util.*;
import java.io.*;

public abstract class XSFacet  {
	protected boolean fixed;
	protected String value;
	
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	public boolean getFixed() {
		return fixed;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
