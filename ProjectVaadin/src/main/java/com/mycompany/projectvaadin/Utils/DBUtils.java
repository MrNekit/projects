
package com.mycompany.projectvaadin.Utils;

import com.mycompany.projectvaadin.beans.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    
    public static Employee findUser(Connection conn, int id) throws SQLException {
 
        String sql = "Select a.id, a.firstName, a.middleName, a.lastName, a.organization from employee a "
                + " where a.id = ? ";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setLong(1, id);
 
        ResultSet rs = pstm.executeQuery();
 
        if (rs.next()) {
            String firstName = rs.getString("firstName");            
            String middleName = rs.getString("middleName");            
            String lastName = rs.getString("lastName");
            String email = rs.getString("email");
            String organization = rs.getString("organization");
            Employee user = new Employee();
            user.setId(id);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setOrganization(organization);
            return user;
        }
        return null;
    }
    
    public static int getUserID(Connection conn, Employee user) throws SQLException{
        String sql = "Select a.id from employee a "
                + " where a.firstName=? and a.middleName=? and a.lastName=? " +
                " and a.email=? and a.organization=?";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
        pstm.setString(1, user.getFirstName());
        pstm.setString(2, user.getMiddleName());
        pstm.setString(3, user.getLastName());
        pstm.setString(4, user.getEmail());
        pstm.setString(5, user.getOrganization());
 
        ResultSet rs = pstm.executeQuery();
 
        if (rs.next()) {
            int id = rs.getInt("id");
            return id;
        }
        return 0;
    }
        
    public static List<Employee> queryUsers(Connection conn) throws SQLException{
        
        String sql = "Select a.id, a.lastName, a.firstName, a.middleName, a.email, a.organization from employee a ";
        
        PreparedStatement pstm = conn.prepareStatement(sql);
        
        ResultSet rs = pstm.executeQuery();
        
        List<Employee> list = new ArrayList<Employee>();
        
        while (rs.next()){
            int id = rs.getInt("id");
            String firstName = rs.getString("firstName");            
            String middleName = rs.getString("middleName");            
            String lastName = rs.getString("lastName");
            String email = rs.getString("email");
            String organization = rs.getString("organization");
            Employee user = new Employee();
            user.setId(id);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setOrganization(organization);
            list.add(user);
        }
        return list;
    }
    
    public static void insertUser(Connection conn, Employee user) throws SQLException {
        String sql = "Insert into employee(firstName, middleName, lastName, email, organization) values (?,?,?,?,?)";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setString(1, user.getFirstName());
        pstm.setString(2, user.getMiddleName());
        pstm.setString(3, user.getLastName());
        pstm.setString(4, user.getEmail());
        pstm.setString(5, user.getOrganization());
 
        pstm.executeUpdate();
    }
    
    public static void updateUser(Connection conn, Employee user) throws SQLException {
        String sql = "Update employee set firstName =?, middleName=?, lastName=?, email=?, organization=? where id=? ";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setString(1, user.getFirstName());
        pstm.setString(2, user.getMiddleName());
        pstm.setString(3, user.getLastName());
        pstm.setString(4, user.getEmail());
        pstm.setString(5, user.getOrganization());
        pstm.setLong(6, user.getId());
        pstm.executeUpdate();
    }
    
    
    public static void deleteUser(Connection conn, long id) throws SQLException {
        String sql = "Delete From employee where id= ?";
 
        PreparedStatement pstm = conn.prepareStatement(sql);
 
        pstm.setLong(1, id);
 
        pstm.executeUpdate();
    }
 
}
