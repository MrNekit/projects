package com.project.notes.pojo;

import java.sql.Timestamp;

public class Note {

    private int id;
    private String time;
    private String text;
    
    //конструктор
    public Note(int id, String time, String text){
        this.id = id;
        this.time = time;
        this.text = text;
    }
        
    //конструктор по-умолчанию
    public Note(){
        
    }

    
    //геттеры
    public String getTime(){
        return time;
    }
    
    public String getText(){
        return text;
    }
    
    public int getID(){
        return id;
    }
    
    //сеттеры 
    public void setTime(String time){
        this.time = time;
    }
    
    public void setText(String text){
        this.text = text;
    }
}
