//package com.mycompany.projectvaadin;
//
//import com.mycompany.projectvaadin.beans.Employee;
//import com.vaadin.data.Binder;
//import com.vaadin.event.ShortcutAction;
//import com.vaadin.ui.Button;
//import com.vaadin.ui.FormLayout;
//import com.vaadin.ui.HorizontalLayout;
//import com.vaadin.ui.NativeSelect;
//import com.vaadin.ui.TextField;
//import com.vaadin.ui.themes.ValoTheme;
//
//public class UserForm extends FormLayout{
//    
//    private TextField firstName = new TextField("First name");
//    private TextField lastName = new TextField("Last name");
//    private TextField middleName = new TextField("Middle name");
//    private TextField email = new TextField("Email");
//    private TextField organization = new TextField("Organization");
////    private NativeSelect<UserStatus> status = new NativeSelect<>("Status");
//    private Button save = new Button("Save");
//    private Button delete = new Button("Delete");
//    private Button cancel = new Button("Canсel");
//    
//    private UserService service = UserService.getInstance();
//    private Employee customer;
//    private MyUI myUI;
//    private Binder<Employee> binder = new Binder<>(Employee.class);
//    
//    public UserForm(MyUI myUI){
//        this.myUI = myUI;
//        
//        setSizeUndefined();
//        HorizontalLayout buttons = new HorizontalLayout(save, delete, cancel);
//        addComponents(lastName, firstName, middleName, email, organization, buttons);
//        
////        status.setItems(UserStatus.values());
//        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
//        delete.setStyleName(ValoTheme.BUTTON_DANGER);
//        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
//        delete.setClickShortcut(ShortcutAction.KeyCode.DELETE);
//        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
//        
//        binder.bindInstanceFields(this);
//        
//        save.addClickListener(e -> this.save());
//        delete.addClickListener(e -> this.delete());
//        cancel.addClickListener(e -> this.cansel());
//    }
//    
//    public void setCustomer(Employee customer, boolean toDelete){
//        this.customer = customer;
//        binder.setBean(customer);
//        
//        delete.setVisible(toDelete);
//        cancel.setVisible(!toDelete);
//        
//        setVisible(true);
//        lastName.selectAll();
//    }
//    
//    public void delete(){
//        service.delete(customer);
//        myUI.updateList();
//        setVisible(false);
//    }
//    
//    public void save(){
//        service.save(customer);
//        myUI.updateList();
//        setVisible(false);                
//    }
//    
//    public void cansel(){
//        setVisible(false);                
//    }
//    
//}
