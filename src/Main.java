package securebankapp;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        BankSystem bankSystem = new BankSystem();
        bankSystem.start();
    }
}
