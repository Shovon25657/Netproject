package com.businesschat;

import java.sql.Connection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        // Test database connection
        Connection conn = DBConnection.connect();
        if (conn != null) {
            System.out.println("Database connection successful!");
        }

        BusinessChatApplication.main(args);


    }
}
