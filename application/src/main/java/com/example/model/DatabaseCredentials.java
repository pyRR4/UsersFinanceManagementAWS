package com.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DatabaseCredentials {
    private String host;
    private int port;
    private String dbname;
    private String username;
    private String password;
}