package securebankapp;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class UserManager {
    private static final String USERS_FILE = "c:/Users/BIGBIRD/Desktop/AUSTIN/SecureBankApp/data/users.txt";
    private Map<String, User> users = new HashMap<>();

    public UserManager() throws IOException {
        loadUsers();
    }

    private void loadUsers() throws IOException {
        File file = new File(USERS_FILE);
        if (!file.exists()) file.createNewFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    users.put(parts[0], new User(parts[0], parts[1]));
                }
            }
        }
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) return false;
        return user.getPasswordHash().equals(hashPassword(password));
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void createUser(String username, String password) throws IOException {
        String hash = hashPassword(password);
        users.put(username, new User(username, hash));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(username + "," + hash + "\n");
        }
    }

    public User getUser(String username) {
        return users.get(username);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
