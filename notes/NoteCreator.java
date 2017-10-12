package com.project.notes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class NoteCreator implements Runnable{
    
    private String time;
    private String text;
    
    public NoteCreator(String time, String text){
        this.time = time;
        this.text = text;
    }
    
   
    @Override
    public void run() {
        Statement st = null;
        Connection connect = null;
        String sqlQuery = new StringBuilder()
                .append("insert into notes (time,text) values ")
                .append("(\'")
                .append(time)
                .append("\', \'")
                .append(text)
                .append("\')")
                .toString();
        
        
        try {
            //Подключаемся к БД и сохраняем новую заметку
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connect = DriverManager.getConnection("jdbc:derby:note_db;create=true;user=root;password=qwer");
                    
            st = connect.createStatement();
            st.execute(sqlQuery);
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(NoteCreator.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            if(st != null){
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(NoteCreator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException ex) {
                    Logger.getLogger(NoteCreator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
