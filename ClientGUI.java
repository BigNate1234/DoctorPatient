/**
 * 
 */
package edu.miami.cis324.hw4.nfurman;

import javax.swing.*;
import javax.xml.stream.XMLStreamException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author nfurman
 *
 */
public class ClientGUI extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	// for displaying time
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	// will first hold "Username:", later on "Enter message"
	private JLabel label;

	// to hold the Username and later on the input data
	private JTextField tf;

	// to hold the server address and the port number
	private JTextField tfServer, tfPort;

	// buttons for different parts of the system
	private JButton send;
	private JButton logout;

	// if it is for connection
	private boolean connected;

	// the Client object
	private Client client;

	// the default port number
	private int defaultPort;
	private String defaultHost;
	
	// for putting data in
	private JPanel dataInput;
	
	// main panes
	private JPanel contentPane; // main pane
	private JPanel contentPaneLeft; // for selection and display
	private JPanel contentPaneRight; // for data input

	// for messages from server
	private JTextArea output;
	private JScrollPane scrollPane;
	
	// menu variables
	private JMenuBar menuBar;
	private JMenu createMenu, editMenu, removeMenu, listMenu, submenu;
	private JMenuItem mItem;
	
	// create/edit/remove buttons
	private JButton create;
	private JButton edit;
	private JButton remove;

	// Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
	
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		
		if(o == send) {
			if (!connected) { // not logged in yet, so have to log in
				// okay it is a connection request
				String username = tf.getText().trim();
				// empty username ignore it
				if(username.length() == 0)
					return;
				// empty serverAddress ignore it
				String server = tfServer.getText().trim();
				if(server.length() == 0)
					return;
				// empty or invalid port number, ignore it
				String portNumber = tfPort.getText().trim();
				if(portNumber.length() == 0)
					return;
				
				// at this point you have some data
	// need to add username and password validation
				int port = 0;
				try {
					port = Integer.parseInt(portNumber);
				}
				catch(Exception en) {
					return;   // nothing I can do if port number is not valid
				}

				// try creating a new Client with GUI
				client = new Client(server, port, username, this);
				
				// test if we can start the Client
				if(!client.start()) 
					return;
				
				display("Logged in...");
				tf.setText("");
				label.setText("Select menu item");
				connected = true;
				
				// selection still not editable because haven't selected menu item
				tf.setEditable(false); // have to select menu item first
				send.setEnabled(false);
				logout.setEnabled(true);
				createMenu.setEnabled(true);
				editMenu.setEnabled(true);
				removeMenu.setEnabled(true);
				listMenu.setEnabled(true);
				
				// disable the Server and Port JTextField
				tfServer.setEditable(false);
				tfPort.setEditable(false);
			} else {
				tf.setText("clicked send");
				display("clicked send button\n");
			}
			return;
		}
		
		if(o == logout) {
// not a real connectionFailed, want to get from server
//
// CHANGE LATER
//
//			connectionFailed();
			client.sendMessage(new Message(Message.LOGOUT, ""));
			client.disconnect();
			return;
		}

