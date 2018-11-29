/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

/**
 * @author nfurman
 *
 */
public class SchedulerXMLWriterUtils extends SchedulerReadWriteUtils{
	
	public static void writeSchedulingXML(String outFile, SchedulerData officeData) throws XMLStreamException, IOException {
	    // Create a XMLOutputFactory
	    XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	    // Create XMLEventWriter
	    Path outputFilePath = Paths.get(outFile);
	    Writer outputFile = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8);
	    XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(outputFile);
	    // Create an XMLEventFactory
	    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	    // Create and write Start Tag
	    StartDocument startDocument = eventFactory.createStartDocument("UTF-8", "1.0");
	    eventWriter.add(startDocument);
	    // put a linefeed for readability
	    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
	    // create the root element
	    StartElement root = eventFactory.createStartElement("", "", ROOT);
		eventWriter.add(root);
	    eventWriter.setDefaultNamespace(NAMESPACE); // set the default namespace for the root before adding it
		// add any other namespaces to the root
	    eventWriter.add(eventFactory.createNamespace(NAMESPACE));
	    eventWriter.add(eventFactory.createNamespace(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_NS));
	    // add the schema attributes to the root element 
	    String schemaLocationArg = NAMESPACE + " " + SCHEMA_FILE_NAME;
	    eventWriter.add(eventFactory.createAttribute(SCHEMA_INSTANCE_PREFIX, SCHEMA_INSTANCE_NS, SCHEMA_LOCATION_ATTRNAME, schemaLocationArg));
	    // put a linefeed for readability
	    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
		// iterate over the list of students and create an element for each
		for (Doctor d : officeData.getDocList()) {
			writeDoctor(eventFactory, eventWriter, d, 1); // write the student with one level of indentation
		    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
		}
		for (Patient p : officeData.getPatList()) {
			writePatient(eventFactory, eventWriter, p, 1); // write the student with one level of indentation
		    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
		}
		for (Visit<Integer, Integer> v : officeData.getVisits()) {
			writeVisit(eventFactory, eventWriter, v, 1); // write the student with one level of indentation
		    eventWriter.add(eventFactory.createIgnorableSpace("\n"));
		}
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();
	}
	
	public static void writeDoctor(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Doctor d, int level) throws XMLStreamException {
		// writes a single doctor through to the XML event writer
		// create the doctor start element
		eventWriter.add(getIndentation(eventFactory, level));
	    StartElement doctorStart = eventFactory.createStartElement("", "", DOCTOR);
	    eventWriter.add(doctorStart);
	    // create the id attribute
	    // note the use of Integer.toString to get a string representation
	    Attribute doctorId = eventFactory.createAttribute(DOCTOR_ID, Integer.toString(d.getDoctorID()));
	    Attribute active = eventFactory.createAttribute(ACTIVE, String.valueOf(d.isActive()));
	    eventWriter.add(doctorId);
	    eventWriter.add(active);
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	    // now create the nested elements
	    writeName(eventFactory, eventWriter, d.getName(), level + 1);
	    writeData(eventFactory, eventWriter, d.getData(), level + 1);
	    writeSpecialty(eventFactory, eventWriter, d.getSpeciality(), level + 1);
	    // create the student end element
		eventWriter.add(getIndentation(eventFactory, level));
	    EndElement doctorEnd = eventFactory.createEndElement("", "", DOCTOR);
	    eventWriter.add(doctorEnd);
	}
	
	public static void writePatient(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Patient p, int level) throws XMLStreamException {
		// writes a single patient through to the XML event writer
		// create the doctor start element
		eventWriter.add(getIndentation(eventFactory, level));
	    StartElement patientStart = eventFactory.createStartElement("", "", PATIENT);
	    eventWriter.add(patientStart);
	    // create the id attribute
	    // note the use of Integer.toString to get a string representation
	    Attribute patientId = eventFactory.createAttribute(PATIENT_ID, Integer.toString(p.getPatientID()));
	    Attribute active = eventFactory.createAttribute(ACTIVE, String.valueOf(p.isActive()));
	    eventWriter.add(patientId);
	    eventWriter.add(active);
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	    // now create the nested elements
	    writeName(eventFactory, eventWriter, p.getName(), level + 1);
	    writeData(eventFactory, eventWriter, p.getData(), level + 1);
	    // create the student end element
		eventWriter.add(getIndentation(eventFactory, level));
	    EndElement patientEnd = eventFactory.createEndElement("", "", DOCTOR);
	    eventWriter.add(patientEnd);
	}
	
	public static void writeName(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Name n, int level) throws XMLStreamException {
		// first, write as many tabs as levels needed
		eventWriter.add(getIndentation(eventFactory, level)); // getIndentation extended (inherited) from SchedulerReadWriteUtils
		// start element
		eventWriter.add(eventFactory.createStartElement("", "", NAME));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
		// first name
		writeNode(eventFactory, eventWriter, FIRST_NAME, n.getFirstName(), level+1);
		// middle initial
		writeNode(eventFactory, eventWriter, MIDDLE_INITIAL, n.getMiddleInitial(), level+1);
		// last name
		writeNode(eventFactory, eventWriter, LAST_NAME, n.getLastName(), level+1);
		// end element
		eventWriter.add(getIndentation(eventFactory, level)); // also indent it
		eventWriter.add(eventFactory.createEndElement("", "", NAME));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	}
	
	public static void writeData(XMLEventFactory eventFactory, XMLEventWriter eventWriter, PersonalData d, int level) throws XMLStreamException {
		// first, write as many tabs as levels needed
		eventWriter.add(getIndentation(eventFactory, level)); // getIndentation extended (inherited) from SchedulerReadWriteUtils
		// start element
		eventWriter.add(eventFactory.createStartElement("", "", PERSONAL_DATA));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
		// first name
		writeDate(eventFactory, eventWriter, DOB, d.getDOB(), level+1);
		// last name
		writeNode(eventFactory, eventWriter, SSN, d.getSSN(), level+1);
		// end element
		eventWriter.add(getIndentation(eventFactory, level)); // also indent it
		eventWriter.add(eventFactory.createEndElement("", "", PERSONAL_DATA));
		eventWriter.add(eventFactory.createIgnorableSpace("\n")); // line feed for readability
	}
	
	public static void writeSpecialty(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Specialties spec, int level) throws XMLStreamException {
		writeNode(eventFactory, eventWriter, SPECIALTY, spec.toString(), level);
	}
	
	public static void writeVisit(XMLEventFactory eventFactory, XMLEventWriter eventWriter, Visit<Integer, Integer> v, int level) throws XMLStreamException {
		eventWriter.add(getIndentation(eventFactory, level));
	    StartElement visitStart = eventFactory.createStartElement("", "", VISIT);
	    eventWriter.add(visitStart);
	    // create the id attribute
	    // note the use of Integer.toString to get a string representation
	    Attribute patientId = eventFactory.createAttribute(PATIENT_ID, Integer.toString(v.getVisitor()));
	    Attribute doctorId = eventFactory.createAttribute(DOCTOR_ID, Integer.toString(v.getHost()));
	    Attribute visitId = eventFactory.createAttribute(VISIT_ID, Integer.toString(v.getVisitID()));
	    Attribute visitDate = eventFactory.createAttribute(VISIT_DATE, v.getDateFormatted());
	    
	    eventWriter.add(patientId);
	    eventWriter.add(doctorId);
	    eventWriter.add(visitId);
	    eventWriter.add(visitDate);
	    EndElement visitEnd = eventFactory.createEndElement("", "", VISIT);
	    eventWriter.add(visitEnd);
	}
}
