package com.project.notes.controller;

import com.project.notes.NoteRemove;
import com.project.notes.pojo.Note;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class FXMLController { 
    
    private ObservableList<Note> notes = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Note> tableNotes;
    
    @FXML
    private TableColumn<Note, String> timeColumn;
    
    @FXML
    private TableColumn<Note, String> textColumn;
    
    @FXML
    private Button newNoteButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button deleteButton;
    
    // инициализируем данные
    @FXML
    private void initialize(){
        //Подготавливаем список заметок
        initData();
        
        
        //выставляем параметры для колонок
        timeColumn.setCellValueFactory(new PropertyValueFactory<Note, String>("time"));
        textColumn.setCellValueFactory(new PropertyValueFactory<Note, String>("text"));
        
        timeColumn.setSortable(false);
        textColumn.setSortable(false);
        
        tableNotes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        //Заполняем таблицу из списка
        tableNotes.setItems(notes);
        
        //Настраиваем кнопки
        Image refreshImage = new Image(getClass().getResourceAsStream("/img/refresh36.png"));
        refreshButton.setGraphic(new ImageView(refreshImage));
        refreshButton.setTooltip(new Tooltip("Обновить"));
        
        Image deleteImage = new Image(getClass().getResourceAsStream("/img/delete36.png"));
        deleteButton.setGraphic(new ImageView(deleteImage));
        deleteButton.setTooltip(new Tooltip("Удалить запись"));
        
    }
   
    // заполняем список заметок
    private void initData(){
        Statement st = null;
        ResultSet rs = null;
        Connection connect = null;
        String sqlCreate = "create table notes ("
                + "id integer not null generated always AS IDENTITY (start with 1, increment by 1),"
                + "time timestamp,"
                + "text varchar(100),"
                + "primary key(id)"
                + ")";
        try {
            //Подключаемся к базе
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connect = DriverManager.getConnection("jdbc:derby:note_db;create=true;user=root;password=qwer");
            
            DatabaseMetaData dbm = connect.getMetaData();

            st = connect.createStatement();
            
            //Проверяем, что существует схема и таблица БД, иначе создаём
            if (!isExistsSchema(dbm)) {
                st.execute("CREATE SCHEMA ROOT");
            }
            if (!isExistsTable(dbm)) {
                st.execute(sqlCreate);
            }
            
            
            //заполняем список данными из БД
            rs = st.executeQuery("SELECT * FROM NOTES");
          
            while (rs.next()) {                
                notes.add(new Note(rs.getInt(1), rs.getString(2).split("\\.")[0], rs.getString(3)));
            }
            
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            alertStackTrace(ex);
        } finally{
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (st != null){
                try {
                    st.close();
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    // обрабатываем нажание на кнопку "Создать заметку"
    @FXML
    public void onClickCreate(){
        
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/NoteScene.fxml"));
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");
            
            Stage stage = new Stage();
            stage.setTitle("Новая заметка");
           
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();            
        }
    }
    
    // обрабатываем нажание на кнопку "Обновить"
    @FXML
    public void onClickRefresh(){        
        notes.clear();
        initData();
        tableNotes.setItems(notes);        
    }
   
    // обрабатываем двойной клик по заметке
    @FXML
    public void onDoubleClick(MouseEvent event){
        if (event.getClickCount() == 2) {
            Note note = tableNotes.getSelectionModel()
                    .getSelectedItem();
            if (note != null) {
                Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Заметка");
            alert.setHeaderText("Дата заметки: \n" + note.getTime());
            alert.setContentText(note.getText());
            alert.showAndWait();            
            }            
        }
        
    }
    
    // обрабатываем нажание на кнопку "Удалить"
    @FXML
    public void onClickDelete(){
        Statement st = null;
        Connection connect = null;
        
        //Получаем значение из выбранной строки
        Note note = tableNotes.getSelectionModel().getSelectedItem();
        
        if (note != null) {
            Thread tDelete = new Thread(new NoteRemove(note.getID()));
            tDelete.start();
            
            //Удаляем запись из таблицы с заметками
            notes.remove(note);
            tableNotes.setItems(notes);
        }
        
    }
    
    
    // Выводить стек ошибок
    private void alertStackTrace(Exception ex){
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Произошло что-то нехорошее");
        //alert.setContentText("Could not find file blabla.txt!");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        
        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    
    // проверка существования схемы  БД
    private boolean isExistsSchema(DatabaseMetaData dbm){
        try {
            
            ResultSet schemas = dbm.getSchemas("ROOT", null);
            while (schemas.next()) {
                return true;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
    
    
    // проверка существования таблицы БД
    private boolean isExistsTable(DatabaseMetaData dbm){
        try {
            ResultSet tables = dbm.getTables(null, null, "NOTES", new String[]{"TABLE"});
            while (tables.next()) {
                return true;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false; 
    }
}
