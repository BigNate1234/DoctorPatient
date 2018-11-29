/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

/**
 * @author nathan
 *
 */
public interface Doctor extends Person, Comparable<Doctor> { // documentation says to implement Comparable<T> on doctor class
	public Integer getDoctorID();
	public Specialties getSpeciality();
	public boolean specEqual(String spec);
	public void setActive(boolean b);
}
