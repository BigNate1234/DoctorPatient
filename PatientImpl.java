/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.Serializable;

/**
 * @author nathan
 *
 */
public class PatientImpl extends PersonImpl implements Serializable, Patient { 
	private static final long serialVersionUID = -31107888102384344L;
	
	private Integer patientID;
	private static Integer PID = 100;
	
	public PatientImpl(Name n, PersonalData d, Integer PID, boolean a) {
		super(n, d, a);
		patientID = PID;
	}
	
	public PatientImpl(Name n, PersonalData d, boolean a) {
		super(n, d, a);
		patientID = PID;
		PID++;
	}
	
	public Integer getPatientID() {
		return patientID;
	}
	
	public boolean equals(Object other) {
		if(this == other) return true; // only if passing same object as that object calling method is same
	      
	       if(other == null || (this.getClass() != other.getClass())){ // has to be of same class
	           return false;
	       }
	      
	       PatientImpl p = (PatientImpl) other; // type-casting other to PatientImpl
	       return  (this.getName() 		!= null && this.getName().equals(p.getName())) &&
	    		   (this.getData() 		!= null && this.getData().equals(p.getData())) &&
	    		   (this.isActive() == p.isActive()) &&
	    		   (this.getPatientID() != null && this.patientID.equals(p.getPatientID()));
	}
	
	
	public int hashCode() {
		int result = 0;
		result = 31*result + patientID;
		result = 31*result + (getName() !=null ? getName().hashCode() : 0);
		result = 31*result + (getData()  !=null ? getData().hashCode() : 0);

		return result;
	}
	
	/*
	public String toString() {
		return this.getName().toString() + "//" + this.getData().toString() + "//" + this.getPatientID() + "//" + this.hashCode();
	}
	*/
	
	public String toString() {
		return this.getName().toString();
	}
	
	public void printPatient() { // prints with proper formatting
		System.out.println("\tFirst name:\t" + this.getName().getFirstName());
		System.out.println("\tLast name:\t" + this.getName().getLastName());
		System.out.println("\tSSN:\t\t" + this.getData().getSSN());
		System.out.println("\tAge:\t\t" + this.getData().getAge());
	}
}
