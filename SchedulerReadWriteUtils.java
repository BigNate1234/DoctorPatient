/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author nfurman
 *
 */
public class SchedulerReadWriteUtils {

	public final static String DOB_FORMAT = "yyyy-MM-dd";
	
	protected final static String ROOT = "root";
	protected final static String DOCTOR = "doctor";
	protected final static String PATIENT = "patient";
	protected final static String VISIT = "visit";
	protected final static String VISIT_DATE = "visitDate";
	protected final static String DOCTOR_ID = "doctorId";
	protected final static String PATIENT_ID = "patientId";
	protected final static String VISIT_ID = "visitId";
	protected final static String SPECIALTY = "specialty";
	protected final static String ACTIVE = "active";
	
	protected final static String PERSONAL_DATA = "data";
	protected final static String SSN = "SSN";
	protected final static String DOB = "dob";
	protected final static String NAME = "name";
	protected final static String FIRST_NAME = "firstName";
	protected final static String MIDDLE_INITIAL = "middleInitial";
	protected final static String LAST_NAME = "lastName";
	
	protected final static String NAMESPACE = "http://www.miami.edu/cis324/xml/scheduling";
	protected final static String SCHEMA_INSTANCE_PREFIX = "xsi";
	protected final static String SCHEMA_INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
	protected final static String SCHEMA_LOCATION_ATTRNAME = "schemaLocation";
	protected final static String SCHEMA_FILE_NAME = "scheduling.xsd";
	
	public static String readCharacters(XMLEventReader eventReader, String elementName) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read a " + elementName + " but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(elementName)) {
			throw new IllegalStateException("Attempting to read a " + elementName + " at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
//System.out.println(eventReader.nextEvent().asCharacters());
String chars = eventReader.nextEvent().toString();
		//String chars = eventReader.nextEvent().asCharacters().getData();
		return chars;
	}
	
	public static Date readDate(XMLEventReader eventReader, String elementName, String dateFormat) throws XMLStreamException {
		XMLEvent firstEvent = eventReader.nextEvent(); // gets the next event
		// first make sure that the current event is the start element of name
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read a " + elementName + " but not a start element: found event of type " + firstEvent.getEventType());
		}
		else if (!firstEvent.asStartElement().getName().getLocalPart().equals(elementName)) {
			throw new IllegalStateException("Attempting to read a " + elementName + " at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		String dateStr = eventReader.nextEvent().asCharacters().getData();
		DateFormat df = new SimpleDateFormat(dateFormat);
		try {
			df.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar date = df.getCalendar();
		Date d = date.getTime();
		return d;
	}
	
	///////////////////////
	
	public static Characters getIndentation(XMLEventFactory eventFactory, int level) {
		// returns an object with as many tabs as needed to indent to the value specified by the input parameter
		char[] tabs = new char[level];
		Arrays.fill(tabs, '\t'); // fill the number of tabs
		return eventFactory.createIgnorableSpace(String.valueOf(tabs)); // and create an ignorable space
	}

	public static void writeNode(XMLEventFactory eventFactory, XMLEventWriter eventWriter, 
			String name, String value, int level) throws XMLStreamException {
		// Create Start node
		eventWriter.add(getIndentation(eventFactory, level));
		StartElement startElement = eventFactory.createStartElement("", "", name);
		eventWriter.add(startElement);
		// Create Content
		Characters charValue = eventFactory.createCharacters(value);
		eventWriter.add(charValue);
		// Create End node
		EndElement endElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(endElement);
		// line feed
		eventWriter.add(eventFactory.createIgnorableSpace("\n"));
	}

	public static void writeDate(XMLEventFactory eventFactory, XMLEventWriter eventWriter, String name, Date date, int level) throws XMLStreamException {
		// write the date in the specific date format required by XML Schema
		DateFormat df = new SimpleDateFormat(DOB_FORMAT); // ignore time zones for simplicity
		String dateStr = df.format(date.getTime());
		writeNode(eventFactory, eventWriter, name, dateStr, level);
	}
}
