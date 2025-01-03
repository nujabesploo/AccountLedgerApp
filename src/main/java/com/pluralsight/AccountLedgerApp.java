import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimpleLedgerApp {

    private static final String FILE_PATH = "transactions.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Simple Ledger App!");

        while (true) {
            displayMenu();
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "D":
                    addTransaction(scanner, true);
                    break;
                case "P":
                    addTransaction(scanner, false);
                    break;
                case "L":
                    showTransactions();
                    break;
                case "X":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("D) Add Deposit");
        System.out.println("P) Make Payment");
        System.out.println("L) View Transactions");
        System.out.println("X) Exit");
        System.out.print("Choose an option: ");
    }

    private static void addTransaction(Scanner scanner, boolean isDeposit) {
        System.out.print("Enter date (yyyy-mm-dd): ");
        String date = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter amount: ");
        String amount = scanner.nextLine();

        if (isValidDate(date) && isValidAmount(amount)) {
            String transaction = date + "," + description + "," + (isDeposit ? amount : "-" + amount);
            saveTransaction(transaction);
            System.out.println("Transaction added successfully!");
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isValidAmount(String amount) {
        try {
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void saveTransaction(String transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(transaction);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    private static void showTransactions() {
        System.out.println("\nTransactions:");
        System.out.println("Date       | Description       | Amount");
        System.out.println("----------------------------------------");

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                System.out.printf("%s | %-15s | %s%n", parts[0], parts[1], parts[2]);
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }
}
