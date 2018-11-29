/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.Serializable;

/**
 * @author nfurman
 *
 */
public class Name implements Serializable {
	private static final long serialVersionUID = 1607814978420352974L;
	
	public String fName, MI, lName;
	
	public Name(String f, String mi, String l) {
		fName = f;
		MI = mi;
		lName = l;
	}
	
	public Name(String name) {
		fName = name.substring(0, name.indexOf(' '));
		lName = name.substring(name.indexOf(' ') + 1,name.length());
	}
	
	public String getFirstName() {
		return fName;
	}
	public String getMiddleInitial() {
		return MI;
	}
	public String getLastName() {
		return lName;
	}
	
	public void setName(String name) {
		this.fName = name.substring(0, name.indexOf(' '));
		this.lName = name.substring(name.indexOf(' ') + 1,name.length());
	}
	
	public void setfName(String fName) {
		this.fName = fName;
	}
	
	public void setMiddleInitial(String m) {
		this.MI = m;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	@Override
	public String toString() {
		return fName + " " + lName;
	}
	
	public boolean equals(Object other) {
		if(this == other) return true; // only if passing same object as that object calling method is same
	      
	       if(other == null || (this.getClass() != other.getClass())){ // has to be of same class
	           return false;
	       }
	      
	       Name n = (Name) other;
	       return  (this.getFirstName() != null && fName.equals(n.getFirstName())) &&
	    		   (this.getLastName()  != null && lName.equals(n.getLastName()));
	}
	
	public int hashCode() {
		int result = 0;
		result = 31*result + (getFirstName() !=null ? getFirstName().hashCode() : 0); 
		result = 31*result + (getLastName()  !=null ? getLastName().hashCode() : 0);
		return result;
	}
}
