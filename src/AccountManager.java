package securebankapp;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static final String ACCOUNTS_FILE = "c:/Users/BIGBIRD/Desktop/AUSTIN/SecureBankApp/data/accounts.txt";
    private static final String TRANSACTIONS_FILE = "c:/Users/BIGBIRD/Desktop/AUSTIN/SecureBankApp/data/transactions.txt";
    private Map<String, List<Account>> userAccounts = new HashMap<>();

    public AccountManager() throws IOException {
        loadAccounts();
    }

    private void loadAccounts() throws IOException {
        File file = new File(ACCOUNTS_FILE);
        if (!file.exists()) file.createNewFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String accNum = parts[0];
                    String username = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    Account acc = new Account(accNum, username, balance);
                    userAccounts.computeIfAbsent(username, k -> new ArrayList<>()).add(acc);
                }
            }
        }
    }

    public void createAccount(String username) throws IOException {
        String accNum = generateAccountNumber();
        Account acc = new Account(accNum, username, 0.0);
        userAccounts.computeIfAbsent(username, k -> new ArrayList<>()).add(acc);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ACCOUNTS_FILE, true))) {
            bw.write(accNum + "," + username + ",0.0\n");
        }
        System.out.println("Account created. Account Number: " + accNum);
    }

    public void viewBalance(String username) {
        List<Account> accounts = userAccounts.get(username);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        for (Account acc : accounts) {
            System.out.printf("Account: %s | Balance: %.2f\n", acc.getAccountNumber(), acc.getBalance());
        }
    }

    public void deposit(String username, Scanner scanner) throws IOException {
        Account acc = selectAccount(username, scanner);
        if (acc == null) return;
        System.out.print("Enter amount to deposit: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        acc.setBalance(acc.getBalance() + amount);
        updateAccount(acc);
        recordTransaction(acc.getAccountNumber(), "DEPOSIT", amount);
        System.out.println("Deposit successful.");
    }

    public void withdraw(String username, Scanner scanner) throws IOException {
        Account acc = selectAccount(username, scanner);
        if (acc == null) return;
        System.out.print("Enter amount to withdraw: ");
        double amount = Double.parseDouble(scanner.nextLine());
        if (amount <= 0 || amount > acc.getBalance()) {
            System.out.println("Invalid amount or insufficient funds.");
            return;
        }
        acc.setBalance(acc.getBalance() - amount);
        updateAccount(acc);
        recordTransaction(acc.getAccountNumber(), "WITHDRAW", amount);
        System.out.println("Withdrawal successful.");
    }

    public void viewTransactions(String username) throws IOException {
        List<Account> accounts = userAccounts.get(username);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        Set<String> accNums = new HashSet<>();
        for (Account acc : accounts) accNums.add(acc.getAccountNumber());
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) file.createNewFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            System.out.println("Transaction History:");
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 4 && accNums.contains(parts[0])) {
                    System.out.printf("Account: %s | Date: %s | Type: %s | Amount: %s\n", parts[0], parts[1], parts[2], parts[3]);
                }
            }
        }
    }

    private Account selectAccount(String username, Scanner scanner) {
        List<Account> accounts = userAccounts.get(username);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return null;
        }
        if (accounts.size() == 1) return accounts.get(0);
        System.out.println("Select account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("%d. %s (Balance: %.2f)\n", i + 1, accounts.get(i).getAccountNumber(), accounts.get(i).getBalance());
        }
        System.out.print("Enter choice: ");
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= accounts.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return accounts.get(idx);
    }

    private void updateAccount(Account acc) throws IOException {
        // Rewrite all accounts to file
        List<String> lines = new ArrayList<>();
        File file = new File(ACCOUNTS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) {
                    lines.add(line);
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(acc.getAccountNumber())) {
                    lines.add(acc.getAccountNumber() + "," + acc.getUsername() + "," + acc.getBalance());
                } else {
                    lines.add(line);
                }
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
            for (String l : lines) {
                bw.write(l + "\n");
            }
        }
    }

    private void recordTransaction(String accNum, String type, double amount) throws IOException {
        String date = new Date().toString();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            bw.write(accNum + "," + date + "," + type + "," + amount + "\n");
        }
    }

    private String generateAccountNumber() {
        return "AC" + (System.currentTimeMillis() % 1000000000L);
    }
}
