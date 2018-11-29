/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;
import javax.xml.stream.XMLStreamException;

/**
 * @author nfurman
 *
 */
public class Client {

	// for I/O
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// the GUI
	private ClientGUI cg;
	
	// the server, the port and the username
	private String server, username;
	private int port;
	
	private SchedulerData data = new SchedulerData(); // to hold all officeData

	Client(String server, int port, String username, ClientGUI cg) {
		this.server = server;
		this.port = port;
		this.username = username;
		this.cg = cg;
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket(server, port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		
		// once it starts read data
		try {
			data = getData();
			//cg.append("Sucessfully read office data");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		// also read all data
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(cg == null)
			System.out.println(msg);      // println in console mode
		else
			cg.append(msg );		// append to the ClientGUI JTextArea (or whatever)
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(Message msg) {
		
		try {
			if (msg == null) {
				System.err.println("No Message... [Client.sendMessage()]");
			} else {
				sOutput.writeObject(msg);
			}
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
		
		//System.out.println(msg.getMessage() + "//" + msg.getType());
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	public void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
        cg.connectionFailed();
	}
	
	// asks server for all data for patients, doctors, and visits
	// no idea how to get info from server
	public SchedulerData getData() throws IOException, XMLStreamException {
		sendMessage(new Message(Message.GET_DATA, ""));
		
		// to fall back on if server can't update data
	/*	
		data = new SchedulerData();
		
		// not finished, reads directly as if access to data
		String fileName = "resources/schedulerData.xml";
		try {
			data = SchedulerXMLReaderUtils.readSchedulingXML(fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file " + fileName);
			e.printStackTrace();
			return null;
		}
		*/
		
		return data;
	}
	
	public ArrayList<Doctor> activeDocList() {
		// assumes data already there
		
		ArrayList<Doctor> allDocs = data.getDocList();
		ArrayList<Doctor> activeDocs = new ArrayList<Doctor>();
		
		for (Doctor d:allDocs) {
			if (d.isActive()) {
				activeDocs.add(d); // creating ArrayList of all active docs
			}
		}
		
		return activeDocs;	
	}
	
	public ArrayList<Patient> activePatList() {
		// assumes data already there
		
		ArrayList<Patient> allPats = data.getPatList();
		ArrayList<Patient> activePats = new ArrayList<Patient>();
		
		for (Patient p:allPats) {
			if (p.isActive()) {
				activePats.add(p);
			}
		}
		
		return activePats;
	}
	
	public ArrayList<Doctor> inactiveDocList() {
		// assumes data already there
		
		ArrayList<Doctor> allDocs = data.getDocList();
		ArrayList<Doctor> inactiveDocs = new ArrayList<Doctor>();
		
		for (Doctor d:allDocs) {
			if (!d.isActive()) {
				inactiveDocs.add(d); // creating ArrayList of all inactive docs
			}
		}
		
		return inactiveDocs;	
	}
	
	public ArrayList<Patient> inactivePatList() {
		// assumes data already there
		
		ArrayList<Patient> allPats = data.getPatList();
		ArrayList<Patient> inactivePats = new ArrayList<Patient>();
		
		for (Patient p:allPats) {
			if (!p.isActive()) {
				inactivePats.add(p);
			}
		}
		
		return inactivePats;
	}
	
	public Object[][] reducedData(boolean pastIncluded) {
		// assumes data already there, fills with rows that hold doctors, patients, and visit dates
		// only for active doctors and patients
		
		/*
		 * { {"John Smith", "Bono", "2018-5-16"},
		 * {"Mary Jones", "Lenord Skinard", "2018-6-17"} }
		 * 
		 */
		ArrayList<Visit<Integer, Integer>> visits = data.getVisits();
		ArrayList<String> docNames = new ArrayList<String>();
		ArrayList<String> patNames = new ArrayList<String>();
		ArrayList<String> visitDates = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // ignore time zones for simplicity
		
		for (Visit<Integer, Integer> v:visits) {
			if (pastIncluded){ // want all of them
				Doctor d = Utilities.getDoctor(v.getHost(), activeDocList());
				Patient p = Utilities.getPatient(v.getVisitor(), activePatList());
				if (d != null && p != null) { // need to have doctor and patient that is active
					docNames.add(d.toString());
					// add the string of the active doctor who's ID equals the visit's host ID (doctor)
					patNames.add(p.toString());
				}
				visitDates.add(df.format(v.getDate()));
			}
			else if (Utilities.compareDates(new Date(), v.getDate()) < 0) { // also day in future
				Doctor d = Utilities.getDoctor(v.getHost(), activeDocList());
				Patient p = Utilities.getPatient(v.getVisitor(), activePatList());
				if (d != null && p != null) { // need to have doctor and patient that is active
					docNames.add(d.toString());
					// add the string of the active doctor who's ID equals the visit's host ID (doctor)
					patNames.add(p.toString());
				}
				visitDates.add(df.format(v.getDate()));
			}
		}
		
		Object[][] reducedData = new Object[docNames.size()][3];
		// names for rows, three columns
		for (int i = 0; i < docNames.size(); i++) {
			reducedData[i][0] = docNames.get(i);
			reducedData[i][1] = patNames.get(i);
			reducedData[i][2] = visitDates.get(i);
		}
		return reducedData;
	}
	
	// gives 2d array of visit data for specified doctor with an option for including past or just future visits
	public Object[][] doctorData(Doctor d, boolean pastIncluded) {
		// assumes data already there, fills with rows that hold doctors, patients, and visit dates
		// only for active doctors and patients
		
		/*
		 * { {"John Smith", "Bono", "2018-5-16"},
		 * {"Mary Jones", "Lenord Skinard", "2018-6-17"} }
		 * 
		 */
		ArrayList<Visit<Integer, Integer>> visits = data.getVisits();
		ArrayList<String> docNames = new ArrayList<String>();
		ArrayList<String> patNames = new ArrayList<String>();
		ArrayList<String> visitDates = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // ignore time zones for simplicity
		
		for (Visit<Integer, Integer> v:visits) {			
			if (pastIncluded){ // want all of them
				if (v.getHost().equals(d.getDoctorID())) { // if the host (doctor) is the doctor you want
					Patient p = Utilities.getPatient(v.getVisitor(), activePatList());
					if (p != null) { // need to have doctor and patient that is active
						docNames.add(d.toString());
						// add the string of the active doctor who's ID equals the visit's host ID (doctor)
						patNames.add(p.toString());
					}
					visitDates.add(df.format(v.getDate()));
				}
			}
			else if (Utilities.compareDates(new Date(), v.getDate()) < 0) { // only days in future
				if (v.getHost().equals(d.getDoctorID())) { // if the host (doctor) is the doctor you want
					Patient p = Utilities.getPatient(v.getVisitor(), activePatList());
					if (p != null) { // need to have doctor and patient that is active
						docNames.add(d.toString());
						// add the string of the active doctor who's ID equals the visit's host ID (doctor)
						patNames.add(p.toString());
					}
					visitDates.add(df.format(v.getDate()));
				}
			}
		}
		
		Object[][] reducedData = new Object[docNames.size()][3];
		// names for rows, three columns
		for (int i = 0; i < docNames.size(); i++) {
			reducedData[i][0] = docNames.get(i);
			reducedData[i][1] = patNames.get(i);
			reducedData[i][2] = visitDates.get(i);
		}
		return reducedData;
	}
	
	public Object[][] patientData(Patient p, boolean pastIncluded) {
		// assumes data already there, fills with rows that hold doctors, patients, and visit dates
		// only for active doctors and patients
		
		/*
		 * { {"John Smith", "Bono", "2018-5-16"},
		 * {"Mary Jones", "Lenord Skinard", "2018-6-17"} }
		 * 
		 */
		ArrayList<Visit<Integer, Integer>> visits = data.getVisits();
		ArrayList<String> docNames = new ArrayList<String>();
		ArrayList<String> patNames = new ArrayList<String>();
		ArrayList<String> visitDates = new ArrayList<String>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // ignore time zones for simplicity
		
		for (Visit<Integer, Integer> v:visits) {			
			if (pastIncluded){ // want all of them
				if (v.getVisitor().equals(p.getPatientID())) { // if the host (doctor) is the doctor you want
					Doctor d = Utilities.getDoctor(v.getHost(), activeDocList());
					if (d != null) { // need to have doctor and patient that is active
						docNames.add(d.toString());
						// add the string of the active doctor who's ID equals the visit's host ID (doctor)
						patNames.add(p.toString());
					}
					visitDates.add(df.format(v.getDate()));
				}
			}
			else if (Utilities.compareDates(new Date(), v.getDate()) < 0) { // only days in future
				if (v.getVisitor().equals(p.getPatientID())) { // if the host (doctor) is the doctor you want
					Doctor d = Utilities.getDoctor(v.getHost(), activeDocList());
					if (d != null) { // need to have doctor and patient that is active
						docNames.add(d.toString());
						// add the string of the active doctor who's ID equals the visit's host ID (doctor)
						patNames.add(p.toString());
					}
					visitDates.add(df.format(v.getDate()));
				}
			}
		}
		
		Object[][] reducedData = new Object[docNames.size()][3];
		// names for rows, three columns
		for (int i = 0; i < docNames.size(); i++) {
			reducedData[i][0] = docNames.get(i);
			reducedData[i][1] = patNames.get(i);
			reducedData[i][2] = visitDates.get(i);
		}
		return reducedData;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Visit<Integer, Integer>[] visitData() {
		ArrayList<Visit<Integer, Integer>> visits = data.getVisits();
		
		Visit[] reducedData = new Visit[visits.size()];
		
		for (int i = 0; i < visits.size(); i++) {
			if (Utilities.getDoctor(visits.get(i).getHost(), data.getDocList()).isActive()
					&& Utilities.getPatient(visits.get(i).getVisitor(), data.getPatList()).isActive()) {
				// only visits for active doctors and patients
				reducedData[i] = visits.get(i);
			}
		}

		return (Visit[]) reducedData;
	}
	
	
	public static void main(String[] args) {
		System.out.println("No need for main method here");
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 * if we have a GUI or simply System.out.println() it in console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					Object o = sInput.readObject();
					String msg = "";
					
					if (o.getClass().equals(msg.getClass())) { // if string...
						msg = (String) sInput.readObject().toString();
						// if console mode print the message and add back the prompt
						cg.append(msg);
					}
					if (o.getClass().equals(data.getClass())) {
						// server just sent officeData
						data = (SchedulerData) o;
					}
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					cg.connectionFailed();
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
