/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

/**
 * @author nathan
 *
 */
public interface Patient extends Person{

	public Integer getPatientID();
	public void printPatient();
	public void setActive(boolean b);
}
