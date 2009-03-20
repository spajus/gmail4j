package com.googlecode.gmail4j.client;

public class Address {

    private String email;
    private String name;
    
    public Address(final String name, final String email) {
        if (name != null) {
            this.name = name;
        }
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public String toString() {
        return name.concat(" <").concat(email).concat(">");
    }
}
