/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.Serializable;
import java.util.Date;

/**
 * @author nfurman
 *
 */
public class PersonalData implements Serializable {
	private static final long serialVersionUID = 2619001623109935970L;
	
	private Date DOB;
	private String SSN;
	private Integer age;
	
	public PersonalData(Date d, String s) {
		DOB = d;
		SSN = s;
		age = Utilities.getAge(DOB);
	}

	public Date getDOB() {
		return DOB;
	}

	public String getSSN() {
		return SSN;
	}
	
	public Integer getAge() {
		return age;
	}
	
	public void setDOB(Date dOB) {
		DOB = dOB;
		age = Utilities.getAge(DOB);
	}

	public void setSSN(String sSN) {
		SSN = sSN;
	}

	public String toString() {
		return this.getDOB().toString() + "//" + this.getSSN() + "//" + this.hashCode();
	}
	
	public boolean equals(Object other) {
		if(this == other) return true; // only if passing same object as that object calling method is same
	      
	       if(other == null || (this.getClass() != other.getClass())){ // has to be of same class
	           return false;
	       }
	      
	       PersonalData d = (PersonalData) other; // type-casting other to PatientImpl
	       return  (this.getDOB() != null && DOB.equals(d.getDOB())) &&
	    		   (this.getSSN() != null && SSN.equals(d.getSSN())) &&
	    		   (this.getAge() != null && age.equals(d.getAge()));
	}
	
	public int hashCode() {
		int result = 0;
		result = 31*result + (getDOB() !=null ? getDOB().hashCode() : 0); 
		result = 31*result + (getSSN()  !=null ? getSSN().hashCode() : 0);
		return result;
	}
}
