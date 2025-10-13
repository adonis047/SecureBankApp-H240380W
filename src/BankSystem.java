package securebankapp;

import java.io.*;
import java.util.*;

public class BankSystem {
    private UserManager userManager;
    private AccountManager accountManager;
    private Scanner scanner;
    private User currentUser;

    public BankSystem() throws IOException {
        userManager = new UserManager();
        accountManager = new AccountManager();
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        System.out.println("Welcome to Secure Banking Application!");
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Create New Account");
            System.out.println("3. Exit");
            System.out.print("Select option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    createUser();
                    break;
                case "3":
                    System.out.println("Thank you for using Secure Banking Application.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void login() throws IOException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        if (userManager.authenticate(username, password)) {
            currentUser = userManager.getUser(username);
            System.out.println("Login successful!");
            userMenu();
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    private void createUser() throws IOException {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();
        if (userManager.userExists(username)) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.print("Choose a password: ");
        String password = scanner.nextLine();
        userManager.createUser(username, password);
        System.out.println("User created successfully. Please login.");
    }

    private void userMenu() throws IOException {
        while (true) {
            System.out.println("\n1. Create Bank Account");
            System.out.println("2. View Account Balance");
            System.out.println("3. Deposit Funds");
            System.out.println("4. Withdraw Funds");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.print("Select option: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    accountManager.createAccount(currentUser.getUsername());
                    break;
                case "2":
                    accountManager.viewBalance(currentUser.getUsername());
                    break;
                case "3":
                    accountManager.deposit(currentUser.getUsername(), scanner);
                    break;
                case "4":
                    accountManager.withdraw(currentUser.getUsername(), scanner);
                    break;
                case "5":
                    accountManager.viewTransactions(currentUser.getUsername());
                    break;
                case "6":
                    currentUser = null;
                    System.out.println("Logged out.");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
