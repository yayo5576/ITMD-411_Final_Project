package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		
		//Create first table ymart_tickets and make the columns ticket_id, ticket issuer, ticket description, start date, end date, and status.
		final String createTicketsTable = "CREATE TABLE ymart_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date DATETIME, end_date DATETIME, status VARCHAR(30))";
		//create second table that makes the columns user id, user name, password, and admin status.
		final String createUsersTable = "CREATE TABLE ymart_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			//executes both statements which creates two previously mentioned tables
			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
		
			System.out.println("Created tables in given database...");// Console output to notify the tables have been made

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) { //error trapping
			System.out.println(e.getMessage()); //if error does happen, print to the console the error string
		}
		// add users to ymart_users table
		addUsers(); 
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql; 

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB
			statement = getConnection().createStatement();
			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			
			//if there is already data in the ymart_users table, don't add any more duplicate users to it.
			ResultSet rs = statement.executeQuery("SELECT uid FROM ymart_users WHERE uid = 1");
			if(rs.next()) {
				System.out.println("Given database has already been inserted into...");
			} else { //if there is no data in the table, add users from userlist.csv
				
			for (List<String> rowData : array) {
				sql = "INSERT INTO ymart_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql); 				
				}
			System.out.println("Inserts completed in the given database...");
		
			// close statement object
			statement.close();
			}
			
			
			
		} catch (Exception e) { //error trapping
			System.out.println(e.getMessage()); //if error does happen, print to the console the error string
		}
	}

	public int insertRecords(String ticketName, String ticketDesc) { //two parameters, opens a new ticket and adds its issuer name, description, start date, and status.
		
		int id = 0; // to later hold the resultSet value
		try {
			

			PreparedStatement ps = null;
			//prepared statement to be executed after all 4 values are filled
			ps = (PreparedStatement) getConnection().prepareStatement("Insert INTO ymart_tickets (ticket_issuer, ticket_description, start_date, status) VALUES (?, ?, ?, ?)", ps.RETURN_GENERATED_KEYS);
			ps.setString(1, ticketName);// fills the ticket_issuer value from first parameter
			ps.setString(2, ticketDesc);// fills the ticket_description value from second parameter
			ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis())); // fills start_date value with current time and date
			ps.setString(4, "ONGOING");// fills status parameter with string ONGOING
			ps.executeUpdate();// executes the prepared statement query
			
			
			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = ps.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {//error trapping
			// TODO Auto-generated catch block
			e.printStackTrace();// print the error trace
		}
		return id; //returns the filled id value

	}

	public ResultSet readRecords() {//reads the data records of the ymart_tickets table
		
		ResultSet results = null;
		try {
			statement = connect.createStatement();//
			results = statement.executeQuery("SELECT * FROM ymart_tickets");// executes statement to gather all current data from ymart_tickets table
			
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;// returns the all the data from the table
		
	}
	
	public void updateRecords(String ticketID, String ticketDesc) {// two parameters, updates a desired ticket's description
		try {
			// Execute update query
			System.out.println("Creating update statement...");
			statement = connect.createStatement();
			
			//the response selected by the dialog box will determine if the ticket gets updated or does not change
			int response = JOptionPane.showConfirmDialog(null, "Update ticket # " + ticketID + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if (response == JOptionPane.NO_OPTION) {// if the user selects no, then the ticket wont get updated
				System.out.println("No record updated");
				JOptionPane.showMessageDialog(null, "No ticket updated.");
			} else if (response == JOptionPane.YES_OPTION) {//if the user selects yes, the ticket will get updated with the new description
				statement.executeUpdate("UPDATE ymart_tickets SET ticket_description = '" + ticketDesc + "' WHERE ticket_id = " + ticketID, Statement.RETURN_GENERATED_KEYS);
				JOptionPane.showMessageDialog(null, "Ticket id # " + ticketID + " updated .");
				System.out.println("Record updated");
				
			} else if (response == JOptionPane.CLOSED_OPTION) {// if the user closes out the dialog box, the ticket will not update and get cancelled.
				System.out.println("Request cancelled");
				JOptionPane.showMessageDialog(null, "Ticket update request cancelled.");
			}
			
			
			
		} catch (SQLException e1) {//error trapping
			e1.printStackTrace();//print error trace message if error does happen
		}
	}
	
	// continue coding for deleteRecords implementation
	public void deleteRecords(String ticketID) {// one parameter, deletes a ticket from the ymart_tickets table
		try {
			// Execute delete query
			System.out.println("Creating statement...");
			statement = connect.createStatement();
			
			//will delete from the table the desired ticket
			String sql = "DELETE FROM ymart_tickets WHERE ticket_id = " + ticketID;
			int response = JOptionPane.showConfirmDialog(null, "Delete ticket # " + ticketID + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if (response == JOptionPane.NO_OPTION) {// if the user selects no on the dialog box, the ticket will not be deleted
				System.out.println("No record deleted");
				JOptionPane.showMessageDialog(null, "No ticket deleted.");
			} else if (response == JOptionPane.YES_OPTION) {//if the user selects yes, the ticket will be deleted
				statement.executeUpdate(sql);
				JOptionPane.showMessageDialog(null, "Ticket id # " + ticketID + " deleted.");
				System.out.println("Record deleted");
			} else if (response == JOptionPane.CLOSED_OPTION) {//if the user closes out the box, the ticket will not be deleted and the request is canceled. 
				System.out.println("Request cancelled");
				JOptionPane.showMessageDialog(null, "Ticket deletion request cancelled.");

			}
			
		} catch (SQLException e1) {//error trapping
			e1.printStackTrace();//if the error does happen, the error message trace is printed.
		}
	} 
	
	public void closeRecords(String ticketID) {//one parameter. closes a current ongoing ticket from the ymart_tickets table
		try {
			// Execute update query
			System.out.println("Creating closing update statement...");
			statement = connect.createStatement();
			
			//dateTime is a variable used to hold the current time and date for later use in the sql query statement
			java.sql.Timestamp dateTime = new java.sql.Timestamp(System.currentTimeMillis());
			//will close a ticket depending on the input done on the dialog box
			int response = JOptionPane.showConfirmDialog(null, "Close ticket # " + ticketID + "?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			

			if (response == JOptionPane.NO_OPTION) {// if the user selects no, the ticket will not get closed
				System.out.println("No record updated");
				JOptionPane.showMessageDialog(null, "No ticket closed.");
			} else if (response == JOptionPane.YES_OPTION) {// if the user selects yes, the ticket will get closed
				statement.executeUpdate("UPDATE ymart_tickets SET status = 'CLOSED', end_date = '" + dateTime + "' WHERE ticket_id = " + ticketID, Statement.RETURN_GENERATED_KEYS);
				JOptionPane.showMessageDialog(null, "Ticket id # " + ticketID + " closed .");
				System.out.println("Record updated");
			
			} else if (response == JOptionPane.CLOSED_OPTION) {// if the user closes out the box, the ticket wont get closed and the request is cancelled.
				System.out.println("Request cancelled");
				JOptionPane.showMessageDialog(null, "Ticket cancel request cancelled.");
			}
			
			
			
		} catch (SQLException e1) {//error trapping
			e1.printStackTrace();// if error does happen, the error gets printed to the console
		}
	}
	

	
}




