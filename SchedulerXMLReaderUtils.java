package edu.miami.cis324.hw4.nfurman;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author nfurman
 *
 */
public final class SchedulerXMLReaderUtils extends SchedulerReadWriteUtils {
	
	
	
	public static SchedulerData readSchedulingXML(String xmlFile) 
	throws IOException, XMLStreamException {
		SchedulerData toReturn = new SchedulerData();
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		Path xmlFilePath = Paths.get(xmlFile);
		Reader in = Files.newBufferedReader(xmlFilePath, StandardCharsets.UTF_8);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.peek(); 
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart() == (ROOT)) { // declared in XML
					// just read the next event, which should be an student
					event = eventReader.nextEvent(); // skip this event and read the next
				}
				// if we are at the top element for an student
				else if (startElement.getName().getLocalPart() == (DOCTOR)) {
					Doctor e = readDoctor(eventReader);
					toReturn.addDoctor(e);
				}
				else if (startElement.getName().getLocalPart() == (PATIENT)) {
					Patient e = readPatient(eventReader);
					toReturn.addPatient(e);
				}
				else if (startElement.getName().getLocalPart() == (VISIT)) {
					Visit<Integer, Integer> e = readVisit(eventReader);
					toReturn.addVisit(e);
				}
				else {
					System.err.println("Unrecognized element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			else {
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		eventReader.close();
		return toReturn;
	}

	public static Doctor readDoctor(XMLEventReader eventReader) throws XMLStreamException {
		Integer doctorId = 0;
		Doctor doc = null;
		Name name = null;
		PersonalData data = null;
		Specialties spec = null;
		boolean active = true;
		boolean finished = false;
		
		XMLEvent firstEvent = eventReader.nextEvent();
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read object but not a start element: found event of type " + firstEvent.getEventType());
		} else if (!firstEvent.asStartElement().getName().getLocalPart().equals(DOCTOR)) {
			throw new IllegalStateException("Attempting to read object at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		
		Iterator<Attribute> attributes = firstEvent.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().getLocalPart().equals(DOCTOR_ID)) {
				doctorId = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(ACTIVE)) {
				if (attribute.getValue().equals("true")) {
					active = true;
				} else {
					active = false;
				}
			}
			else {
				System.err.println("Found unknown attribute, ignoring; found: " + attribute.getName());
			}
		}
		
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(NAME)) {
					name = readName(eventReader);
				}
				else if (startElement.getName().getLocalPart().equals(PERSONAL_DATA)) {
					data = readData(eventReader, DOB_FORMAT);
				}
				else if (startElement.getName().getLocalPart().equals(SPECIALTY)) {
					spec = readSpec(eventReader);
				}
				else {
					System.err.println("Unrecognized element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(DOCTOR)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					doc = new DoctorImpl(name, data, spec, doctorId, active);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		
		return doc;
	}

	public static Patient readPatient(XMLEventReader eventReader) throws XMLStreamException {
		Integer patientId = 0;
		Patient pat = null;
		Name name = null;
		PersonalData data = null;
		boolean active = true;
		boolean finished = false;
		
		XMLEvent firstEvent = eventReader.nextEvent();
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read object but not a start element: found event of type " + firstEvent.getEventType());
		} else if (!firstEvent.asStartElement().getName().getLocalPart().equals(PATIENT)) {
			throw new IllegalStateException("Attempting to read object at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		
		Iterator<Attribute> attributes = firstEvent.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().getLocalPart().equals(PATIENT_ID)) {
				patientId = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(ACTIVE)) {
				if (attribute.getValue().equals("true")) {
					active = true;
				} else {
					active = false;
				}
			}
			else {
				System.err.println("Found unknown attribute, ignoring; found: " + attribute.getName());
			}
		}
		
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(NAME)) {
					name = readName(eventReader);
				}
				else if (startElement.getName().getLocalPart().equals(PERSONAL_DATA)) {
					data = readData(eventReader, DOB_FORMAT);
				}
				else {
					System.err.println("Unrecognized element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(PATIENT)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					pat = new PatientImpl(name, data, patientId, active);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		
		return pat;
	}
	
	public static Visit<Integer, Integer> readVisit(XMLEventReader eventReader) throws XMLStreamException {
		Integer patientId = 0;
		Integer doctorId = 0;
		Integer visitId = 0;
		Date date = null;
		Visit<Integer, Integer> visit = null;
		boolean finished = false;
		
		XMLEvent firstEvent = eventReader.nextEvent();
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read object but not a start element: found event of type " + firstEvent.getEventType());
		} else if (!firstEvent.asStartElement().getName().getLocalPart().equals(VISIT)) {
			throw new IllegalStateException("Attempting to read object at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		
		Iterator<Attribute> attributes = firstEvent.asStartElement().getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			if (attribute.getName().getLocalPart().equals(PATIENT_ID)) {
				patientId = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(DOCTOR_ID)) {
				doctorId = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(VISIT_ID)) {
				visitId = Integer.valueOf(attribute.getValue());
			}
			else if (attribute.getName().getLocalPart().equals(VISIT_DATE)) {
				date = Utilities.createDate(attribute.getValue());
			}
			else {
				System.err.println("Found unknown attribute, ignoring; found: " + attribute.getName());
			}
		}
		
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student

			if (event.isStartElement() || event.isEndElement()) {
				event = eventReader.nextEvent();
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart().equals(VISIT)) {
					visit = new VisitImpl<Integer, Integer>(patientId, doctorId, date, visitId);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		
		return visit;
	}
	
	public static Name readName(XMLEventReader eventReader) throws XMLStreamException {
		String fName = null, MI= null, lName = null;
		Name name = null;
		boolean finished = false;
		
		XMLEvent firstEvent = eventReader.nextEvent();
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read object but not a start element: found event of type " + firstEvent.getEventType());
		} else if (!firstEvent.asStartElement().getName().getLocalPart().equals(NAME)) {
			throw new IllegalStateException("Attempting to read object at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals(FIRST_NAME)) {
					fName = readCharacters(eventReader, FIRST_NAME);
				}
				else if (startElement.getName().getLocalPart().equals(LAST_NAME)) {
					lName = readCharacters(eventReader, LAST_NAME);
				}
				else if (startElement.getName().getLocalPart().equals(MIDDLE_INITIAL)) {
					MI = readCharacters(eventReader, MIDDLE_INITIAL);
				}
				else {
					System.err.println("Unrecognized element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(NAME)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					name = new Name(fName, MI, lName);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		
		return name;
	}
	
	public static PersonalData readData(XMLEventReader eventReader, String dobFormat) throws XMLStreamException {
		String social = null;
		Date dob = null;
		PersonalData data = null;
		boolean finished = false;
		
		XMLEvent firstEvent = eventReader.nextEvent();
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read object but not a start element: found event of type " + firstEvent.getEventType());
		} else if (!firstEvent.asStartElement().getName().getLocalPart().equals(PERSONAL_DATA)) {
			throw new IllegalStateException("Attempting to read object at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		
		while (!finished) {
			XMLEvent event = eventReader.peek(); // peek to have the event reader remain before the next start element
			// check the start elements for the nested elements inside the student
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
//System.out.println("Start element: " + startElement.getName().getLocalPart());
				if (startElement.getName().getLocalPart().equals(DOB)) {
					dob = readDate(eventReader, DOB, DOB_FORMAT);
				}
				else if (startElement.getName().getLocalPart().equals(SSN)) {
					social = readCharacters(eventReader, SSN);
				}
				else {
					System.err.println("Unrecognized element, ignoring: " + startElement.getName());
					event = eventReader.nextEvent(); // skip this event and read the next
				}
			}
			// check the end elements to find where the name element is closed
			else if (event.isEndElement()) {
				event = eventReader.nextEvent(); // retrieve the event
				EndElement endElement = event.asEndElement();
				// when the end element is the name element, create the name return object;
				if (endElement.getName().getLocalPart().equals(PERSONAL_DATA)) {
					// Schema makes these required, so they must exist
					// would be a good practice to check for existence anyways
					data = new PersonalData(dob, social);
					finished = true;
				}
			}
			else {
				// ignore other events, such as character events
				event = eventReader.nextEvent(); // skip this event and read the next
			}
		}
		
		return data;
	}
	
	public static Specialties readSpec(XMLEventReader eventReader) throws XMLStreamException {		
		XMLEvent firstEvent = eventReader.nextEvent();
		if (!firstEvent.isStartElement()) {
			throw new IllegalStateException("Attempting to read object but not a start element: found event of type " + firstEvent.getEventType());
		} else if (!firstEvent.asStartElement().getName().getLocalPart().equals(SPECIALTY)) {
			throw new IllegalStateException("Attempting to read object at the wrong start element: found " + firstEvent.asStartElement().getName());
		}
		
		XMLEvent event = eventReader.peek();
		return Specialties.getFromString(event.toString());
	}
	
}
