/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author nfurman
 *
 * Class for many methods used by multiple classes
 * that don't need to be declared in any specific class
 */
public abstract class Utilities {

	static public String[] months = {"Janurary", "Feburary", "March", "April", "May", "June", "July", 
			"August", "September", "October", "November", "December"};
	
	/*
	 * Prints all doctors in list, similar for patients
	 */
	public static void printDoctors(ArrayList<Doctor> dList) {
		for (Doctor d:dList) {
			System.out.println(d.toString());
			System.out.println("---------");
		}
	}
	
	public static void printPatients(ArrayList<Patient> pList) {
		for (Patient p:pList) {
			System.out.println(p.toString());
			System.out.println("---------");
		}
	}
	
	
	/*
	 * Compares each entry in Doctor ArrayList and sees if doctors are the same
	 * Overloaded the methods with doctorID as well
	 */
	public static Doctor getDoctor(Doctor doc, ArrayList<Doctor> dList) {
		if (doc == null) {
			System.err.println("No doctor to get [Utilities.getDoctor]");
			return null;
		}
		
		for (Doctor d:dList) {
			if (d.equals(doc)) { // using equals of doctorInterface (defined in doctorImpl)
				return d;
			}
		}
		return null;
	}
	
	public static Doctor getDoctor(Integer doctorID, ArrayList<Doctor> dList) { 
		// given doctor ID to compare
		if (doctorID == null) {
			System.err.println("No doctor to get [Utilities.getDoctor]");
			return null;
		}
		
		for (Doctor d:dList) {
			if (d.getDoctorID().equals(doctorID)) {
				return d;
			}
		}
		return null;
	}
	
	// similar to getDoctor
	public static Patient getPatient(Patient pat, ArrayList<Patient> pList) {
		if (pat == null) {
			System.err.println("No patient to get [Utilities.getPatient]");
			return null;
		}
		
		for (Patient p:pList) {
			if (p.equals(pat)) {
				return p;
			}
		}
		return null;
	}
	
	public static Patient getPatient(Integer patientID, ArrayList<Patient> pList) { 
		// given doctor ID to compare
		if (patientID == null) {
			System.err.println("No patient to get [Utilities.getPatient]");
			return null;
		}
		
		for (Patient p:pList) {
			if (p.getPatientID().equals(patientID)) {
				return p;
			}
		}
		return null;
	}
	
	public static Visit<Integer, Integer> getVisit(Integer visitID, ArrayList<Visit<Integer, Integer>> vList) {
		if (visitID == null) {
			System.err.println("No visit to get [Utilities.getVisit]");
			return null;
		}
		
		for (Visit<Integer, Integer> v:vList) {
			if (v.getVisitID().equals(visitID)) {
				return v;
			}
		}
		
		return null;
	}
	
	public static Date createDate(String date) { // in format given in project requirements
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d;
		try {
			d = sdf.parse(date); // creates date using string and format set
		} catch (ParseException e) {
			d = new Date(); // date created as empty, display error
			System.err.println("Date not in correct format: " + date);
		}
		
		return d;
	}
	
	@SuppressWarnings("deprecation")
	// the "get" methods from date are not really the best, suppressing warnings
	public static Integer getAge(Date DOB) {
		return 2018 - DOB.getYear() - 1900; // getYear() returns int with +1900
	}
	
	public static long getDifferenceDays(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			System.err.println("No date to get difference of [Utilities.getDifferenceDays]");
			return 0;
		}
		
	    long diff = d2.getTime() - d1.getTime();
	    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}
	
	public static int compareDates(Date date0, Date date1) {
		if (date1 == null || date0 == null) {
			System.err.println("No date to compare [Utilities.compareDates]");
			return 0;
		}
		
		// date0 earlier date1 returns negative, same date is zero, date0 later than date1 return positive
		if (date0.after(date1)) {
			return 1;
		} else if (date0.before(date1)) {
			return -1;
		} else { // no other option but equal
			return 0;
		}
	}
	
	public static <E> void swap(int index1, int index2, List<E> list) { // swaps the index of two items in list		
		E temp = list.get(index1);
		list.set(index1, list.get(index2));
		list.set(index2, temp);
	}
	
	public static <V, T> void sortVisits(ArrayList<Visit<V, T>> visits) {
		// visits with earliest dates come first, using Collection's sort method
		Collections.sort(visits, new VisitComparator<V, T>());
	}
	
	public static void printVisitsByID(ArrayList<Visit<Integer, Integer>> visitByID, Map<Integer, Patient> patientIdMap, Map<Integer, Doctor> doctorIdMap) {
		// order visits before doing anything else
		sortVisits(visitByID);
		
		for (Visit<Integer, Integer> v : visitByID) {			
			Doctor d = doctorIdMap.get(v.getHost()); // second integer
			Patient p = patientIdMap.get(v.getVisitor()); // first integer
			
			if (d == null) {
				System.err.println("Visit has no doctor [SchedulePatients.printVisitsByID()]");
				return;
			}
			if (p == null) {
				System.err.println("Visit has no patient [SchedulePatients.printVisitsByID()]");
				return;
			}
			
			if (d.getName() == null) {
				System.err.println("Name is null... [SchedulePatients.printVisitsByID()]");
				return;
			}
			
			if (d.getSpeciality() == null) {
				System.err.println("Speciality is null... [SchedulePatients.printVisitsByID()]");
				return;
			}
			
			if (v.getDate() == null) {
				System.err.println("No date for visit... [SchedulePatients.printVisitsByID()]");
				return;
			}
			
			Date date = v.getDate(); // gets date of visit
			Calendar visitCal = Calendar.getInstance();
			visitCal.setTime(date);
			int month = visitCal.get(Calendar.MONTH);
			int day = visitCal.get(Calendar.DATE);
			int year = visitCal.get(Calendar.YEAR);
			
			Calendar todayCal = Calendar.getInstance();
			todayCal.set(Calendar.HOUR_OF_DAY, 0); // makes cal today
			Date today = todayCal.getTime();
			
			long dayDiff = Utilities.getDifferenceDays(today, date);
			
			System.out.println("Visit date:\t\t" + months[month] + " " + day + ", " + year);
			System.out.println("Doctor:\t\t\t" + d.getName().getFirstName() + " " + d.getName().getLastName());
			System.out.println("Specialty:\t\t" + d.getSpeciality().toString());
			System.out.println("Days until visit:\t" + dayDiff);
			System.out.println("Patient:");
			p.printPatient();
			
			System.out.println();
		}
	}
}
