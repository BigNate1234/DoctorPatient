/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author nfurman
 *
 */
public class SchedulerData implements Serializable {

	private static final long serialVersionUID = 7371097390632906230L;
	
	private ArrayList<Doctor> docList;
	private ArrayList<Patient> patList;
	private ArrayList<Visit<Integer, Integer>> visits;
	private Map<Integer, Patient> patientIdMap = new TreeMap<Integer, Patient>();
	private Map<Integer, Doctor> doctorIdMap = new TreeMap<Integer, Doctor>();
	
	SchedulerData() {
		docList = new ArrayList<Doctor>();
		patList = new ArrayList<Patient>();
		visits = new ArrayList<Visit<Integer, Integer>>();
	}
	
	public void addDoctor(Doctor d) {
		docList.add(d);
		doctorIdMap.put(d.getDoctorID(), d); // key is doctorID, value is the doctor
		return;
	}

	public void addPatient(Patient p) {
		patList.add(p);
		patientIdMap.put(p.getPatientID(), p); // for every patient, put its ID into the key and the patient into the value
		return;
	}
	
	public void addVisit(Visit<Integer, Integer> v) {
		visits.add(v);
		return;
	}
	
	public ArrayList<Doctor> getDocList() {
		return docList;
	}
	public ArrayList<Patient> getPatList() {
		return patList;
	}
	public ArrayList<Visit<Integer, Integer>> getVisits() {
		return visits;
	}
	
	public Map<Integer, Patient> getPatientIdMap() {
		return patientIdMap;
	}

	public Map<Integer, Doctor> getDoctorIdMap() {
		return doctorIdMap;
	}
	
	public String toString() {
		return "Doctors: "
				+ docList.size() +
				", Patients: "
				+ patList.size() +
				", Visits: "
				+ visits.size();
	}
}
