package com.membership.hub.security;

public class Credentials {
    public static final String AUTHORIZATION = "Authorization";

    private String id;
    private String password;

    public Credentials(String header) {
        if (header.startsWith("Basic ")) {
            header = header.substring("Basic ".length());
            if (header.indexOf('#')> 0 ){
                this.id = header.substring(0, header.indexOf('#'));
                this.password = header.substring(header.indexOf('#')+1);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
