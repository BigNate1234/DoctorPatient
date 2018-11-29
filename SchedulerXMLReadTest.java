/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

/**
 * @author nfurman
 *
 */
public class SchedulerXMLReadTest {

	private final static String INPUT_FILE = "resources/schedulerData.xml";
	private final static String OUTPUT_FILE = "resources/schedulerData.out.xml";
	
	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, XMLStreamException {
		SchedulerData officeData = new SchedulerData();
		try {
			officeData = SchedulerXMLReaderUtils.readSchedulingXML(INPUT_FILE);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file " + INPUT_FILE);
			e.printStackTrace();
			return;
		}
		
		// prints all visits using the Id's of doctors and patients
		Utilities.printVisitsByID(officeData.getVisits(), officeData.getPatientIdMap(), officeData.getDoctorIdMap());
		
		SchedulerXMLWriterUtils.writeSchedulingXML(OUTPUT_FILE, officeData);

	}

}
