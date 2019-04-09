
package com.mycompany.projectvaadin;

import com.mycompany.projectvaadin.Utils.FillChecker;
import com.mycompany.projectvaadin.Utils.UserService;
import com.mycompany.projectvaadin.beans.Employee;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserUI extends UI{    
    
    private UserService service = UserService.getInstance();
    private Grid<Employee> grid = new Grid<>(Employee.class);
    private TextField filterText = new TextField();
    private HorizontalLayout main;
    private HorizontalLayout toolbar;
    
    GridContextMenu<Employee> gridContextMenu = new GridContextMenu<>(grid);

    
    @Override
    protected void init(VaadinRequest request) {
        
        this.toolbar = new HorizontalLayout(createFilteringLayout());                    

        this.main = createMainLayout();         
               
        
        this.gridContextMenu.addGridHeaderContextMenuListener(e -> {
            gridContextMenu.removeItems();
        });        
        
        
        gridContextMenu.addGridBodyContextMenuListener(this::updateGridBodyMenu);      
        
        
    }
    
    
    private void updateGridBodyMenu(GridContextMenuOpenEvent<Employee> event){

        event.getContextMenu().removeItems();
        
        if (event.getItem() != null) {
            event.getContextMenu().addItem("Add", VaadinIcons.PLUS, selectedItem->{
                initCreateWindow();            
            });
            
            event.getContextMenu().addItem("Remove", VaadinIcons.CLOSE, selectedItem ->{
               initDeleteWindow((Employee) event.getItem());
            });
            
            event.getContextMenu().addItem("Edit", VaadinIcons.EDIT, selectedItem ->{
                initEditWindow((Employee) event.getItem());
            });
        } else{
            event.getContextMenu().addItem("Add", VaadinIcons.PLUS, selectedItem->{
                initCreateWindow();            
            });
        }
        
        
    }

    
    private CssLayout createFilteringLayout(){
        
        filterText.setPlaceholder("filter by name...");
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
                
        Button clearFilter = new Button(VaadinIcons.CLOSE);
        clearFilter.setDescription("Clear the current filter");
        clearFilter.addClickListener(e -> filterText.clear());
            
        CssLayout filtering = new CssLayout();
        filtering.addComponents(filterText, clearFilter);
        filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        
        return filtering;
    }
    
//    
//    private CssLayout createPanelLayout(){
//        
//        CssLayout panel = new CssLayout();
//        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
//        panel.setSizeFull();
//        panel.addComponent(createPanelCaption());
//        return panel;        
//    }
    
       
//    private HorizontalLayout createPanelCaption(){
//        
//        HorizontalLayout caption = new HorizontalLayout();
//        try {
//            caption.addStyleName("v-panel-caption");
//            caption.setSpacing(true);
//            
//            String basePath = VaadinService.getCurrent().getBaseDirectory().getCanonicalPath();
//            FileResource resource = new FileResource(new File(basePath +
//                    "/WEB-INF/img/inform_min.png"));
//            Image image = new Image(null, resource);
//            image.setWidthUndefined();
//            Label title = new Label("Консоль администратора");
//            title.setStyleName("v-label-stylename");
//            caption.addComponents(image, title);
//            
//            caption.setSizeFull();
//            caption.setExpandRatio(title, 1);
//            caption.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
//        } catch (IOException ex) {
//            Logger.getLogger(MyUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return caption;
//    }
    
    
    private HorizontalLayout createMainLayout(){
        
        HorizontalLayout main = new HorizontalLayout(grid);
        
        grid.setColumns("organization", "lastName", "firstName", "middleName", "email");
        
        main.setSizeFull();
        grid.setSizeFull();
        
//            grid.getEditor().setEnabled(true);
        main.setExpandRatio(grid, 1);
        
        return main;
    }
    
    
    public void updateList(){
        List<Employee> customers = service.findAll(filterText.getValue());        
        grid.setItems(customers);
    }
    
  
    private void initCreateWindow(){
        Window subWindow = new Window("Create new entry");
        subWindow.setClosable(false);
        subWindow.setDraggable(false);
        
        ArrayList componentsList = new ArrayList();
        
        FormLayout createForm = new FormLayout();        
        
        TextField lastName = new TextField("Last name", "");
        componentsList.add(lastName);
        
        TextField firstName = new TextField("First name", "");
        componentsList.add(firstName);
        
        TextField middleName = new TextField("Middle name", "");
        componentsList.add(middleName);
        
        TextField email = new TextField("Email", "");
        componentsList.add(email);
        
        NativeSelect organization = new NativeSelect("Organization", 
                Arrays.asList(UserOganization.values()));
        organization.setWidth(100.0f, Unit.PERCENTAGE);  
        componentsList.add(organization);
        
        createForm.addComponents(lastName, firstName, middleName, email, organization);
        
        
        subWindow.setContent(createForm);
        subWindow.center();
        subWindow.setResizable(false);
        subWindow.setModal(true);
//        addWindow(subWindow);
        
        
        Button saveButton = new Button("Save");
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(saveButton, cancelButton);
        
        createForm.addComponent(buttons);
        
        saveButton.addClickListener(e ->{
            if (FillChecker.isFilled(componentsList)) {
                Employee empl = new Employee();
                empl.setLastName(lastName.getValue());
                empl.setFirstName(firstName.getValue());
                empl.setMiddleName(middleName.getValue());
                empl.setEmail(email.getValue());
                empl.setOrganization(organization.getValue().toString());
            
                service.save(empl);
            
                updateList();
                subWindow.close();                
            }
        });
        
        cancelButton.addClickListener(e ->{
            subWindow.close();
        });
        
        
    }
    
    
    private void initEditWindow(Employee employee){
        Window subWindow = new Window("Create new entry");
        subWindow.setClosable(false);
        subWindow.setDraggable(false);
        
        ArrayList componentsList = new ArrayList();
        
        FormLayout createForm = new FormLayout();
              
        TextField lastName = new TextField("Last name", employee.getLastName());
        componentsList.add(lastName);
        
        TextField firstName = new TextField("First name", employee.getFirstName());
        componentsList.add(firstName);
        
        TextField middleName = new TextField("Middle name", employee.getMiddleName());
        componentsList.add(middleName);
        
        TextField email = new TextField("Email", employee.getEmail());
        componentsList.add(email);

        NativeSelect organization = new NativeSelect("Organization", 
                Arrays.asList(UserOganization.values()));
        organization.setSelectedItem(UserOganization.valueOf(employee.getOrganization()));
        organization.setWidth(100.0f, Unit.PERCENTAGE);
        componentsList.add(organization);

        
        createForm.addComponents(lastName, firstName, middleName, email, organization);
        
        subWindow.setContent(createForm);
        subWindow.center();
        subWindow.setResizable(false);
        subWindow.setModal(true);
        addWindow(subWindow);
        
        Button saveButton = new Button("Save");
        saveButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        saveButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(saveButton, cancelButton);
        
        createForm.addComponent(buttons);
        
        saveButton.addClickListener(e ->{
            if (FillChecker.isFilled(componentsList)) {
                Employee empl = new Employee();
                empl.setId(employee.getId());
                empl.setLastName(lastName.getValue());
                empl.setFirstName(firstName.getValue());
                empl.setMiddleName(middleName.getValue());
                empl.setEmail(email.getValue());
                empl.setOrganization(organization.getValue().toString());
            
                service.save(empl);
            
                updateList();
                subWindow.close();
            }
        });
        
        cancelButton.addClickListener(e ->{
            subWindow.close();
        });
    }
    
    
    private void initDeleteWindow(Employee employee){        
        Window subWindow = new Window("Delete employee");
        subWindow.setClosable(false);
        subWindow.setDraggable(false);
        
        FormLayout subContent = new FormLayout();
        subWindow.setContent(subContent);
        subContent.addComponents(new Label("Do you want to delete this item?"),
                new Label(employee.toString()));
            
        Button yesButton = new Button("Delete");
        yesButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        yesButton.setStyleName(ValoTheme.BUTTON_DANGER);
        
        Button noButton = new Button("Cancel");
        noButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
            
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.addComponents(yesButton, noButton);
        subContent.addComponent(buttons);
        
        subWindow.center();
        subWindow.setHeight("200px");
        subWindow.setWidth("400px");
        subWindow.setResizable(false);
        subWindow.setModal(true);
        addWindow(subWindow);
            
        yesButton.addClickListener(e ->{
            service.delete(employee);
            updateList();
            subWindow.close();
        });
            
        noButton.addClickListener(e -> {
            subWindow.close();
        });
    }
 
    
    public HorizontalLayout getMainUserList(){
        return this.main;
    }
    
    
    public HorizontalLayout getUserToolbar(){
        return this.toolbar;
    }
    
    
    public VerticalLayout getUsers(){
        return new VerticalLayout(this.toolbar, this.main);
    }
    
    public GridContextMenu getGridContextMenu(){
        return this.gridContextMenu;
    }
}
