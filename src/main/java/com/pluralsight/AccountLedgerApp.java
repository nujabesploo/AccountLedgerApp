package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AccountLedgerApp {

        // Regex pattern for hh:mm:ss format
        private static final String TIME_PATTERN = "^([01]?\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$";
        private static final Pattern pattern = Pattern.compile(TIME_PATTERN);
        private static final String FILE_PATH = "transactions.csv";
        private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n      -----Whats Popping, Yerr!! New York Bodega! Bing Bong!-----    ");
            while (true) {
                showHomeScreen(scanner);
            }
        }

        // Home screen options
        private static void showHomeScreen(Scanner scanner) {

            System.out.println("Home Screen:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().toUpperCase();

            switch (choice) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    makePayment(scanner);
                    break;
                case "L":
                    showLedger(scanner);
                    break;
                case "X":
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }

        // Add a deposit
        private static void addDeposit(Scanner scanner) {
            System.out.print("Enter date (yyyy-mm-dd): ");
            String date = scanner.nextLine();
            System.out.print("Enter time (hh:mm:ss): ");
            String time = scanner.nextLine();
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();
            System.out.print("Enter amount (positive for deposit): ");
            String amount = scanner.nextLine();

            String[] newTransaction = {date, time, description, vendor, amount};

            // Write the new transaction to the CSV file
            if(isValidDate(date) && isValidTime(time) && isNumber(amount))
            {
                saveTransactionToFile(newTransaction);

                System.out.println("Deposit added.");
            }
            else{
                System.out.println("Sorry data not added . Please follow input data format");
            }

        }

        // Save a single transaction to the CSV file
        private static void saveTransactionToFile(String[] transaction) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
                writer.write(String.join("|", transaction));
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Error saving transaction: " + e.getMessage());
            }
        }


        // Make a payment (debit)
        private static void makePayment(Scanner scanner) {
            System.out.print("Enter date (yyyy-mm-dd): ");
            String date = scanner.nextLine();
            System.out.print("Enter time (hh:mm:ss): ");
            String time = scanner.nextLine();
            System.out.print("Enter description: ");
            String description = scanner.nextLine();
            System.out.print("Enter vendor: ");
            String vendor = scanner.nextLine();
            System.out.print("Enter amount (negative for payment): ");
            String amount = scanner.nextLine();

            String[] newTransaction = {date, time, description, vendor, amount};



            // Write the new transaction to the CSV file
            if(isValidDate(date) && isValidTime(time) && isNumber(amount))
            {
                // Save the payment transaction to the CSV file
                saveTransactionToFile(newTransaction);

                System.out.println("Payment added.");
            }
            else{
                System.out.println("Sorry data not added . Please follow input data format");
            }
        }




        // Show Ledger screen
        private static void showLedger(Scanner scanner) {
            while (true) { // Start an infinite loop
                System.out.println("\nLedger:");
                System.out.println("A) All Entries");
                System.out.println("D) Deposits Only");
                System.out.println("P) Payments Only");
                System.out.println("R) Reports");
                System.out.println("H) Home");

                System.out.print("Choose an option: ");
                String choice = scanner.nextLine().toUpperCase();

                switch (choice) {
                    case "A":
                        List<String[]> allTransactions = readTransactionsFromFile(); // Read from the CSV file
                        displayTransactions(allTransactions);
                        break;
                    case "D":
                        displayFilteredTransactions(true);
                        break;
                    case "P":
                        displayFilteredTransactions(false);
                        break;
                    case "R":
                        showReports(scanner);
                        break;
                    case "H":
                        return; // Exit the method and return to the previous menu
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        }


        // Display all transactions
        private static void displayTransactions(List<String[]> transactionsToShow) {
            System.out.println("\nDate       | Time     | Description         | Vendor   | Amount");
            System.out.println("---------------------------------------------------------------");
            for (String[] transaction : transactionsToShow) {
                System.out.printf("%s | %s | %-18s | %-8s | %8s%n",
                        transaction[0], transaction[1], transaction[2], transaction[3], transaction[4]);
            }
        }

        // Read transactions from the CSV file
        private static List<String[]> readTransactionsFromFile() {
            List<String[]> transactionsFromFile = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] transaction = line.split("\\|");  // Assuming "|" is used as delimiter
                    transactionsFromFile.add(transaction);
                }
            } catch (IOException e) {
                System.out.println("Error reading transactions: " + e.getMessage());
            }
            return transactionsFromFile;
        }

        // Filter transactions: true for deposits, false for payments
        private static void displayFilteredTransactions(boolean isDeposit) {
            List<String[]> allTransactions = readTransactionsFromFile(); // Read from the CSV file
            List<String[]> filtered = new ArrayList<>();

            for (String[] transaction : allTransactions) {
                double amount = Double.parseDouble(transaction[4]);
                if ((isDeposit && amount > 0) || (!isDeposit && amount < 0)) {
                    filtered.add(transaction);
                }
            }

            displayTransactions(filtered);  // Display the filtered transactions
        }


        // Show Reports
        private static void showReports(Scanner scanner) {
            while (true) { // Start an infinite loop
                System.out.println("\nReports:");
                System.out.println("1) Month To Date");
                System.out.println("2) Previous Month");
                System.out.println("3) Year To Date");
                System.out.println("4) Previous Year");
                System.out.println("5) Search by Vendor");
                System.out.println("6) Custom Search"); // New option
                System.out.println("0) Back");

                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        displayMonthToDateReport();
                        break;
                    case "2":
                        displayPreviousMonthReport();
                        break;
                    case "3":
                        displayYearToDateReport();
                        break;
                    case "4":
                        displayPreviousYearReport();
                        break;
                    case "5":
                        searchByVendor(scanner);
                        break;
                    case "6":
                        customSearch(scanner); // Call the custom search method
                        break; // Make sure to break after calling the method
                    case "0":
                        return; // Exit the method and return to the previous menu
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        }


        // Utility method to clean up the date string
        private static String cleanDateString(String date) {
            return date.replaceAll("[^\\x20-\\x7E]", "").trim(); // Removes non-ASCII characters and trims
        }

        // In displayFilteredByDate method
        private static void displayFilteredByDate(LocalDate startDate, LocalDate endDate) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<String[]> filtered = new ArrayList<>();
            List<String[]> transactions = readTransactionsFromFile(); // Read from the CSV file
            for (String[] transaction : transactions) {
                // Clean the date string before parsing
                String cleanedDate = cleanDateString(transaction[0]);
                try {
                    LocalDate transactionDate = LocalDate.parse(cleanedDate, dateFormatter);

                    if ((transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate))
                            && (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate))) {
                        filtered.add(transaction);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Error parsing date: " + cleanedDate);
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("No transactions found in the given date range.");
            } else {
                displayTransactions(filtered);
            }
        }

        // Display transactions for Month to Date
        private static void displayMonthToDateReport() {
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfMonth = today.withDayOfMonth(1);
            System.out.println(firstDayOfMonth+" "+today);
            System.out.println("\nMonth to Date Transactions:");
            displayFilteredByDate(firstDayOfMonth, today);
        }

        // Display transactions for Previous Month
        private static void displayPreviousMonthReport() {
            LocalDate today = LocalDate.now();
            YearMonth previousMonth = YearMonth.from(today).minusMonths(1);
            LocalDate firstDayOfPreviousMonth = previousMonth.atDay(1);
            LocalDate lastDayOfPreviousMonth = previousMonth.atEndOfMonth();

            System.out.println("\nPrevious Month Transactions:");
            displayFilteredByDate(firstDayOfPreviousMonth, lastDayOfPreviousMonth);
        }

        // Display transactions for Year to Date
        private static void displayYearToDateReport() {
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfYear = today.withDayOfYear(1);

            System.out.println("\nYear to Date Transactions:");
            displayFilteredByDate(firstDayOfYear, today);
        }

        // Display transactions for Previous Year
        private static void displayPreviousYearReport() {
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfPreviousYear = today.minusYears(1).withDayOfYear(1);
            LocalDate lastDayOfPreviousYear = firstDayOfPreviousYear.withDayOfYear(firstDayOfPreviousYear.lengthOfYear());

            System.out.println("\nPrevious Year Transactions:");
            displayFilteredByDate(firstDayOfPreviousYear, lastDayOfPreviousYear);
        }

        // Search transactions by vendor
        private static void searchByVendor(Scanner scanner) {
            System.out.print("Enter vendor name: ");
            String vendor = scanner.nextLine();
            List<String[]> transactions = readTransactionsFromFile(); // Read from the CSV file
            List<String[]> filtered = new ArrayList<>();
            for (String[] transaction : transactions) {
                if (transaction[3].equalsIgnoreCase(vendor)) {
                    filtered.add(transaction);
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("No transactions found for vendor: " + vendor);
            } else {
                displayTransactions(filtered);
            }
        }




        private static void customSearch(Scanner scanner) {
            System.out.print("Enter start date (yyyy-mm-dd) or leave empty: ");
            String startDateInput = scanner.nextLine().trim();
            startDateInput = cleanDateString(startDateInput);  // Clean the date string
            LocalDate startDate = null;
            if (!startDateInput.isEmpty()) {
                try {
                    startDate = LocalDate.parse(startDateInput);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid start date format. Please use yyyy-mm-dd.");
                    return; // Handle according to your application's flow
                }
            }

            System.out.print("Enter end date (yyyy-mm-dd) or leave empty: ");
            String endDateInput = scanner.nextLine().trim();
            endDateInput = cleanDateString(endDateInput);  // Clean the date string
            LocalDate endDate = null;
            if (!endDateInput.isEmpty()) {
                try {
                    endDate = LocalDate.parse(endDateInput);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid end date format. Please use yyyy-mm-dd.");
                    return; // Handle according to your application's flow
                }
            }

            System.out.print("Enter description or leave empty: ");
            String description = scanner.nextLine().trim();

            System.out.print("Enter vendor or leave empty: ");
            String vendor = scanner.nextLine().trim();

            System.out.print("Enter amount (leave empty for no filter): ");
            String amountInput = scanner.nextLine().trim();
            Double amount = null;
            if (!amountInput.isEmpty()) {
                try {
                    amount = Double.parseDouble(amountInput);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid amount format. Please enter a valid number.");
                    return; // Handle according to your application's flow
                }
            }

            List<String[]> filteredTransactions = new ArrayList<>();
            List<String[]> transactions = readTransactionsFromFile(); // Read from the CSV file

            for (String[] transaction : transactions) {
                LocalDate transactionDate = LocalDate.parse(cleanDateString(transaction[0]));
                String transactionDescription = transaction[2];
                String transactionVendor = transaction[3];
                Double transactionAmount = Double.parseDouble(transaction[4]);

                boolean matches = true;

                if (startDate != null && transactionDate.isBefore(startDate)) {
                    matches = false;
                }
                if (endDate != null && transactionDate.isAfter(endDate)) {
                    matches = false;
                }
                if (!description.isEmpty() && !transactionDescription.toLowerCase().contains(description.toLowerCase())) {
                    matches = false;
                }
                if (!vendor.isEmpty() && !transactionVendor.equalsIgnoreCase(vendor)) {
                    matches = false;
                }
                if (amount != null && !transactionAmount.equals(amount)) {
                    matches = false;
                }

                if (matches) {
                    filteredTransactions.add(transaction);
                }
            }

            if (filteredTransactions.isEmpty()) {
                System.out.println("No transactions found for the specified criteria.");
            } else {
                displayTransactions(filteredTransactions); // Assume you have a method to display transactions
            }
        }
        // Method to check if a string is a valid date
        public static boolean isValidDate(String dateString) {
            if (dateString == null || dateString.isEmpty()) {
                return false; // Return false for null or empty strings
            }

            // Define the expected date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                LocalDate parsedDate = LocalDate.parse(dateString, formatter);
                return true; // Successfully parsed, return true
            } catch (DateTimeParseException e) {
                return false; // Parsing failed, return false
            }
        }
        // Method to check if a string is a valid number
        public static boolean isNumber(String numberString) {
            if (numberString == null || numberString.isEmpty()) {
                return false; // Return false for null or empty strings
            }

            try {
                // Try to parse the string as a number (can be Integer, Double, etc.)
                Double.parseDouble(numberString); // Use Double for broader range
                return true; // Successfully parsed, return true
            } catch (NumberFormatException e) {
                return false; // Parsing failed, return false
            }
        }
        // Method to check if a string is in hh:mm:ss format
        public static boolean isValidTime(String timeString) {
            if (timeString == null || timeString.isEmpty()) {
                return false; // Return false for null or empty strings
            }

            return pattern.matcher(timeString).matches(); // Match against the regex pattern
        }
    }

