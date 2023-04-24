package com.servlet.json;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.pdf.PdfWriter;



//DAO class		

public class UserDao {
	
	Connection connection;
	
	public UserDao() {
		//Creating connection to MySQl database
		try {  
			Class.forName("com.mysql.jdbc.Driver");
		connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/bank","root","1111");
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	
	}
	
	//Method to insert details into states table
	public int insertIntoStates(JsonObject json) {
		
		try {
			PreparedStatement pStatement=connection.prepareStatement("insert into states(state_name) values(?)");
        if (json.has("state_name")) {
            pStatement.setString(1, json.get("state_name").getAsString());
        } else {
            pStatement.setString(1, "");
        }
			int res=pStatement.executeUpdate();
		
			return res;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return 0;
		
	}
	
	
	//Method to retrieve data from states table
	public Object fetchFromStates() {
		List<Map<String, String>> list = new ArrayList<>();
		
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("select * from states");
			while (rs.next()) {
				String stateid=rs.getString("state_id");
				String stateName=rs.getString("state_name");
				Map<String, String> row=new HashMap<>();
				row.put("state_id", stateid);
				row.put("state_name", stateName);
				
				list.add(row);
			}
			String resultString =new Gson().toJson(list);
			
			return resultString;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	
		}
		
		return null;
		
	}
	
	//Method to fetch state details using state name
	public Object fetchState(String searchRequest) {
		List<Map<String, String>> list = new ArrayList<>();

		try {
			
		String querySearch="select * from states where state_name=?";
		
		PreparedStatement preparedStatement=connection.prepareStatement(querySearch);
		preparedStatement.setString(1, searchRequest);
		ResultSet resultSet=preparedStatement.executeQuery();
		
		
		while(resultSet.next()){
			String state_id=resultSet.getString("state_id");
			String state_name=resultSet.getString("state_name");
			
			Map<String, String> map=new HashMap<>();
			map.put("state_id",state_id);
			map.put("state_name", state_name);
			
			list.add(map);
		}
		
			String fetchstate= new Gson().toJson(list);
		
			return fetchstate;
					
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	public Object fetchAccount(String accno) {
		List<Map<String,String>> list=new ArrayList<>();
		try {
			PreparedStatement preparedStatement=connection.prepareStatement("select * from bank_account where AccountNumber=?");
			preparedStatement.setString(1, accno);
			ResultSet resultSet=preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				String FirstName=resultSet.getString("FirstName");
				String LastName=resultSet.getString("LastName");
				String MiddleName=resultSet.getString("MiddleName");
				String ContactNumber=resultSet.getString("ContactNumber");
				String EmailId=resultSet.getString("EmailId");
				String Address=resultSet.getString("Address");
				String PinCode=resultSet.getString("PinCode");
				String state_id=resultSet.getString("state_id");
				String AccountNumber=resultSet.getString("AccountNumber");
				String Balance=resultSet.getString("Balance");
				
				Map<String, String> map=new HashMap<>();
				map.put("FirstName", FirstName);
				map.put("LastName", LastName);
				map.put("MiddleName", MiddleName);
				map.put("ContactNumber", ContactNumber);
				map.put("EmailId", EmailId);
				map.put("Address", Address);
				map.put("PinCode", PinCode);
				map.put("state_id", state_id);
				map.put("AccountNumber", AccountNumber);
				map.put("Balance", Balance);
				
				list.add(map);
			}
			String fetchAccountString=new Gson().toJson(list);
			
			return fetchAccountString;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return null;
		
	}
	
	
	public String fetchPDF(String accnoPdf,HttpServletResponse response) {
		String pdfurl="";
		try {
			PreparedStatement statement=connection.prepareStatement("select Date, Activity, Amount from activity where AccountNumber=?");
			statement.setString(1, accnoPdf);
			ResultSet respdf=statement.executeQuery();
			
			Document document=new Document(PageSize.A4);
			OutputStream outputStream=response.getOutputStream();
			PdfWriter.getInstance(document, outputStream);
			document.open();
			
			PdfPTable table=new PdfPTable(3);
			table.setWidthPercentage(100);
			
			PdfPCell cell1=new PdfPCell(new Paragraph("Date"));
			PdfPCell cell2=new PdfPCell(new Paragraph("Activity"));
			PdfPCell cell3=new PdfPCell(new Paragraph("Amount"));
			table.addCell(cell1);
			table.addCell(cell2);
			table.addCell(cell3);
			
			while(respdf.next()) {
				String Date=respdf.getString("Date");
				String Activity=respdf.getString("Activity");
				Double Amount=respdf.getDouble("Amount");
				
				PdfPCell dateCell=new PdfPCell(new Paragraph(Date));
				PdfPCell activityCell=new PdfPCell(new Paragraph(Activity));
				PdfPCell amountCell=new PdfPCell(new Paragraph(String.valueOf(Amount)));
				
				table.addCell(dateCell);
				table.addCell(activityCell);
				table.addCell(amountCell);
			}
			
			document.add(table);
			document.close();	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pdfurl;
		
	}

	
	
	
	//Method to insert details into bank_account table
	public String insertdetails(JsonObject json) {
				
		try {
			int n=12;
			Random random=new Random();
			
			String randomNumber="";
			for(int i=0;i<n;i++) {
				int digit=random.nextInt(10);
				randomNumber+=digit;
			}
			
			
			PreparedStatement validationStatement=connection.prepareStatement("SELECT COUNT(*) FROM states WHERE state_id = ?");
			validationStatement.setString(1, json.get("state_id").getAsString());
			ResultSet rSet=validationStatement.executeQuery();
			
			if(rSet.next() && rSet.getInt(1)==1 ) {
				
				PreparedStatement pStatement=connection.prepareStatement("insert into bank_account (FirstName,LastName,MiddleName,ContactNumber,EmailId,Address,PinCode,state_id,AccountNumber,Balance) values(?,?,?,?,?,?,?,?,?,?)");
				
				
				if(json.has("FirstName")) {
					pStatement.setString(1, json.get("FirstName").getAsString());
				}else {
					pStatement.setString(1, "");
				}
				
				if(json.has("LastName")) {
					pStatement.setString(2, json.get("LastName").getAsString());
				}else {
					pStatement.setString(2, "");
				}
				
				if(json.has("MiddleName")) {
					pStatement.setString(3, json.get("MiddleName").getAsString());
				}else {
					pStatement.setString(3, "");
				}
				
				if(json.has("ContactNumber")) {
					PreparedStatement contactStatement =connection.prepareStatement("select COUNT(*) from bank_account where ContactNumber=?");
					
					contactStatement.setString(1, json.get("ContactNumber").getAsString());
					ResultSet contactResultSet=contactStatement.executeQuery();
					contactResultSet.next();
					int count=contactResultSet.getInt(1);
					String ce="Contact Number already exists!";
					if(count>0) {
						return ce;
					}else {
						pStatement.setString(4,json.get("ContactNumber").getAsString());						
					}	
					
				}else {
					pStatement.setString(4, "");
				}
				
				if(json.has("EmailId")) {
					PreparedStatement contactStatement =connection.prepareStatement("select COUNT(*) from bank_account where EmailId=?");
					
					contactStatement.setString(1, json.get("EmailId").getAsString());
					ResultSet contactResultSet=contactStatement.executeQuery();
					contactResultSet.next();
					int count=contactResultSet.getInt(1);
					String ce="Email already exists!";
					if(count>0) {
						return ce;
					}else {
						pStatement.setString(5,json.get("EmailId").getAsString());						
					}
				}else {
					pStatement.setString(5, "");
				}
				
				if(json.has("Address")) {
					pStatement.setString(6,json.get("Address").getAsString());
				}else {
					pStatement.setString(6, "");
				}
				
				if(json.has("PinCode")) {
					pStatement.setString(7, json.get("PinCode").getAsString());
				}else {
					pStatement.setString(7, "");
				}
				
				
				if(json.has("state_id")) {
					pStatement.setString(8, json.get("state_id").getAsString());
				}else {
					pStatement.setString(8, "");
				}
				
				
				pStatement.setString(9, randomNumber);
				
				
				pStatement.setInt(10, 0);
				
				
				pStatement.executeUpdate();
				
				return randomNumber;

			}else {
				return "State_id is invalid!";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	//Method to perform Activity- Deposit and withdrawl
	public String insertActivity(JsonObject json) {
		
			
			try {
				
				LocalDate currentDate=LocalDate.now();
				String AccountNumber=json.get("AccountNumber").getAsString();
				String Date=currentDate.toString();
				int Amount=json.get("Amount").getAsInt();
				String Activity=json.get("Activity").getAsString();
				
				PreparedStatement preparedStatement=connection.prepareStatement("select COUNT(*) from bank_account where AccountNumber=?");
				preparedStatement.setString(1, AccountNumber);
				ResultSet rSet=preparedStatement.executeQuery();
				
				if(rSet.next() && rSet.getInt(1)==1) {
					
					PreparedStatement balanceStatement=connection.prepareStatement("select Balance from bank_account where AccountNumber=?");
					balanceStatement.setString(1, AccountNumber);
					ResultSet balanceSet=balanceStatement.executeQuery();
					
					if(balanceSet.next()) {
						int currentBalance=balanceSet.getInt("Balance");
						int newBalance=0;
						if(Activity.equals("1")) {
							newBalance=currentBalance+Amount;
						}else if (Activity.equals("2")) {
							if(currentBalance>=Amount) {
								newBalance=currentBalance-Amount;
							}else {
								return "Insufficient balance!";
							}
						}
						
						PreparedStatement updateStatement=connection.prepareStatement("update bank_account set Balance=? where AccountNumber=?");
						updateStatement.setInt(1, newBalance);
						updateStatement.setString(2, AccountNumber);
						updateStatement.executeUpdate();
						
						
						if("1".equals(Activity) || "2".equals(Activity)) {
							
							PreparedStatement insertStatement=connection.prepareStatement("insert into Activity (AccountNumber,Date,Amount,Activity) values(?,?,?,?)");
							insertStatement.setString(1, AccountNumber);
							insertStatement.setString(2, Date);						
							insertStatement.setInt(3, Amount);
							insertStatement.setString(4, Activity);
							
							insertStatement.executeUpdate();
							
						}
						return Activity+" done sucessfully";
					}
				}else {
					return "Invalid account number";
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
		
	}
		
}