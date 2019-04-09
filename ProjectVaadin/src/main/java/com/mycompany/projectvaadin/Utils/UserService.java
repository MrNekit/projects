
package com.mycompany.projectvaadin.Utils;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mycompany.projectvaadin.beans.Employee;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    
    private static UserService instance;
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final ArrayList<Employee> contacts = new ArrayList<>();
//    private long nextId = 0;
    
    public final int LOCAL_PORT = 3309;
    private final int REMOTE_PORT = 3306;
    
    private Connection conn;
    private Session session;
    
    //constructor
    private UserService(){
        
    }
    
    //get instance
    public static UserService getInstance(){
        if (instance == null) {
            instance = new UserService();
            instance.getUsers();
        }
        return instance;
    }
    
    //find all users
    public synchronized List<Employee> findAll(){
        return findAll(null);
    }

    //find all users
    public synchronized List<Employee> findAll(String stringFilter) {
        ArrayList<Employee> arrayList = new ArrayList<>();
        for(Employee contact : contacts){
            try {
                boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                        || contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    arrayList.add(contact.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collections.sort(arrayList, (Employee t, Employee t1) -> (int)(t1.getId() - t.getId()));
        
        return arrayList;
    }
    
    //find all users
    public synchronized List<Employee> findAll(String stringFilter, int start, int maxResults){
        ArrayList<Employee> arrayList = new ArrayList<>();
        for(Employee contact : contacts){
            try {
                boolean passesFilter = (stringFilter == null || stringFilter.isEmpty())
                        || contact.toString().toLowerCase().contains(stringFilter.toLowerCase());
                if (passesFilter) {
                    arrayList.add(contact.clone());
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collections.sort(arrayList, (Employee t, Employee t1) -> (int)(t1.getId() - t.getId()));
        
        int end = start + maxResults;
        if (end > arrayList.size()) {
            end = arrayList.size();
        }
        return arrayList.subList(start, end);
    }
    
    //delete users
    public synchronized void delete(Employee value){
        try {
            connectDB();
            DBUtils.deleteUser(conn, value.getId());
            disconnectDB();
            contacts.remove(value);
            
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //save users
    public synchronized void save(Employee entry){
        try {
            if(entry == null){
                LOGGER.log(Level.SEVERE, "Customer is null.");
                return;
            }
            try {
                entry = (Employee) entry.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            
            //Connection conn = getConnection();
            connectDB();        
            
            if (entry.getId() == 0) {
                DBUtils.insertUser(conn, entry);
            } else{
                DBUtils.updateUser(conn, entry);
                contacts.remove(entry);
            }
            contacts.add(entry); 
            
//            conn.close();
            disconnectDB();            
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    //get users
    private void getUsers(){
        try {
            
            connectDB();
            contacts.addAll(DBUtils.queryUsers(conn));
            disconnectDB();
                    
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }         
    }
    
    //update user's list
    public void updateUserList(){
        try {
            contacts.clear();
            connectDB();
            contacts.addAll(DBUtils.queryUsers(conn));
            disconnectDB();
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //connect to DB with SSH
    private void connectDB(){
        try {
            session = getSession();
            conn = getConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //disconnect
    private void disconnectDB(){
        try {
            this.conn.close();
            this.session.delPortForwardingL(LOCAL_PORT);
            this.session.disconnect();
        } catch (JSchException | SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //get connection
    private Connection getConnection() throws ClassNotFoundException{
        
//        String hostName = "localhost";
//        String dbName = "vaadin";
//        String dbUser = "root";
//        String dbPassword = "Yuhjnm2$";

        String hostName = "localhost";
        String dbName = "questionnaire";
        String dbUser = "adminDB";
        String dbPassword = "Yuhjnm2$";
        
        Class.forName("com.mysql.jdbc.Driver");        

        int assigned_port = 0;       
        
        try {
            assigned_port = session.setPortForwardingL(LOCAL_PORT, hostName, REMOTE_PORT); 
            
        } catch (JSchException e) {
            LOGGER.log(Level.SEVERE, e.getMessage()); 
            System.out.println(e.getMessage());
            //return null;
        }
         
        if (assigned_port == 0) {
            LOGGER.log(Level.SEVERE, "Port forwarding failed !"); 
            return null;
        }
          
        StringBuilder url = new StringBuilder("jdbc:mysql://" + hostName + ":");
        
        url.append(assigned_port).append("/").append(dbName)
                .append("?useUnicode=true&useJDBCCompliantTimezoneShift=true")
                .append("&useLegacyDatetimeCode=false&serverTimezone=UTC");
        
        try {
            Properties properties = new Properties();
            properties.setProperty("user", dbUser);
            properties.setProperty("password", dbPassword);
            properties.setProperty("useUnicode","true");
            properties.setProperty("characterEncoding","UTF-8");

            Connection conn = DriverManager.getConnection(url.toString(), 
                    properties);
            
            return conn;
        
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }
        
    //get session
    private Session getSession(){                  
        String host = "10.47.12.51";
        String user = "adminDB";
        String pass = "Yuhjnm2$";
            
        try {               
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);            
            Properties config = new Properties();
            
            session.setPassword(pass);
        
            config.put("StrictHostKeyChecking", "no");
        
            session.setConfig(config);
        
            session.connect();
            
            return session;
        } catch (JSchException ex) {
        }
        return null;
    } 
}
