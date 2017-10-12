package com.project.notes.controller;

import com.project.notes.NoteCreator;
import java.sql.Timestamp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class NoteController  {
    
    private String timeNote;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Label labelTime, labelCount;
    
    @FXML
    private TextArea textNote;
    
    @FXML
    private void initialize(){   
        //Получаем текущее время и записываем в Label
        timeNote = (new Timestamp(System.currentTimeMillis())).toString().split("\\.")[0];
        labelTime.setText(timeNote.split(" ")[0] + " / " + timeNote.split(" ")[1]);
        
        
        //делаем ограничение на длину заметки (100 символов)
        labelCount.setText("0 / 100");
        textNote.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {                
                int maxLength = 100;                
                if (textNote.getText().length() > maxLength){
                    String s = textNote.getText().substring(0, maxLength);
                    textNote.setText(s);
                }
                labelCount.setText(textNote.getText().length() + " / 100");
            }
        });       
    }
    
    //Обработка кнопки "Сохранить"
    public void onClickMethod(){
        //Создаём новый поток, в котором обращаемся к базе
        Thread tNote = new Thread(new NoteCreator(timeNote, textNote.getText()));
        tNote.start();
        
        Stage stage = (Stage)saveButton.getScene().getWindow();
        stage.close();
    }
    
    
}
