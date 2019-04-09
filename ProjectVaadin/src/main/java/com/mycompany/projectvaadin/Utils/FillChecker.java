
package com.mycompany.projectvaadin.Utils;

import com.vaadin.data.HasValue;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import java.util.ArrayList;


public class FillChecker {
    
    public static boolean isFilled(ArrayList<? extends AbstractComponent> components){
        boolean result = true;
        
        for (int i = 0; i < components.size(); i++) { 

            if (components.get(i) instanceof HasValue) {
                if (((HasValue)components.get(i)).isEmpty()) {
                    components.get(i).setComponentError(new UserError("Fill it"));
                    result = false;  
                } else{
                    components.get(i).setComponentError(null);            
                }   
            }
        }        
        return result;
    }  
}
