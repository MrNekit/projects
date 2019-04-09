
package com.mycompany.projectvaadin.beans;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Employee implements Serializable, Cloneable{
    
    private int id ;    
    private String lastName = "";
    private String firstName = "";
    private String middleName = "";
    private String email = "";
    private String organization = "";
    
    public int getId(){
        return this.id;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public String getEmail(){
        return this.email;
    }
    
    public void setEmail(String email){
        this.email = email;
    }
    
    public String getLastName(){
        return this.lastName;
    }
    
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    
    public String getFirstName(){
        return this.firstName;
    }
    
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    
    public String getMiddleName(){
        return this.middleName;
    }
    
    public void setMiddleName(String middleName){
        this.middleName = middleName;
    }
       
    public String getOrganization(){
        return this.organization;
    }
    
    public void setOrganization(String organization){
        this.organization = organization;
    }
    
    public boolean isPersisted(){
        return Integer.valueOf(id) != null;
    }
    
    
    @Override
    public boolean equals(Object obj){
        if(this == obj) 
            return true;
        if(Integer.valueOf(this.id) == null) 
            return false;
        if(obj instanceof Employee && obj.getClass().equals(getClass()))
            return Integer.valueOf(this.id).equals(((Employee)obj).id);
        return false;
    }
    
    @Override
    public int hashCode(){
        int hash = 5;
        hash = 43 * hash + (Integer.valueOf(this.id) == null ? 0 : Integer.valueOf(this.id).hashCode());
        return hash;
    }
    
    @Override
    public Employee clone() throws CloneNotSupportedException{
        return (Employee) super.clone();
    }
    
    @Override
    public String toString(){
        return lastName + " " + firstName + " " + middleName + ", " + organization
                + ", " + email;
    }
}
