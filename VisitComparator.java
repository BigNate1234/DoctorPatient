/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.util.Comparator;
import java.util.Date;

/**
 * @author nathan
 *
 */
public class VisitComparator<V, T> implements Comparator<Visit<V,T>>{
	// compares the dates in the VisitImpl class
	// make abstract so can't instantiate

	@Override
	public int compare(Visit<V, T> arg0, Visit<V, T> arg1) { // this is used for Visit<Patient, Doctor> AND Visit<Integer, Integer>
		if (arg0 == null || arg1 == null) {
			System.err.println("Can't compare null objects [VisitComparator.compare]");
			return 0;
		}
		Date date0 = arg0.getDate();
		Date date1 = arg1.getDate();
		
		return Utilities.compareDates(date0, date1);
	}
}
