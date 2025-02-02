// Import required libraries for file operations
import java.io.*;
// Import libraries for date handling
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
// Import libraries for data structures
import java.util.ArrayList;
import java.util.List;
// Import Scanner for user input
import java.util.Scanner;

// Main class for managing a simple financial ledger
public class SimpleLedgerApp {
    // Define constant for the file where transactions will be stored
    private static final String FILE_PATH = "transactions.csv";
    // Define date format pattern for transaction dates
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Main method - entry point of the program
    public static void main(String[] args) {
        // Create Scanner object for reading user input
        Scanner scanner = new Scanner(System.in);
        
        // Display welcome message
        System.out.println("Welcome to the Simple Ledger App!");
        
        // Main program loop
        while (true) {
            // Show menu options to user
            displayMenu();
            
            // Get user's choice and convert to uppercase
            String choice = scanner.nextLine().toUpperCase();
            
            // Process user's choice
            switch (choice) {
                case "D":    // Handle deposit
                    addTransaction(scanner, true);  // true indicates a deposit
                    break;
                    
                case "P":    // Handle payment
                    addTransaction(scanner, false); // false indicates a payment
                    break;
                    
                case "L":    // List all transactions
                    showTransactions();
                    break;
                    
                case "X":    // Exit the program
                    System.out.println("Goodbye!");
                    return;
                    
                default:     // Handle invalid input
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Display the main menu options
    private static void displayMenu() {
        System.out.println("\nMenu:");
        System.out.println("D) Add Deposit");
        System.out.println("P) Make Payment");
        System.out.println("L) View Transactions");
        System.out.println("X) Exit");
        System.out.print("Choose an option: ");
    }

    // Add a new transaction (either deposit or payment)
    private static void addTransaction(Scanner scanner, boolean isDeposit) {
        // Get transaction date from user
        System.out.print("Enter date (yyyy-mm-dd): ");
        String date = scanner.nextLine();
        
        // Get transaction description
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        
        // Get transaction amount
        System.out.print("Enter amount: ");
        String amount = scanner.nextLine();
        
        // Validate user input before saving
        if (isValidDate(date) && isValidAmount(amount)) {
            // Create transaction string (negative amount for payments)
            String transaction = date + "," + description + "," + (isDeposit ? amount : "-" + amount);
            // Save the transaction to file
            saveTransaction(transaction);
            System.out.println("Transaction added successfully!");
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    // Validate the date format
    private static boolean isValidDate(String date) {
        try {
            // Attempt to parse the date string
            LocalDate.parse(date, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;  // Return false if date format is invalid
        }
    }

    // Validate the amount format
    private static boolean isValidAmount(String amount) {
        try {
            // Attempt to parse the amount as a double
            Double.parseDouble(amount);
            return true;
        } catch (NumberFormatException e) {
            return false;  // Return false if amount format is invalid
        }
    }

    // Save transaction to the CSV file
    private static void saveTransaction(String transaction) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Write the transaction and add a new line
            writer.write(transaction);
            writer.newLine();
        } catch (IOException e) {
            // Handle any file writing errors
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    // Display all transactions from the CSV file
    private static void showTransactions() {
        // Print header for transaction list
        System.out.println("\nTransactions:");
        System.out.println("Date       | Description       | Amount");
        System.out.println("----------------------------------------");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            // Read and display each transaction
            while ((line = reader.readLine()) != null) {
                // Split the CSV line into parts
                String[] parts = line.split(",");
                // Format and display the transaction
                System.out.printf("%s | %-15s | %s%n", parts[0], parts[1], parts[2]);
            }
        } catch (IOException e) {
            // Handle any file reading errors
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }
}
