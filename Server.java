/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.stream.XMLStreamException;

/**
 * @author nfurman
 *
 */
public class Server {

	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	
	private SchedulerData data;
	private final static String INPUT_FILE = "resources/schedulerData.xml";
	private final static String OUTPUT_FILE = "resources/schedulerData.out.xml";
	

	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port) {
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) {
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
	}
	
	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
				al.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	@SuppressWarnings("resource") // leak of a resource from the new Socket, ignore
	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	/*
	 * Display an event (not a message) to the console or the GUI
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	/*
	 *  to broadcast a message to all Clients
	 */
	private synchronized void broadcast(Object message) {
		// add HH:mm:ss and \n to the message
		String time = sdf.format(new Date());
		
		if (message.getClass().equals(time.getClass())) { // if string, add time
			message = time + " " + message + "\n"; // re-write message
		}
		
		// display message on console or GUI
		sg.appendMessages(message.toString() + "\n");     // append in the message window
		
		// we loop in reverse order in case we would have to remove a Client
		// because it has disconnected
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(message)) {
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
	}

	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id) {
				al.remove(i);
				return;
			}
		}
	}
	
	// after every creation, update the data
	public void updateData(SchedulerData d) {
		// writes whatever data is stored to output file
		try {
			SchedulerXMLWriterUtils.writeSchedulingXML(INPUT_FILE,  d);
//			SchedulerXMLWriterUtils.writeSchedulingXML(OUTPUT_FILE,  d);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// read data
		try {
			data = SchedulerXMLReaderUtils.readSchedulingXML(INPUT_FILE);
//			data = SchedulerXMLReaderUtils.readSchedulingXML(OUTPUT_FILE); // output becomes new input
		} catch (FileNotFoundException e) {
			System.out.println("Could not find file " + OUTPUT_FILE);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		// updates data for client
		for(int i = al.size(); --i >= 0;) {
			ClientThread ct = al.get(i);
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(data)) {// sends data to client
				al.remove(i);
				display("Disconnected Client " + ct.username + " removed from list.");
			}
		}
		broadcast(data);
	}
	
	/*
	 *  To run as a console application just open a console window and: 
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	/** One instance of this thread will run for each client */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for disconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		Message cm;
		// the date I connect
		String date;

		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			/* Creating both Data Stream */
			try
			{
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
//
// do login check here
//
				// read the username
				username = (String) sInput.readObject();
				display(username + " just connected.");
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			// have to catch ClassNotFoundException
			// but I read a String, I am sure it will work
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (Message) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the string part of the Message
				String message = cm.getMessage();
				String[] info = null;
				Doctor d = null;
				Patient p = null;
				Visit<Integer, Integer> v = null;
				
				// Switch on the type of message receive
				switch(cm.getType()) {
				case Message.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case Message.CREATE_DOC:
					info = message.split(":");
					d = new DoctorImpl(
							new Name(info[0], info[1], info[2]),
							new PersonalData(Utilities.createDate(info[3]), info[4]),
							info[5],
							true // when creating automatically active
							);
					data.addDoctor(d);
					
					display("Creating doctor: " + d.toString());				
					broadcast("Creating doctor: " + d.toString());
	
					// once doc created update data
					updateData(data);
					break;
					
				case Message.CREATE_PAT:
					info = message.split(":");
					p = new PatientImpl(
							new Name(info[0], info[1], info[2]),
							new PersonalData(Utilities.createDate(info[3]), info[4]),
							true // when creating automatically active
							);
					data.addPatient(p);
					
					broadcast("Creating patient: " + p.toString());
					display("Creating patient: " + p.toString());
					updateData(data);
					break;
					
				case Message.CREATE_VISIT:
					info = message.split(":");
					v = new VisitImpl<Integer, Integer>(
							Integer.parseInt(info[1]),
							Integer.parseInt(info[0]),
							Utilities.createDate(info[2])
							);
					data.addVisit(v);
					broadcast("Creating visit: " + v.toString());
					display("Creating visit: " + v.toString());
					updateData(data);
					break;
				
				case Message.ADD_REMOVED_DOC:
					d = Utilities.getDoctor(Integer.parseInt(message), data.getDocList());
					d.setActive(true);
					broadcast("Re-adding doctor: " + d.toString());
					display("Re-adding doctor: " + d.toString());
					updateData(data);
					break;
				
				case Message.ADD_REMOVED_PAT:
					p = Utilities.getPatient(Integer.parseInt(message), data.getPatList());
					p.setActive(true);
					broadcast("Re-adding patient: " + p.toString());
					display("Re-adding patient: " + p.toString());
					updateData(data);
					break;
					
				case Message.EDIT_DOC:
					info = message.split(":");
					data.getDocList().remove(Utilities.getDoctor(Integer.parseInt(info[6]), data.getDocList()));
					// remove doctor from list before add him back
					d = new DoctorImpl(
							new Name(info[0], info[1], info[2]),
							new PersonalData(Utilities.createDate(info[3]), info[4]),
							info[5],
							Integer.parseInt(info[6]), // making ID same as before
							true // when creating automatically active
							);
					data.addDoctor(d);
					
					display("Edit doctor: " + d.toString());				
					broadcast("Edit doctor: " + d.toString());
	
					// once doc edit complete update data
					updateData(data);
					break;
					
				case Message.EDIT_PAT:
					info = message.split(":");
					data.getPatList().remove(Utilities.getPatient(Integer.parseInt(info[5]), data.getPatList()));
					// remove doctor from list before add him back
					p = new PatientImpl(
							new Name(info[0], info[1], info[2]),
							new PersonalData(Utilities.createDate(info[3]), info[4]),
							Integer.parseInt(info[5]), // making ID same as before
							true // when creating automatically active
							);
					data.addDoctor(d);
					
					display("Edit patient: " + p.toString());				
					broadcast("Edit patient: " + p.toString());
	
					// once doc edit complete update data
					updateData(data);
					break;
					
				case Message.EDIT_VISIT:
					info = message.split(":");
					
					data.getVisits().remove(Utilities.getVisit(Integer.parseInt(info[0]), data.getVisits()));
		
					// remove visit from list before adding back
					v = new VisitImpl<Integer, Integer>(
							Integer.parseInt(info[3]),
							Integer.parseInt(info[2]),
							Utilities.createDate(info[1]),
							Integer.parseInt(info[0])
							);
					data.addVisit(v);
					
					display("Edit visit: " + v.toString());				
					broadcast("Edit visit: " + v.toString());
	
					// once edit complete update data
					updateData(data);
					break;
				
				case Message.REMOVE_DOC:
					d = Utilities.getDoctor(Integer.parseInt(message), data.getDocList());
					d.setActive(false);
					broadcast("Removing doctor: " + d.toString());
					display("Removing doctor: " + d.toString());
					updateData(data);
					break;
					
				case Message.REMOVE_PAT:
					p = Utilities.getPatient(Integer.parseInt(message), data.getPatList());
					p.setActive(false);
					broadcast("Removing patient: " + p.toString());
					display("Removing patient: " + p.toString());
					updateData(data);
					break;
					
				case Message.REMOVE_VISIT:
					v = Utilities.getVisit(Integer.parseInt(message), data.getVisits());
					data.getVisits().remove(v);
					broadcast("Removing visit: " + v.toString());
					display("Removing visit: " + v.toString());
					updateData(data);
					break;
					
				case Message.GET_DATA:
					// read data and send to client
					try {
						data = SchedulerXMLReaderUtils.readSchedulingXML(INPUT_FILE);
					} catch (FileNotFoundException e) {
						System.out.println("Could not find file " + INPUT_FILE);
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
					
					display("Data read.");
					
					for(int i = al.size(); --i >= 0;) {
						ClientThread ct = al.get(i);
						// try to write to the Client if it fails remove it from the list
						if(!ct.writeMsg(data)) {// sends data to client
							al.remove(i);
							display("Disconnected Client " + ct.username + " removed from list.");
						}
					}
					break;
					
				case Message.WRITE_DATA:
					// writes whatever data is stored to output file
					try {
						SchedulerXMLWriterUtils.writeSchedulingXML(OUTPUT_FILE,  data);
					} catch (XMLStreamException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				default:
					display(username + ": Message not recognized [" + cm.getType() + "]");
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		}
		
		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		/*
		 * Write a String to the Client output stream
		 */
		private boolean writeMsg(Object msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}
	}
}
