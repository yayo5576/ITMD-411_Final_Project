package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemCloseTicket;
	

	public Tickets(Boolean isAdmin) {

		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);
		//Initialize third sub menu item for Admin main menu
		mnuItemCloseTicket = new JMenuItem("Close a Ticket");  
		// add to Admin Main menu item
		mnuAdmin.add(mnuItemCloseTicket);
		
		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);
		
		
		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemCloseTicket.addActionListener(this); // new sub menu item under admin menu


	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		if (chkIfAdmin == false) { // if the user logging in is not a admin, they will not see the admin main menu
			bar.add(mnuTickets);
		} else {// if the user is a admin, they will see the admin main menu
			bar.add(mnuAdmin);
			bar.add(mnuTickets);
		}
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {//opens a new ticket

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");//enter for ticket_issuer
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");//enter for ticket description
			if (ticketName.isBlank() || ticketDesc.isBlank()) {// if one of the inputs is blank, the ticket wont be created
			System.out.println("Ticket cannot be created!!!");

			} else {// if both inputs are valid, the new issue gets created with help of insertRecorsd from Dao
				// insert ticket information to database
				int id = dao.insertRecords(ticketName, ticketDesc);
				if (id != 0) {
					System.out.println("Ticket ID : " + id + " created successfully!!!");//console output
					JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");//dialog output
			}
				}
			}

		else if (e.getSource() == mnuItemViewTicket) {//views the current tickets in ymart_tickets table

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} 
		else if (e.getSource() == mnuItemDelete) {// deletes a ticket from the ymart_tickets table
			String ticketID = JOptionPane.showInputDialog(null, "Enter the ticket's ID to remove from the table");//enter the id of the ticket to be removed
			 if (ticketID.isBlank() ) {//if the input is blank, then the ticket wont be deleted.
					System.out.println("Ticket cannot be deleted!!!");
					} else {
						 dao.deleteRecords(ticketID);//deletes the ticket
					}
			
			
		}
		else if (e.getSource() == mnuItemUpdate) {// updates a ticket from the ymart_tickets table
			
			String ticketID = JOptionPane.showInputDialog(null, "Enter the ticket's ID to update from the table");// enter the desired ticket's id to update
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter new ticket's description");// enter the new description for the updated ticket


			// insert ticket information to database
			 if (ticketID.isBlank() || ticketDesc.isBlank()) {// if one of the inputs is blank, the ticket wont be updated
					System.out.println("Ticket cannot be updated!!!");
					} else {
						dao.updateRecords(ticketID, ticketDesc);//updates the ticket
					}
		
		} else if(e.getSource() == mnuItemCloseTicket) {//closes a ticket from the ymart_tickets table
			
			String ticketID = JOptionPane.showInputDialog(null, "Enter the ticket's ID to close");//enter the desired ticket's id to close
			 if (ticketID.isBlank()) {// if the input is blank, the ticket wont be closed.
					System.out.println("Ticket cannot be closed!!!");
					} else {
						dao.closeRecords(ticketID);//closes the ticket
					}
		}

	}

}
