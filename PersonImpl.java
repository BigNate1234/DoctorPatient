/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.Serializable;

/**
 * @author nfurman
 *
 */
public abstract class PersonImpl implements Serializable, Person {
	private static final long serialVersionUID = 9045432511241991541L;
	
	private Name name;
	private PersonalData data;
	private boolean active = true;
	
	public PersonImpl(Name n, PersonalData d) {
		name = n;
		data = d;
	}
	
	public PersonImpl(Name n, PersonalData d, boolean a) {
		name = n;
		data = d;
		active = a;
	}
	
	public Name getName() {
		return name;
	}
	
	public PersonalData getData() {
		return data;
	}
	
	public void setActive(boolean state) {
		this.active = state;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setName(Name name) {
		this.name = name;
	}

	public void setData(PersonalData data) {
		this.data = data;
	}

	public boolean equals(Object other) {
		if(this == other) return true; // only if passing same object as that object calling method is same
	      
	       if(other == null || (this.getClass() != other.getClass())){ // has to be of same class
	           return false;
	       }
	      
	       PersonImpl p = (PersonImpl) other;
	       return  (this.getName() 		!= null && this.getName().equals(p.getName())) &&
	    		   (this.getData() 		!= null && this.getData().equals(p.getData())) &&
	    		   (this.isActive() == p.isActive());
	}
	
	public int hashCode() {
		int result = 0;
		result = 31*result + (getName() !=null ? getName().hashCode() : 0);
		result = 31*result + (getData()  !=null ? getData().hashCode() : 0);

		return result;
	}
}
