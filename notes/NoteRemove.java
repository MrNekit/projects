package com.project.notes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NoteRemove implements Runnable{
    
    private int id;
    
    public NoteRemove(int id){
        this.id = id;
    }
    
   
    @Override
    public void run() {
        Statement st = null;
        Connection connect = null;

               
        try {
            //Подключаемся к БД и сохраняем новую заметку
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connect = DriverManager.getConnection("jdbc:derby:note_db;create=true;user=root;password=qwer");
                    
            st = connect.createStatement();
            st.execute("DELETE FROM NOTES WHERE id = " + this.id);
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(NoteRemove.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if(st != null){
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(NoteRemove.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException ex) {
                    Logger.getLogger(NoteRemove.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