// create, edit, and remove have their own ActionListeners
// so if thrown something went wrong
		if (o == create) {
			System.out.println("Hit a create button");
			display("Hit a create button");
			
			contentPaneRight.removeAll();
			contentPaneRight.revalidate();
			contentPaneRight.repaint();
		}
		if (o == edit) {
			System.out.println("Hit an edit button");
			display("Hit an edit button");
			
			contentPaneRight.removeAll();
			contentPaneRight.revalidate();
			contentPaneRight.repaint();
		}
		if (o == remove) {
			System.out.println("Hit aremove button");
			display("Hit a remove button");
			
			contentPaneRight.removeAll();
			contentPaneRight.revalidate();
			contentPaneRight.repaint();
		}
		// at this point know it's not a send or logout button

		if (o.getClass().equals(mItem.getClass())) {		
			// selections for menu items
			JMenuItem input = (JMenuItem)(e.getSource());
		// create section
			if (input.getText().equals("Create Doctor")) {
				display("Selected create doctor");
				createDoctor();
			}
			if (input.getText().equals("Create Patient")) {
				display("Selected create patient");
				createPatient();
			}
			if (input.getText().equals("Create Visit")) {
				display("Selected create visit");
				createVisit();
			}
			
			if (input.getText().equals("Removed Doctor")) {
				display("Selected doctor in previous removal");
				reCreateDoctor();
			}
			if (input.getText().equals("Removed Patient")) {
				display("Selected patient in previous removal");
				reCreatePatient();
			}
			
		// edit section
			if (input.getText().equals("Edit Doctor")) {
				display("Selected doctor to edit");
				editDoctor();
			}
			if (input.getText().equals("Edit Patient")) {
				display("Selected patient to edit");
				editPatient();
			}
			if (input.getText().equals("Edit Visit")) {
				display("Selected visit to edit");
				editVisit();
			}
			
		// remove section
			if (input.getText().equals("Remove Doctor")) {
				display("Selected doctor in remove");
				removeDoctor();
			}
			if (input.getText().equals("Remove Patient")) {
				display("Selected patient in remove");
				removePatient();
			}
			if (input.getText().equals("Remove Visit")) {
				display("Selected visit in remove");
				removeVisit();
			}
			
		// Listing section
			if (input.getText().equals("All Visits")) {
				display("List all visits");
				listVisits(true);
			}
			if (input.getText().equals("Upcoming Visits")) {
				display("List all upcoming visits");
				listVisits(false);
			}
			
			if (input.getText().equals("All Visits for patient...")) {
				display("List all visits for patient");
				patientVisits(true);
			}
			if (input.getText().equals("Upcoming Visits for patient...")) {
				display("List all upcoming visits for patient");
				patientVisits(false);
			}
			
			if (input.getText().equals("All Visits for doctor...")) {
				display("List all visits for doctor");
				doctorVisits(true);
			}
			if (input.getText().equals("Upcoming Visits for doctor...")) {
				display("List all upcoming visits for doctor");
				doctorVisits(false);
			}
		}
	}

	public JMenuBar createMenu() {
		// want a create, edit, and remove options along with list and submenus for listing all visits, doctor, and patient

		// create new menu bar
		menuBar = new JMenuBar();

		// build first menu
		createMenu = new JMenu("Create");
		createMenu.setMnemonic(KeyEvent.VK_C); // type C to get to menu
		createMenu.getAccessibleContext().setAccessibleDescription("Create data");
		menuBar.add(createMenu); // have to add it at the end

		// add menu items
		// when hold down alt and press key, activates that menu item
		mItem = new JMenuItem("Create Doctor", KeyEvent.VK_D);
		mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.ALT_MASK));
		mItem.addActionListener(this);
		createMenu.add(mItem);

		mItem = new JMenuItem("Create Patient", KeyEvent.VK_P);
		mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.ALT_MASK));
		mItem.addActionListener(this);
		createMenu.add(mItem);

		mItem = new JMenuItem("Create Visit", KeyEvent.VK_V);
		mItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.ALT_MASK));
		mItem.addActionListener(this);
		createMenu.add(mItem);
		
		createMenu.addSeparator();
		submenu = new JMenu("Add removed item...");
		
		mItem = new JMenuItem("Removed Doctor");
		mItem.addActionListener(this);
		submenu.add(mItem);
		mItem = new JMenuItem("Removed Patient");
		mItem.addActionListener(this);
		submenu.add(mItem);
		
		createMenu.add(submenu);

		// build second menu
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E); // type E to get to menu
		editMenu.getAccessibleContext().setAccessibleDescription("Edit data");
		menuBar.add(editMenu); // have to add it at the end
		
		// add menu items
		mItem = new JMenuItem("Edit Doctor");
		mItem.addActionListener(this);
		editMenu.add(mItem);

		mItem = new JMenuItem("Edit Patient");
		mItem.addActionListener(this);
		editMenu.add(mItem);

		mItem = new JMenuItem("Edit Visit");
		mItem.addActionListener(this);
		editMenu.add(mItem);
		
		// build third menu
		removeMenu = new JMenu("Remove");
		removeMenu.setMnemonic(KeyEvent.VK_R); // type R to get to menu
		removeMenu.getAccessibleContext().setAccessibleDescription("Remove data");
		menuBar.add(removeMenu); // have to add it at the end

		// add menu items
		mItem = new JMenuItem("Remove Doctor");
		mItem.addActionListener(this);
		removeMenu.add(mItem);

		mItem = new JMenuItem("Remove Patient");
		mItem.addActionListener(this);
		removeMenu.add(mItem);

		mItem = new JMenuItem("Remove Visit");
		mItem.addActionListener(this);
		removeMenu.add(mItem);
		
		// build last menu
		listMenu = new JMenu("List...");
		listMenu.setMnemonic(KeyEvent.VK_L); // type L to get to menu
		listMenu.getAccessibleContext().setAccessibleDescription("List data");
		menuBar.add(listMenu); // have to add it at the end

		// add menu items
			submenu = new JMenu("Visit listing");
			mItem = new JMenuItem("All Visits");
			mItem.addActionListener(this);
			submenu.add(mItem);
			mItem = new JMenuItem("Upcoming Visits");
			mItem.addActionListener(this);
			submenu.add(mItem);

		listMenu.add(submenu);

			submenu = new JMenu("Patient Visits");
			mItem = new JMenuItem("All Visits for patient...");
			mItem.addActionListener(this);
			submenu.add(mItem);
			mItem = new JMenuItem("Upcoming Visits for patient...");
			mItem.addActionListener(this);
			submenu.add(mItem);

		listMenu.add(submenu);

			submenu = new JMenu("Doctor Visits");
			mItem = new JMenuItem("All Visits for doctor...");
			mItem.addActionListener(this);
			submenu.add(mItem);
			mItem = new JMenuItem("Upcoming Visits for doctor...");
			mItem.addActionListener(this);
			submenu.add(mItem);

		listMenu.add(submenu);
		
		logout = new JButton("Logout");
		logout.addActionListener(this);
		menuBar.add(logout);
		
		// can't access menu's before login
		createMenu.setEnabled(false);
		editMenu.setEnabled(false);
		removeMenu.setEnabled(false);
		listMenu.setEnabled(false);
		
		// can't log out before logged in
		logout.setEnabled(false);
				
		return menuBar;
	}

	public JPanel createPortDisplay() {
		// The panel with:
		JPanel panel = new JPanel(new GridLayout(3,1));
		// the server name and the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(defaultHost);
		tfPort = new JTextField("" + defaultPort);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
		panel.add(serverAndPort);
		
		JPanel login = new JPanel(new GridLayout(1,1));
		label = new JLabel("Enter your username:", SwingConstants.RIGHT);
		login.add(label);
		tf = new JTextField("Username");
		tf.setBackground(Color.WHITE);
		login.add(tf);
		send = new JButton("Send");
		send.addActionListener(this);
		login.add(send);
		
		panel.add(login, BorderLayout.SOUTH);

		return panel;
	}

	public JPanel createContent() {
		
		// Create the content-pane-to-be.
		contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		
		contentPaneLeft = new JPanel(new BorderLayout());
		contentPaneLeft.setOpaque(true);
		
		//contentPaneRight = new JPanel(new BorderLayout());
		contentPaneRight = new JPanel();
		contentPaneRight.setOpaque(true);
		

		// Create a scrolled text area.
		output = new JTextArea(20, 20);
		output.setEditable(false);
		scrollPane = new JScrollPane(output);

		//Add the items area to the left and right content pane.
		contentPaneLeft.add(scrollPane);
		
		contentPane.add(contentPaneLeft, BorderLayout.WEST);
		contentPane.add(contentPaneRight, BorderLayout.EAST);
		return contentPane;
	}

	public ClientGUI(String host, int port) {
		super("Office Client");
		defaultHost = host;
		defaultPort = port;

		//Create and set up the window.
		//JFrame frame = new JFrame("Office Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setJMenuBar(createMenu());
		this.getContentPane().add(createContent(), BorderLayout.CENTER);
		this.getContentPane().add(createPortDisplay(), BorderLayout.SOUTH);
		
		setSize(600,600);
		setVisible(true);
		tf.requestFocus();
	}
	
	public void createDoctor() {
		dataInput = new JPanel(new GridLayout(9, 2));
		
		JLabel fNameLabel = new JLabel("First Name: ");
		JLabel lNameLabel = new JLabel("Last Name: ");
		JLabel MILabel = new JLabel("Middle Initial: ");
		JLabel DOBYearLabel = new JLabel("DOB Year: ");
		JLabel DOBMonthLabel = new JLabel("DOB Month: ");
		JLabel DOBDayLabel = new JLabel("DOB Day: ");
		JLabel SSNLabel = new JLabel("SSN: ");
		
		JTextField fNameInput = new JTextField("John");
		JTextField lNameInput = new JTextField("Smith");
		JTextField MIInput = new JTextField("C");
		JTextField DOBYearInput = new JTextField("1990");
		JTextField DOBMonthInput = new JTextField("01");
		JTextField DOBDayInput = new JTextField("13");
		JTextField SSNInput = new JTextField("123-45-6789");
		
		JComboBox<Specialties> specs = new JComboBox<Specialties>(Specialties.values());
		
		JPanel fNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel lNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel MIPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel DOBYearPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel DOBMonthPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel DOBDayPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel SSNPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel specPanel = new JPanel(new GridLayout(1,1,1,3));
		
		fNamePanel.add(fNameLabel);
		fNamePanel.add(fNameInput);
		
		MIPanel.add(MILabel);
		MIPanel.add(MIInput);
		
		lNamePanel.add(lNameLabel);
		lNamePanel.add(lNameInput);
		
		DOBYearPanel.add(DOBYearLabel);
		DOBYearPanel.add(DOBYearInput);
		
		DOBMonthPanel.add(DOBMonthLabel);
		DOBMonthPanel.add(DOBMonthInput);
		
		DOBDayPanel.add(DOBDayLabel);
		DOBDayPanel.add(DOBDayInput);
		
		SSNPanel.add(SSNLabel);
		SSNPanel.add(SSNInput);
		
		specPanel.add(specs);
		
		dataInput.add(fNamePanel);
		dataInput.add(MIPanel);
		dataInput.add(lNamePanel);
		dataInput.add(DOBYearPanel);
		dataInput.add(DOBMonthPanel);
		dataInput.add(DOBDayPanel);
		dataInput.add(SSNPanel);
		dataInput.add(specPanel);
		
		create = new JButton("Create Doctor");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    		
	    	    JDialog.setDefaultLookAndFeelDecorated(true);
	    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to create doctor?", "Confirm",
	    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	    	    if (response == JOptionPane.CLOSED_OPTION ||
	    	    		response == JOptionPane.NO_OPTION) {
	    	      append("Create doctor cancelled");
	    	      return;
	    	    } else if (response == JOptionPane.YES_OPTION) {
			    		
			    	String fName = null, MI = null, lName = null, DOB = null, SSN = null, spec = null;
			    	
			    	display("Created doctor");
	
					fName = fNameInput.getText().trim();
					lName = lNameInput.getText().trim();
					MI = MIInput.getText().trim();
					DOB = DOBYearInput.getText().trim() + "-" +
							DOBMonthInput.getText().trim() + "-" +
							DOBDayInput.getText().trim();
					SSN = SSNInput.getText().trim();
					spec = specs.getSelectedItem().toString();
					
					client.sendMessage(new Message(Message.CREATE_DOC, fName + ":" + MI + ":" +
							lName + ":" + DOB + ":" + SSN + ":" + spec));
					
					contentPaneRight.removeAll();
					contentPaneRight.revalidate();
					contentPaneRight.repaint();
	    	    }
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void createPatient() {
		dataInput = new JPanel(new GridLayout(9, 2));
		
		JLabel fNameLabel = new JLabel("First Name: ");
		JLabel lNameLabel = new JLabel("Last Name: ");
		JLabel MILabel = new JLabel("Middle Initial: ");
		JLabel DOBYearLabel = new JLabel("DOB Year: ");
		JLabel DOBMonthLabel = new JLabel("DOB Month: ");
		JLabel DOBDayLabel = new JLabel("DOB Day: ");
		JLabel SSNLabel = new JLabel("SSN: ");
		
		JTextField fNameInput = new JTextField("John");
		JTextField lNameInput = new JTextField("Smith");
		JTextField MIInput = new JTextField("C");
		JTextField DOBYearInput = new JTextField("1990");
		JTextField DOBMonthInput = new JTextField("01");
		JTextField DOBDayInput = new JTextField("13");
		JTextField SSNInput = new JTextField("123-45-6789");
		
		JPanel fNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel lNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel MIPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel DOBYearPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel DOBMonthPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel DOBDayPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel SSNPanel = new JPanel(new GridLayout(1,2, 1, 3));
		
		fNamePanel.add(fNameLabel);
		fNamePanel.add(fNameInput);
		
		MIPanel.add(MILabel);
		MIPanel.add(MIInput);
		
		lNamePanel.add(lNameLabel);
		lNamePanel.add(lNameInput);
		
		DOBYearPanel.add(DOBYearLabel);
		DOBYearPanel.add(DOBYearInput);
		
		DOBMonthPanel.add(DOBMonthLabel);
		DOBMonthPanel.add(DOBMonthInput);
		
		DOBDayPanel.add(DOBDayLabel);
		DOBDayPanel.add(DOBDayInput);
		
		SSNPanel.add(SSNLabel);
		SSNPanel.add(SSNInput);
		
		dataInput.add(fNamePanel);
		dataInput.add(MIPanel);
		dataInput.add(lNamePanel);
		dataInput.add(DOBYearPanel);
		dataInput.add(DOBMonthPanel);
		dataInput.add(DOBDayPanel);
		dataInput.add(SSNPanel);
		
		create = new JButton("Create Patient");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    		
		    		JDialog.setDefaultLookAndFeelDecorated(true);
		    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to create patient?", "Confirm",
		    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		    	    if (response == JOptionPane.CLOSED_OPTION ||
		    	    		response == JOptionPane.NO_OPTION) {
		    	      append("Create patient cancelled");
		    	      return;
		    	    } else if (response == JOptionPane.YES_OPTION) {
				    	String fName = null, MI = null, lName = null, DOB = null, SSN = null;
				    	
				    	append("Created patient");
		
						fName = fNameInput.getText().trim();
						lName = lNameInput.getText().trim();
						MI = MIInput.getText().trim();
						DOB = DOBYearInput.getText().trim() + "-" +
								DOBMonthInput.getText().trim() + "-" +
								DOBDayInput.getText().trim();
						SSN = SSNInput.getText().trim();
						
						client.sendMessage(new Message(Message.CREATE_PAT, fName + ":" + MI + ":" +
								lName + ":" + DOB + ":" + SSN));
						
						contentPaneRight.removeAll();
						contentPaneRight.revalidate();
						contentPaneRight.repaint();
		    	    }
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void createVisit()  {
		dataInput = new JPanel(new GridLayout(6,2));
		
		Doctor[] docArray = client.activeDocList().toArray(new Doctor[client.activeDocList().size()]);
		Patient[] patArray = client.activePatList().toArray(new Patient[client.activePatList().size()]);
		
		JComboBox<Doctor> docSelector = new JComboBox<Doctor>(docArray);
		JComboBox<Patient> patSelector = new JComboBox<Patient>(patArray);
		
		JLabel vDateYearLabel = new JLabel("Visit Year: ");
		JLabel vDateMonthLabel = new JLabel("Visit Month: ");
		JLabel vDateDayLabel = new JLabel("Visit Day: ");
		JLabel docLabel = new JLabel("Select Doctor: ");
		JLabel patLabel = new JLabel("Select Patient: ");
		
		JTextField vDateYearInput = new JTextField("1990");
		JTextField vDateMonthInput = new JTextField("01");
		JTextField vDateDayInput = new JTextField("13");
		
		JPanel vDateYearPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel vDateMonthPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel vDateDayPanel = new JPanel(new GridLayout(1,2, 1, 3));
		JPanel docPanel = new JPanel(new GridLayout(1,2,1,3));
		JPanel patPanel = new JPanel(new GridLayout(1,2,1,3));
		
		vDateYearPanel.add(vDateYearLabel);
		vDateYearPanel.add(vDateYearInput);
		
		vDateMonthPanel.add(vDateMonthLabel);
		vDateMonthPanel.add(vDateMonthInput);
		
		vDateDayPanel.add(vDateDayLabel);
		vDateDayPanel.add(vDateDayInput);
		
		docPanel.add(docLabel);
		docPanel.add(docSelector);
		
		patPanel.add(patLabel);
		patPanel.add(patSelector);
		
		dataInput.add(docPanel);
		dataInput.add(patPanel);
		dataInput.add(vDateYearPanel);
		dataInput.add(vDateMonthPanel);
		dataInput.add(vDateDayPanel);
		
		create = new JButton("Create Visit");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    	String docID, patID, vDate; 
		    	Doctor tempDoc;
		    	Patient tempPat;
		    	
		    	append("Created visit");

			// type cast to doctor then get the string representation of the ID
				tempDoc = (Doctor)(docSelector.getSelectedItem());
				docID = Integer.toString(tempDoc.getDoctorID());
				
				tempPat = (Patient)(patSelector.getSelectedItem());
				patID = Integer.toString(tempPat.getPatientID());
				
				vDate = vDateYearInput.getText().trim() + "-" +
						vDateMonthInput.getText().trim() + "-" +
						vDateDayInput.getText().trim();
				
						client.sendMessage(new Message(Message.CREATE_VISIT, docID + ":" + patID + ":" +
						vDate));
				
				contentPaneRight.removeAll();
				contentPaneRight.revalidate();
				contentPaneRight.repaint();
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}

	public void reCreateDoctor() {
		dataInput = new JPanel(new GridLayout(2,2));
		
		Doctor[] docArray = client.inactiveDocList().toArray(new Doctor[client.inactiveDocList().size()]);
		
		JComboBox<Doctor> docSelector = new JComboBox<Doctor>(docArray);
		
		JLabel docLabel = new JLabel("Select Doctor: ");
		JPanel docPanel = new JPanel(new GridLayout(1,2,1,3));
		
		docPanel.add(docLabel);
		docPanel.add(docSelector);
		
		dataInput.add(docPanel);
		
		create = new JButton("Re-create doctor");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    	String docID; 
		    	Doctor tempDoc;
		    	
		    	append("Re-created doctor");

			// type cast to doctor then get the string representation of the ID
				tempDoc = (Doctor)(docSelector.getSelectedItem());
				docID = Integer.toString(tempDoc.getDoctorID());
			
				client.sendMessage(new Message(Message.ADD_REMOVED_DOC, docID));
				
				contentPaneRight.removeAll();
				contentPaneRight.revalidate();
				contentPaneRight.repaint();
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void reCreatePatient() {
		dataInput = new JPanel(new GridLayout(2,2));
		
		Patient[] patArray = client.inactivePatList().toArray(new Patient[client.inactivePatList().size()]);
		
		JComboBox<Patient> patSelector = new JComboBox<Patient>(patArray);
		
		JLabel patLabel = new JLabel("Select Patient: ");
		JPanel patPanel = new JPanel(new GridLayout(1,2,1,3));
		
		patPanel.add(patLabel);
		patPanel.add(patSelector);
		
		dataInput.add(patPanel);
		
		create = new JButton("Re-create patient");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    	String patID; 
		    	Patient tempPat;
		    	
		    	append("Re-created patient");

			// type cast to doctor then get the string representation of the ID
				tempPat = (Patient)(patSelector.getSelectedItem());
				patID = Integer.toString(tempPat.getPatientID());
			
				client.sendMessage(new Message(Message.ADD_REMOVED_PAT, patID));
				
				contentPaneRight.removeAll();
				contentPaneRight.revalidate();
				contentPaneRight.repaint();
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	/* edits:
	 *  - select object like list
	 *  - remove selected object
	 *  - create new object
	 */
	public void editDoctor() {
		// have to use JComboBox to select doctor from active doctors
		// then use action listener to make table of all visits
		dataInput = new JPanel(new GridLayout(2,1)); // two rows one column

		Doctor[] docArray = client.activeDocList().toArray(new Doctor[client.activeDocList().size()]);

		JComboBox<Doctor> docSelector = new JComboBox<Doctor>(docArray);
		JLabel docLabel = new JLabel("Select Doctor: ");
		JPanel docPanel = new JPanel(new GridLayout(1,2,1,3));

		docPanel.add(docLabel);
		docPanel.add(docSelector);

		dataInput.add(docPanel);

		create = new JButton("Select Doctor");
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = e.getSource();
				// only one option so no need for the actual ActionEvent
				if (o == create) {
					dataInput = new JPanel(new BorderLayout());
					Doctor d = (Doctor)docSelector.getSelectedItem();
					
					dataInput = new JPanel(new GridLayout(9, 2));
					
					JLabel fNameLabel = new JLabel("First Name: ");
					JLabel lNameLabel = new JLabel("Last Name: ");
					JLabel MILabel = new JLabel("Middle Initial: ");
					JLabel DOBYearLabel = new JLabel("DOB Year: ");
					JLabel DOBMonthLabel = new JLabel("DOB Month: ");
					JLabel DOBDayLabel = new JLabel("DOB Day: ");
					JLabel SSNLabel = new JLabel("SSN: ");
					
					JTextField fNameInput = new JTextField(d.getName().getFirstName());
					JTextField lNameInput = new JTextField(d.getName().getLastName());
					JTextField MIInput = new JTextField(d.getName().getMiddleInitial());
					
					Calendar dob = Calendar.getInstance();
					dob.setTime(d.getData().getDOB());

		    		JTextField DOBYearInput = new JTextField(Integer.toString(dob.get(Calendar.YEAR)));
					JTextField DOBMonthInput = new JTextField(Integer.toString( 1 + dob.get(Calendar.MONTH))); // 0 is January so have to add one
					JTextField DOBDayInput = new JTextField(Integer.toString(dob.get(Calendar.DAY_OF_MONTH)));
					JTextField SSNInput = new JTextField(d.getData().getSSN());
					
					JComboBox<Specialties> specs = new JComboBox<Specialties>(Specialties.values());
					specs.setSelectedItem(d.getSpeciality());
					// don't forget to set the spec for the doctor's spec
					
					JPanel fNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel lNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel MIPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel DOBYearPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel DOBMonthPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel DOBDayPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel SSNPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel specPanel = new JPanel(new GridLayout(1,1,1,3));
					
					fNamePanel.add(fNameLabel);
					fNamePanel.add(fNameInput);
					
					MIPanel.add(MILabel);
					MIPanel.add(MIInput);
					
					lNamePanel.add(lNameLabel);
					lNamePanel.add(lNameInput);
					
					DOBYearPanel.add(DOBYearLabel);
					DOBYearPanel.add(DOBYearInput);
					
					DOBMonthPanel.add(DOBMonthLabel);
					DOBMonthPanel.add(DOBMonthInput);
					
					DOBDayPanel.add(DOBDayLabel);
					DOBDayPanel.add(DOBDayInput);
					
					SSNPanel.add(SSNLabel);
					SSNPanel.add(SSNInput);
					
					specPanel.add(specs);
					
					dataInput.add(fNamePanel);
					dataInput.add(MIPanel);
					dataInput.add(lNamePanel);
					dataInput.add(DOBYearPanel);
					dataInput.add(DOBMonthPanel);
					dataInput.add(DOBDayPanel);
					dataInput.add(SSNPanel);
					dataInput.add(specPanel);
					
					create = new JButton("Edit Doctor");
					create.addActionListener(new ActionListener() {
					    @Override
					    public void actionPerformed(ActionEvent e) {
					    	Object o = e.getSource();
					    	// only one option so no need for the actual ActionEvent
					    	if (o == create) {
					    		
				    	    JDialog.setDefaultLookAndFeelDecorated(true);
				    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to edit doctor?", "Confirm",
				    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				    	    if (response == JOptionPane.CLOSED_OPTION ||
				    	    		response == JOptionPane.NO_OPTION) {
				    	      append("Edit doctor cancelled");
				    	      return;
				    	    } else if (response == JOptionPane.YES_OPTION) {
						    		
						    	String fName = null, MI = null, lName = null, DOB = null, SSN = null, spec = null, docID = null;
						    	
						    	display("Edit doctor");
				
								fName = fNameInput.getText().trim();
								lName = lNameInput.getText().trim();
								MI = MIInput.getText().trim();
								DOB = DOBYearInput.getText().trim() + "-" +
										DOBMonthInput.getText().trim() + "-" +
										DOBDayInput.getText().trim();
								SSN = SSNInput.getText().trim();
								spec = specs.getSelectedItem().toString();
								docID = String.valueOf(d.getDoctorID()); 
								// so when pass to server removes that doc from list and adds another
								
								
								client.sendMessage(new Message(Message.EDIT_DOC, fName + ":" + MI + ":" +
										lName + ":" + DOB + ":" + SSN + ":" + spec + ":" + docID));
								
								contentPaneRight.removeAll();
								contentPaneRight.revalidate();
								contentPaneRight.repaint();
				    	    }
					    	}
					    }
					});
					dataInput.add(create);
					
					contentPaneRight.removeAll();
					contentPaneRight.add(dataInput);
					contentPaneRight.revalidate();
					contentPaneRight.repaint();

					return;
				}
			}
		});
		dataInput.add(create);

		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void editPatient() {
		dataInput = new JPanel(new GridLayout(2,1)); // two rows one column

		Patient[] patArray = client.activePatList().toArray(new Patient[client.activePatList().size()]);

		JComboBox<Patient> patSelector = new JComboBox<Patient>(patArray);
		JLabel patLabel = new JLabel("Select Patient: ");
		JPanel patPanel = new JPanel(new GridLayout(1,2,1,3));

		patPanel.add(patLabel);
		patPanel.add(patSelector);

		dataInput.add(patPanel);

		create = new JButton("Select Patient");
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object o = e.getSource();
				// only one option so no need for the actual ActionEvent
				if (o == create) {
					dataInput = new JPanel(new BorderLayout());
					Patient p = (Patient)patSelector.getSelectedItem();

					dataInput = new JPanel(new GridLayout(8, 2));

					JLabel fNameLabel = new JLabel("First Name: ");
					JLabel lNameLabel = new JLabel("Last Name: ");
					JLabel MILabel = new JLabel("Middle Initial: ");
					JLabel DOBYearLabel = new JLabel("DOB Year: ");
					JLabel DOBMonthLabel = new JLabel("DOB Month: ");
					JLabel DOBDayLabel = new JLabel("DOB Day: ");
					JLabel SSNLabel = new JLabel("SSN: ");

					JTextField fNameInput = new JTextField(p.getName().getFirstName());
					JTextField lNameInput = new JTextField(p.getName().getLastName());
					JTextField MIInput = new JTextField(p.getName().getMiddleInitial());

					Calendar dob = Calendar.getInstance();
					dob.setTime(p.getData().getDOB());

		    		JTextField DOBYearInput = new JTextField(Integer.toString(dob.get(Calendar.YEAR)));
					JTextField DOBMonthInput = new JTextField(Integer.toString( 1 + dob.get(Calendar.MONTH))); // 0 is January so have to add one
					JTextField DOBDayInput = new JTextField(Integer.toString(dob.get(Calendar.DAY_OF_MONTH)));
					JTextField SSNInput = new JTextField(p.getData().getSSN());

					JPanel fNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel lNamePanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel MIPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel DOBYearPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel DOBMonthPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel DOBDayPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel SSNPanel = new JPanel(new GridLayout(1,2, 1, 3));
					JPanel specPanel = new JPanel(new GridLayout(1,1,1,3));

					fNamePanel.add(fNameLabel);
					fNamePanel.add(fNameInput);

					MIPanel.add(MILabel);
					MIPanel.add(MIInput);

					lNamePanel.add(lNameLabel);
					lNamePanel.add(lNameInput);

					DOBYearPanel.add(DOBYearLabel);
					DOBYearPanel.add(DOBYearInput);

					DOBMonthPanel.add(DOBMonthLabel);
					DOBMonthPanel.add(DOBMonthInput);

					DOBDayPanel.add(DOBDayLabel);
					DOBDayPanel.add(DOBDayInput);

					SSNPanel.add(SSNLabel);
					SSNPanel.add(SSNInput);

					dataInput.add(fNamePanel);
					dataInput.add(MIPanel);
					dataInput.add(lNamePanel);
					dataInput.add(DOBYearPanel);
					dataInput.add(DOBMonthPanel);
					dataInput.add(DOBDayPanel);
					dataInput.add(SSNPanel);
					dataInput.add(specPanel);

					create = new JButton("Edit Patient");
					create.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Object o = e.getSource();
							// only one option so no need for the actual ActionEvent
							if (o == create) {

								JDialog.setDefaultLookAndFeelDecorated(true);
								int response = JOptionPane.showConfirmDialog(null, "Do you want to edit patient?", "Confirm",
										JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
								if (response == JOptionPane.CLOSED_OPTION ||
										response == JOptionPane.NO_OPTION) {
									append("Edit patient cancelled");
									return;
								} else if (response == JOptionPane.YES_OPTION) {

									String fName = null, MI = null, lName = null, DOB = null, SSN = null, patID = null;

									display("Edit patient");

									fName = fNameInput.getText().trim();
									lName = lNameInput.getText().trim();
									MI = MIInput.getText().trim();
									DOB = DOBYearInput.getText().trim() + "-" +
											DOBMonthInput.getText().trim() + "-" +
											DOBDayInput.getText().trim();
									SSN = SSNInput.getText().trim();
									patID = String.valueOf(p.getPatientID()); 
									// so when pass to server removes that doc from list and adds another

									client.sendMessage(new Message(Message.EDIT_DOC, fName + ":" + MI + ":" +
											lName + ":" + DOB + ":" + SSN + ":" + ":" + patID));

									contentPaneRight.removeAll();
									contentPaneRight.revalidate();
									contentPaneRight.repaint();
								}
							}
						}
					});
					dataInput.add(create);

					contentPaneRight.removeAll();
					contentPaneRight.add(dataInput);
					contentPaneRight.revalidate();
					contentPaneRight.repaint();

					return;
				}
			}
		});
		dataInput.add(create);

		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void editVisit() {
		dataInput = new JPanel(new GridLayout(2,1));

		Visit<Integer, Integer>[] visitArray = (Visit<Integer, Integer>[]) client.visitData();
		
		JComboBox<Visit<Integer, Integer>> visitSelector = new JComboBox<Visit<Integer, Integer>>(visitArray);
		
		JLabel visitLabel = new JLabel("Select visit: ");
		JPanel visitPanel = new JPanel(new GridLayout(2,1,1,3));
		
		visitPanel.add(visitLabel);
		visitPanel.add(visitSelector);
		
		dataInput.add(visitPanel);
		
		create = new JButton("Edit visit");
		create.addActionListener(new ActionListener() {
		    @SuppressWarnings({ "unchecked"})
			@Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    		Visit<Integer, Integer> v = (Visit<Integer, Integer>)visitSelector.getSelectedItem();
		    		dataInput = new JPanel(new GridLayout(3, 2));
		    		
		    		JLabel docLabel = new JLabel("Doctor: ");
		    		JLabel patLabel = new JLabel("Patient: ");
		    		JLabel DOBYearLabel = new JLabel("Visit Year: ");
		    		JLabel DOBMonthLabel = new JLabel("Visit Month: ");
		    		JLabel DOBDayLabel = new JLabel("Visit Day: ");
		    		
		    		JLabel docInfo = null, patInfo = null;
					try {
						docInfo = new JLabel(Utilities.getDoctor( v.getHost(), client.getData().getDocList() ).toString() );
						patInfo = new JLabel(Utilities.getPatient( v.getVisitor(), client.getData().getPatList() ).toString() );
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						e1.printStackTrace();
					}
		    		
					Calendar vDate = Calendar.getInstance();
					vDate.setTime(v.getDate());

		    		JTextField DOBYearInput = new JTextField(Integer.toString(vDate.get(Calendar.YEAR)));
					JTextField DOBMonthInput = new JTextField(Integer.toString( 1 + vDate.get(Calendar.MONTH))); // 0 is January so have to add one
					JTextField DOBDayInput = new JTextField(Integer.toString(vDate.get(Calendar.DAY_OF_MONTH)));
					
		    		JPanel docPanel = new JPanel(new GridLayout(1,2, 1, 3));
		    		JPanel patPanel = new JPanel(new GridLayout(1,2, 1, 3));
		    		JPanel DOBYearPanel = new JPanel(new GridLayout(1,2, 1, 3));
		    		JPanel DOBMonthPanel = new JPanel(new GridLayout(1,2, 1, 3));
		    		JPanel DOBDayPanel = new JPanel(new GridLayout(1,2, 1, 3));
		    		
		    		docPanel.add(docLabel);
		    		docPanel.add(docInfo);
		    		
		    		patPanel.add(patLabel);
		    		patPanel.add(patInfo);
		    		
		    		DOBYearPanel.add(DOBYearLabel);
		    		DOBYearPanel.add(DOBYearInput);
		    		
		    		DOBMonthPanel.add(DOBMonthLabel);
		    		DOBMonthPanel.add(DOBMonthInput);
		    		
		    		DOBDayPanel.add(DOBDayLabel);
		    		DOBDayPanel.add(DOBDayInput);
		    		
		    		dataInput.add(docPanel);
		    		dataInput.add(patPanel);
		    		dataInput.add(DOBYearPanel);
		    		dataInput.add(DOBMonthPanel);
		    		dataInput.add(DOBDayPanel);
		    		
		    		create = new JButton("Edit Visit");
					create.addActionListener(new ActionListener() {
					    @Override
					    public void actionPerformed(ActionEvent e) {
					    	Object o = e.getSource();
					    	// only one option so no need for the actual ActionEvent
					    	if (o == create) {
					    		JDialog.setDefaultLookAndFeelDecorated(true);
					    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to edit visit?", "Confirm",
					    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					    	    if (response == JOptionPane.CLOSED_OPTION ||
					    	    		response == JOptionPane.NO_OPTION) {
					    	      append("Edit visit cancelled");
					    	      return;
					    	    } else if (response == JOptionPane.YES_OPTION) {
							    	String DOB; 
							    	
							    	append("Edit visit");
									
									DOB = DOBYearInput.getText().trim() + "-" +
											DOBMonthInput.getText().trim() + "-" +
											DOBDayInput.getText().trim();
								
									client.sendMessage(new Message(Message.EDIT_VISIT, Integer.toString(v.getVisitID()) + ":" + 
											DOB + ":" +
											Integer.toString(v.getHost()) + ":" +
											Integer.toString(v.getVisitor())
											));
									
									contentPaneRight.removeAll();
									contentPaneRight.revalidate();
									contentPaneRight.repaint();
					    	    }
					    	}
					    	}
					});
					dataInput.add(create);
					
					contentPaneRight.removeAll();
					contentPaneRight.add(dataInput);
					contentPaneRight.revalidate();
					contentPaneRight.repaint();

					return;
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void removeDoctor() {
		dataInput = new JPanel(new GridLayout(2,2));
		
		Doctor[] docArray = client.activeDocList().toArray(new Doctor[client.activeDocList().size()]);
		
		JComboBox<Doctor> docSelector = new JComboBox<Doctor>(docArray);
		
		JLabel docLabel = new JLabel("Select Doctor: ");
		JPanel docPanel = new JPanel(new GridLayout(1,2,1,3));
		
		docPanel.add(docLabel);
		docPanel.add(docSelector);
		
		dataInput.add(docPanel);
		
		create = new JButton("Remove doctor");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    		JDialog.setDefaultLookAndFeelDecorated(true);
		    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to remove doctor?", "Confirm",
		    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		    	    if (response == JOptionPane.CLOSED_OPTION ||
		    	    		response == JOptionPane.NO_OPTION) {
		    	      append("Remove doctor cancelled");
		    	      return;
		    	    } else if (response == JOptionPane.YES_OPTION) {
				    	String docID; 
				    	Doctor tempDoc;
				    	
				    	append("Remove doctor");
		
					// type cast to doctor then get the string representation of the ID
						tempDoc = (Doctor)(docSelector.getSelectedItem());
						docID = Integer.toString(tempDoc.getDoctorID());
					
						client.sendMessage(new Message(Message.REMOVE_DOC, docID));
						
						contentPaneRight.removeAll();
						contentPaneRight.revalidate();
						contentPaneRight.repaint();
		    	    }
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void removePatient() {
		dataInput = new JPanel(new GridLayout(2,2));
		
		Patient[] patArray = client.activePatList().toArray(new Patient[client.activePatList().size()]);
		
		JComboBox<Patient> patSelector = new JComboBox<Patient>(patArray);
		
		JLabel patLabel = new JLabel("Select Patient: ");
		JPanel patPanel = new JPanel(new GridLayout(1,2,1,3));
		
		patPanel.add(patLabel);
		patPanel.add(patSelector);
		
		dataInput.add(patPanel);
		
		create = new JButton("Remove patient");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    		JDialog.setDefaultLookAndFeelDecorated(true);
		    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to remove patient?", "Confirm",
		    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		    	    if (response == JOptionPane.CLOSED_OPTION ||
		    	    		response == JOptionPane.NO_OPTION) {
		    	      append("Remove patient cancelled");
		    	      return;
		    	    } else if (response == JOptionPane.YES_OPTION) {
				    	String patID; 
				    	Patient tempPat;
				    	
				    	append("Remove patient");
		
					// type cast to doctor then get the string representation of the ID
						tempPat = (Patient)(patSelector.getSelectedItem());
						patID = Integer.toString(tempPat.getPatientID());
					
						client.sendMessage(new Message(Message.REMOVE_PAT, patID));
						
						contentPaneRight.removeAll();
						contentPaneRight.revalidate();
						contentPaneRight.repaint();
		    	    }
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void removeVisit() {
		dataInput = new JPanel(new GridLayout(2,1));

		Visit<Integer, Integer>[] visitArray = (Visit<Integer, Integer>[]) client.visitData();
		
		JComboBox<Visit<Integer, Integer>> visitSelector = new JComboBox<Visit<Integer, Integer>>(visitArray);
		
		JLabel visitLabel = new JLabel("Select visit: ");
		JPanel visitPanel = new JPanel(new GridLayout(2,1,1,3));
		
		visitPanel.add(visitLabel);
		visitPanel.add(visitSelector);
		
		dataInput.add(visitPanel);
		
		create = new JButton("Remove visit");
		create.addActionListener(new ActionListener() {
		    @SuppressWarnings("unchecked")
			@Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    		JDialog.setDefaultLookAndFeelDecorated(true);
		    	    int response = JOptionPane.showConfirmDialog(null, "Do you want to remove visit?", "Confirm",
		    	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		    	    if (response == JOptionPane.CLOSED_OPTION ||
		    	    		response == JOptionPane.NO_OPTION) {
		    	      append("Remove visit cancelled");
		    	      return;
		    	    } else if (response == JOptionPane.YES_OPTION) {
				    	String visitID; 
				    	Visit<Integer, Integer> tempVisit;
				    	
				    	append("Remove visit");
		
					// type cast to doctor then get the string representation of the ID
						tempVisit = (Visit<Integer, Integer>)(visitSelector.getSelectedItem());
						visitID = Integer.toString(tempVisit.getVisitID());
					
						client.sendMessage(new Message(Message.REMOVE_VISIT, visitID));
						
						contentPaneRight.removeAll();
						contentPaneRight.revalidate();
						contentPaneRight.repaint();
		    	    }
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void listVisits(boolean allVisits) {
		append("Displaying all visits...");
		
		String[] columns = {"Doctor", "Patient", "Visit"};
		Object[][] reducedData = client.reducedData(allVisits); // true for all time
		
		JTable table = new JTable(reducedData, columns);
		table.setEnabled(false);
		table.setFillsViewportHeight(true);
		table.setPreferredScrollableViewportSize(new Dimension(325, 425));
		table.setShowGrid(true);
		table.setShowHorizontalLines(true);
		
		dataInput = new JPanel(new BorderLayout());
		
		dataInput.add(new JScrollPane(table));
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();
		
		return;
	}
	
	public void doctorVisits(boolean allVisits) {
		// have to use JComboBox to select doctor from active doctors
		// then use action listener to make table of all visits
		dataInput = new JPanel(new GridLayout(2,1)); // two rows one column
		
		Doctor[] docArray = client.activeDocList().toArray(new Doctor[client.activeDocList().size()]);
		
		JComboBox<Doctor> docSelector = new JComboBox<Doctor>(docArray);
		JLabel docLabel = new JLabel("Select Doctor: ");
		JPanel docPanel = new JPanel(new GridLayout(1,2,1,3));
		
		docPanel.add(docLabel);
		docPanel.add(docSelector);
		
		dataInput.add(docPanel);
		
		create = new JButton("List visits");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    	append("Listed visits");
				
		    	String[] columns = {"Doctor", "Patient", "Visit"};
				Object[][] reducedData = client.doctorData((Doctor)(docSelector.getSelectedItem()), allVisits); // true for all time
				
		    	JTable table = new JTable(reducedData, columns);
				table.setEnabled(false);
				table.setFillsViewportHeight(true);
				table.setPreferredScrollableViewportSize(new Dimension(325, 425));
				table.setShowGrid(true);
				table.setShowHorizontalLines(true);
				
				dataInput = new JPanel(new BorderLayout());
				
				dataInput.add(new JScrollPane(table));
				
				contentPaneRight.removeAll();
				contentPaneRight.add(dataInput);
				contentPaneRight.revalidate();
				contentPaneRight.repaint();
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	public void patientVisits(boolean allVisits) {
		dataInput = new JPanel(new GridLayout(2,1)); // two rows one column
		
		Patient[] patArray = client.activePatList().toArray(new Patient[client.activePatList().size()]);
		
		JComboBox<Patient> patSelector = new JComboBox<Patient>(patArray);
		JLabel patLabel = new JLabel("Select Patient: ");
		JPanel patPanel = new JPanel(new GridLayout(1,2,1,3));
		
		patPanel.add(patLabel);
		patPanel.add(patSelector);
		
		dataInput.add(patPanel);
		
		create = new JButton("List visits");
		create.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	Object o = e.getSource();
		    	// only one option so no need for the actual ActionEvent
		    	if (o == create) {
		    	append("Listed visits");
				
		    	String[] columns = {"Doctor", "Patient", "Visit"};
				Object[][] reducedData = client.patientData((Patient)(patSelector.getSelectedItem()), allVisits); // true for all time
				
		    	JTable table = new JTable(reducedData, columns);
				table.setEnabled(false);
				table.setFillsViewportHeight(true);
				table.setPreferredScrollableViewportSize(new Dimension(325, 425));
				table.setShowGrid(true);
				table.setShowHorizontalLines(true);
				
				dataInput = new JPanel(new BorderLayout());
				
				dataInput.add(new JScrollPane(table));
				
				contentPaneRight.removeAll();
				contentPaneRight.add(dataInput);
				contentPaneRight.revalidate();
				contentPaneRight.repaint();
		    	}
		    }
		});
		dataInput.add(create);
		
		contentPaneRight.removeAll();
		contentPaneRight.add(dataInput);
		contentPaneRight.revalidate();
		contentPaneRight.repaint();

		return;
	}
	
	// called by the GUI if the connection failed
	// reset everything
	void connectionFailed() {
		// remove all stuff from right before anything else
		contentPaneRight.removeAll();
		contentPaneRight.revalidate();
		contentPaneRight.repaint();
		
		append("Logged out...");
		
		send.setEnabled(true);
		logout.setEnabled(false);
		createMenu.setEnabled(false);
		editMenu.setEnabled(false);
		removeMenu.setEnabled(false);
		listMenu.setEnabled(false);
		
		// reset dialogue/messages
		label.setText("Enter your username:");
		tf.setText("Username");
		
		// reset port number and host name as a construction time
		tfServer = new JTextField(defaultHost);
		tfPort = new JTextField("" + defaultPort);
		
		// let the user change them
		tfServer.setEditable(true);
		tfPort.setEditable(true);
		tf.setEditable(true);
		tf.requestFocus();
		send.setEnabled(true);
		
		// don't react to a <CR> after the username
		tf.removeActionListener(this);
		
		// so when send is clicked it's like logging in again
		connected = false;
	}
	
	// called by the Client to append text in the TextArea 
	void append(String str) {
		display(str);
		output.setCaretPosition(output.getText().length() - 1);
	}
	
	private void display(String msg) {
		String time = sdf.format(new Date()) + ": " + msg;
		output.append(time + "\n");
		//output.setCaretPosition(chat.getText().length() - 1);
	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI("localhost", 42000);
	}
}
