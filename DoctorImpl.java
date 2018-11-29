/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.Serializable;
import java.util.Date;

/**
 * @author nathan
 *
 */
public class DoctorImpl extends PersonImpl implements Serializable, Doctor{

	private static final long serialVersionUID = -2949435513451894830L;
	
	private Specialties speciality;
	private static Integer DID = 100;
	private Integer doctorID;
	
	public DoctorImpl(Name n, PersonalData d, String speciality, boolean a) {
		super(n,d,a);
		this.speciality = Specialties.getFromString(speciality);
		doctorID = DID;
		DID++;
	}
	
	public DoctorImpl(Name n, PersonalData d, Specialties speciality, boolean a) {
		super(n,d, a);
		this.speciality = speciality;
		doctorID = DID;
		DID++;
	}
	
	public DoctorImpl(Name n, PersonalData d, String speciality, Integer DID, boolean a) {
		super(n, d, a);
		this.speciality = Specialties.getFromString(speciality);
		doctorID = DID;
	}
	
	public DoctorImpl(Name n, PersonalData d, Specialties speciality, Integer DID, boolean a) {
		super(n, d, a);
		this.speciality = speciality;
		doctorID = DID;
	}
	
	public Integer getDoctorID() {
		return doctorID;
	}
	
	public Specialties getSpeciality() {
		return speciality;
	}
	
	public void setSpeciality(String speciality) {
		this.speciality = Specialties.getFromString(speciality);
	}
	
	public int compareTo(Doctor d) { // using this to order doctors, if negative return means need swap
		// last name, first name, DOB, then SSN
		String lName1 = this.getName().getLastName(), lName2 = d.getName().getLastName();
		String fName1 = this.getName().getFirstName(), fName2 = d.getName().getFirstName();
		Date date1 = this.getData().getDOB(), date2 = d.getData().getDOB();
		String ssn1 = this.getData().getSSN(), ssn2 = d.getData().getSSN();
		
		if (lName1.compareToIgnoreCase(lName2) > 0) { // if greater than zero, out of order
			return -1;
		} else if (lName1.equalsIgnoreCase(lName2)) { // equals go to next level
			if (fName1.compareToIgnoreCase(fName2) > 0) {
				return -1;
			} else if (fName1.equalsIgnoreCase(fName2)) {
				if (Utilities.compareDates(date1, date2) > 0) {
					return -1;
				} else if (Utilities.compareDates(date1, date2) == 0) {
					if (ssn1.compareToIgnoreCase(ssn2) > 0) {
						return -1;
					} else if (ssn1.equalsIgnoreCase(ssn2)) { // if all the same, return 0 (order doesn't matter)
						return 0;
					}
				}
			}
		}
		
		return 1; // if at any point in order, you're good
		
	}
	
	public boolean equals(Object other) {
		if(this == other) return true; // only if passing same object as that object calling method is same
	      
	       if(other == null || (this.getClass() != other.getClass())){ // has to be of same class and there has to be an object there
	           return false;
	       }
	      
	       DoctorImpl d = (DoctorImpl) other; // type-casting other to DoctorImpl
	       return  (this.getName() 		!= null && this.getName().equals(d.getName())) &&
	    		   (this.getData() 		!= null && this.getData().equals(d.getData())) &&
	    		   (this.getDoctorID() 	!= null && this.doctorID.equals(d.getDoctorID())) &&
	    		   (this.isActive() == d.isActive()) &&
	    		   (this.getSpeciality() != null && this.getSpeciality() == d.getSpeciality());
	}
	
	public int hashCode() {
		int result = 0;
		result = 31*result + doctorID;
		result = 31*result + (getName() !=null ? getName().hashCode() : 0);
		result = 31*result + (getData()  !=null ? getData().hashCode() : 0);
		result = 31*result + (speciality != null ? speciality.hashCode() : 0); // Specialty also changes hash code

		return result;
	}

	/*
	public String toString() {
		return this.getName().toString() + "//" + this.getData().toString() + "//" + this.getDoctorID() + "//" + this.getSpeciality() + "//" + this.hashCode();
	}
	*/
	
	public String toString() {
		return this.getName().toString();
	}
	
	public boolean specEqual(String spec) { // if specialties are equal return true
		if (speciality == Specialties.getFromString(spec)) {
			return true;
		}
		return false;
	}
}
