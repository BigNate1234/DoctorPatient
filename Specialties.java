/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

/**
 * @author nathan
 *
 */
public enum Specialties {
	GENERAL_MEDICINE, PEDIATRICS, ONCOLOGY;
	
	public static Specialties getFromString(String spec) {
		if (spec==null) {
			System.err.println("No specialty provided...");
			return null;
		}
		return valueOf(spec.toUpperCase());
	}
}
