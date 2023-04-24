package com.servlet.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



@WebServlet("/UserServlet")

//Servlet to perform apis
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//Get api to fetch details from states table
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String api=request.getParameter("search");
		switch (api) {
		case "fetchAll":
			UserDao userDao= new UserDao();
			Object result=userDao.fetchFromStates();
			
			response.getWriter().write(result.toString());
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			break;
			
		case "searchState":
			String searchString=request.getParameter("searchRequest").toString();
			UserDao userDao2=new UserDao();
			Object searchResult=userDao2.fetchState(searchString);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(searchResult.toString());
			break;
			
			
		case "searchAccount":
			String searchAccountNumber=request.getParameter("searchAccountNumber").toString();
			UserDao userDao3=new UserDao();
			Object searchAccountResult=userDao3.fetchAccount(searchAccountNumber);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(searchAccountResult.toString());
			break;
			
		case "searchAccountPDF":
			String searchPdf=request.getParameter("searchAccno").toString();
			UserDao userDao4Dao=new UserDao();
			String searchAccoutnPdf=userDao4Dao.fetchPDF(searchPdf,response);
			PrintWriter outPrintWriter=response.getWriter();
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			outPrintWriter.print(searchAccoutnPdf);
			break;
			
		
		}
			
		
	}

	//Post api
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
		String jsonResponse="";
        String jsonString = request.getReader().lines().collect(Collectors.joining());
        JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();
        String action=request.getParameter("action");
        Gson gson = new Gson();
        JsonObject json1= new JsonObject();
        JsonObject json2=new JsonObject(); 

        
        
        switch (action) {
        case "insert-state":
	        String inputString=jsonObject.get("state_name").getAsString();
	        
	        json1.addProperty("Status Code", 200);
	        json1.addProperty("Message","State added successfully!");
	        json2.addProperty("Status Code", 404);
	        json2.addProperty("Message", "Please enter only alphabets");
	        
	        if(inputString.matches("^[a-zA-Z\\s]+$")) {
	        	new UserDao().insertIntoStates(jsonObject);
	        	jsonResponse = gson.toJson(json1);        	
	        }else {
	        	jsonResponse=gson.toJson(json2);
	        }
        
	        response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().write(jsonResponse);
	        break;
        
        case "insert-bank-account":
        	
        	String FirstName=jsonObject.get("FirstName").getAsString();
			String LastName=jsonObject.get("LastName").getAsString();
			String MiddleName=jsonObject.get("MiddleName").getAsString();
			String ContactNumber=jsonObject.get("ContactNumber").getAsString();
			String EmailId=jsonObject.get("EmailId").getAsString();
			String Address=jsonObject.get("Address").getAsString();	
			String PinCode=jsonObject.get("PinCode").getAsString();
			
			String accno=new UserDao().insertdetails(jsonObject);
			
			if(accno.equals("Contact Number already exists!") || accno.equals("Email already exists!")||accno.equals("State_id is invalid!")) {
				json1.addProperty("Status Code", 409);
				json1.addProperty("Message",accno);
			}else {
				json1.addProperty("Status Code", 200);
				json1.addProperty("Message", "User added Successfully!");
				json1.addProperty("Account Number", accno);
			}

	        
        	
			if(FirstName==null || FirstName.isEmpty() || !FirstName.matches("[a-zA-Z]+")) {
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "FirstName should contain only alphabets and is mandatory");
				jsonResponse=gson.toJson(json2);
			}else if (LastName==null || LastName.isEmpty() || !LastName.matches("\\d+") ) {
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "LastName should contain only numbers and is mandatory");
				jsonResponse=gson.toJson(json2);
			}else if (!MiddleName.matches("[a-zA-Z0-9]+")) {
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "MiddleName should be alphaNumeric");
				jsonResponse=gson.toJson(json2);
			}else if (ContactNumber==null || ContactNumber.isEmpty() || !ContactNumber.matches("\\d{10}")) {
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "Enter valid ContactNumber of 10 digits");
				jsonResponse=gson.toJson(json2);
			}else if(EmailId==null || EmailId.isEmpty() || !EmailId.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "Enter valid emailId");
				jsonResponse=gson.toJson(json2);
			}else if (Address==null || Address.isEmpty() || !Address.matches("[a-zA-Z0-9]+")) {
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "Enter valid address");
				jsonResponse=gson.toJson(json2);
			}else if (PinCode==null || PinCode.isEmpty() || !PinCode.matches("\\d{6}")) {
				json2.addProperty("Status Code", 404);
		        json2.addProperty("Message", "Enter valid pincode");
				jsonResponse=gson.toJson(json2);
			}else {
				
				jsonResponse=gson.toJson(json1);
			}
				
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(jsonResponse);
			break;
        	
        	
        	
        case "activity":
        	String activityString=jsonObject.get("Activity").getAsString();
        	
        	String activity=new UserDao().insertActivity(jsonObject);        		
        	
        	if(activity.equals("Insufficient balance!") || activity.equals("Invalid account number")) {
        		json1.addProperty("Status Code", 404);
        		json1.addProperty("Message", activity);
        	}else {
        		json1.addProperty("Status Code", 200);
        		json1.addProperty("Message", activity);		
        	}
        		json2.addProperty("Status Code", 409);
        		json2.addProperty("Message", "Invalid Entry in activity");
        		
        	if("1".equals(activityString)) {
        		jsonResponse=gson.toJson(json1);
        	}else if("2".equals(activityString)){
				jsonResponse=gson.toJson(json1);
			}else {
				jsonResponse=gson.toJson(json2);
			}
        	
        	
        	response.setContentType("application/json");
        	response.setCharacterEncoding("UTF-8");
        	response.getWriter().write(jsonResponse);
        	break;
		
	}
	
}}


	
