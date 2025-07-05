package com.example;  // Use your desired package name

public class MainClass {

    public static void main(String[] args) {
        // Print a welcome message to the console
        System.out.println("Hello, World!");

        // Call another method
        greetUser("John Doe");
    }

    // A simple method to greet the user
    public static void greetUser(String name) {
        System.out.println("Hello, " + name + "!");
    }
}
