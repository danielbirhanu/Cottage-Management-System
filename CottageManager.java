import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Main Cottage Manager application
 * Implements console-based cottage booking system
 * @author Student
 * @version 1.0
 */
public class CottageManager {
    private ArrayList<Cottage> cottages;
    private Scanner scanner;
    
    /**
     * Constructor - loads cottages from file
     */
    public CottageManager() {
        scanner = new Scanner(System.in);
        loadCottages();
    }
    
    /**
     * Load cottages from file at startup
     */
    private void loadCottages() {
        try {
            cottages = FileHandler.readCottagesFromFile();
            System.out.println("✓ Successfully loaded " + cottages.size() + " cottages from file.");
        } catch (IOException e) {
            System.err.println("✗ Error loading cottages from file: " + e.getMessage());
            System.out.println("Starting with empty cottage list.");
            cottages = new ArrayList<>();
        }
    }
    
    /**
     * Save cottages to file before exit
     */
    private void saveCottages() {
        try {
            FileHandler.backupFile(); // Backup before saving
            FileHandler.saveCottagesToFile(cottages);
            System.out.println("✓ Cottage data saved successfully.");
        } catch (IOException e) {
            System.err.println("✗ Error saving cottages to file: " + e.getMessage());
        }
    }
    
    /**
     * Display main menu and handle user input
     */
    public void run() {
        boolean running = true;
        
        System.out.println("\n" + 
            "╔═══════════════════════════════════════╗\n" +
            "║      COTTAGE BOOKING SYSTEM           ║\n" +
            "╚═══════════════════════════════════════╝\n");
        
        while (running) {
            displayMainMenu();
            String choice = getInput("Pick: ").toUpperCase();
            
            switch (choice) {
                case "1":
                    reserveCottage();
                    break;
                case "2":
                    cancelCottage();
                    break;
                case "3":
                    viewReservations();
                    break;
                case "Q":
                    System.out.println("\nSaving data and exiting...");
                    saveCottages();
                    System.out.println("Thank you for using Cottage Booking System!");
                    running = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid choice. Please try again.");
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    /**
     * Display the main menu
     */
    private void displayMainMenu() {
        System.out.println("\n" + 
            "╔═══════════════════════════════════════╗\n" +
            "║            MAIN MENU                  ║\n" +
            "╠═══════════════════════════════════════╣\n" +
            "║  1 - Reserve Cottage                  ║\n" +
            "║  2 - Cancel Cottage                   ║\n" +
            "║  3 - View Cottage Reservations        ║\n" +
            "║  Q - Quit                             ║\n" +
            "╚═══════════════════════════════════════╝");
    }
    
    /**
     * Reserve a cottage based on user requirements
     */
    private void reserveCottage() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║          RESERVE A COTTAGE            ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        // Get user requirements with validation
        String cottageType = getCottageTypeInput();
        int partySize = getPositiveIntInput("Enter party size: ");
        double maxPrice = getPositiveDoubleInput("Enter maximum price per night (£): ");
        boolean seaViewRequired = getYesNoInput("Require sea view? (Y/N): ");
        boolean petsAllowedRequired = getYesNoInput("Require pets allowed? (Y/N): ");
        String email = getEmailInput();
        
        // Find matching cottages
        ArrayList<Cottage> matches = findMatchingCottages(cottageType, partySize, 
                                                         maxPrice, seaViewRequired, 
                                                         petsAllowedRequired);
        
        if (!matches.isEmpty()) {
            // Display matches
            System.out.println("\n✓ Found " + matches.size() + " matching cottage(s):");
            for (int i = 0; i < matches.size(); i++) {
                System.out.println("\n[" + (i + 1) + "] " + matches.get(i));
            }
            
            // Let user choose which cottage to book
            int choice = getIntInRange("\nEnter cottage number to book (1-" + 
                                      matches.size() + ") or 0 to cancel: ", 
                                      0, matches.size());
            
            if (choice > 0) {
                Cottage chosenCottage = matches.get(choice - 1);
                chosenCottage.setEmail(email);
                System.out.println("\n✓ Successfully booked Cottage #" + 
                                 chosenCottage.getCottageNum() + " for " + email);
            } else {
                System.out.println("\nBooking cancelled.");
            }
        } else {
            System.out.println("\n✗ No cottages match all your requirements.");
            findNextBestMatch(cottageType, partySize, maxPrice, 
                            seaViewRequired, petsAllowedRequired);
        }
    }
    
    /**
     * Find next best matches when no perfect match is found
     */
    private void findNextBestMatch(String cottageType, int partySize, 
                                  double maxPrice, boolean seaViewRequired, 
                                  boolean petsAllowedRequired) {
        System.out.println("\nLooking for next best matches...\n");
        
        Cottage bestMatch = null;
        int bestScore = 0;
        
        for (Cottage cottage : cottages) {
            if (cottage.isAvailable()) {
                int score = cottage.calculateMatchScore(cottageType, partySize, 
                                                       maxPrice, seaViewRequired, 
                                                       petsAllowedRequired);
                
                if (score > bestScore) {
                    bestScore = score;
                    bestMatch = cottage;
                }
            }
        }
        
        if (bestMatch != null && bestScore >= 4) {
            System.out.println("✓ Found a cottage matching " + bestScore + " of 5 requirements:");
            System.out.println(bestMatch);
            
            if (getYesNoInput("\nWould you like to book this cottage? (Y/N): ")) {
                String email = getEmailInput();
                bestMatch.setEmail(email);
                System.out.println("\n✓ Successfully booked Cottage #" + 
                                 bestMatch.getCottageNum() + " for " + email);
            }
        } else if (bestMatch != null) {
            System.out.println("Best available match (" + bestScore + "/5 requirements):");
            System.out.println(bestMatch);
        } else {
            System.out.println("✗ No suitable cottages available.");
        }
    }
    
    /**
     * Cancel a cottage reservation
     */
    private void cancelCottage() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║          CANCEL RESERVATION           ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        String email = getEmailInput();
        ArrayList<Cottage> userBookings = new ArrayList<>();
        
        // Find all cottages booked by this email
        for (Cottage cottage : cottages) {
            if (!cottage.isAvailable() && cottage.getEmail().equalsIgnoreCase(email)) {
                userBookings.add(cottage);
            }
        }
        
        if (userBookings.isEmpty()) {
            System.out.println("\n✗ No bookings found for email: " + email);
            return;
        }
        
        // Display user's bookings
        System.out.println("\nFound " + userBookings.size() + " booking(s) for " + email + ":");
        for (int i = 0; i < userBookings.size(); i++) {
            Cottage cottage = userBookings.get(i);
            System.out.println("\n[" + (i + 1) + "] Cottage #" + cottage.getCottageNum() + 
                             " - " + cottage.getCottageType() + 
                             " (£" + cottage.getCottagePrice() + "/night)");
        }
        
        // Let user choose which to cancel
        int choice = getIntInRange("\nEnter booking number to cancel (1-" + 
                                  userBookings.size() + ") or 0 to cancel: ", 
                                  0, userBookings.size());
        
        if (choice > 0) {
            Cottage cottageToCancel = userBookings.get(choice - 1);
            cottageToCancel.setEmail("free");
            System.out.println("\n✓ Successfully cancelled booking for Cottage #" + 
                             cottageToCancel.getCottageNum());
        } else {
            System.out.println("\nCancellation cancelled.");
        }
    }
    
    /**
     * View all cottage reservations
     */
    private void viewReservations() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║        VIEW RESERVATIONS              ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        int bookedCount = 0;
        int availableCount = 0;
        
        System.out.println("=== ALL COTTAGES ===");
        for (Cottage cottage : cottages) {
            System.out.println(cottage);
            System.out.println("----------------------------------------");
            
            if (cottage.isAvailable()) {
                availableCount++;
            } else {
                bookedCount++;
            }
        }
        
        System.out.println("\n=== SUMMARY ===");
        System.out.println("Total cottages: " + cottages.size());
        System.out.println("Booked: " + bookedCount);
        System.out.println("Available: " + availableCount);
    }
    
    /**
     * Find cottages matching all requirements
     */
    private ArrayList<Cottage> findMatchingCottages(String cottageType, int partySize, 
                                                   double maxPrice, boolean seaViewRequired, 
                                                   boolean petsAllowedRequired) {
        ArrayList<Cottage> matches = new ArrayList<>();
        
        for (Cottage cottage : cottages) {
            if (cottage.isAvailable() && 
                cottage.matchesRequirements(cottageType, partySize, maxPrice, 
                                          seaViewRequired, petsAllowedRequired)) {
                matches.add(cottage);
            }
        }
        
        return matches;
    }
    
    // ========== INPUT VALIDATION METHODS ==========
    
    /**
     * Get cottage type with validation
     * @return Validated cottage type
     */
    private String getCottageTypeInput() {
        while (true) {
            System.out.println("Cottage types: Terraced, Detached, Bungalow, Any");
            String type = getInput("Enter cottage type: ").trim();
            
            if (type.equalsIgnoreCase("Terraced") || 
                type.equalsIgnoreCase("Detached") || 
                type.equalsIgnoreCase("Bungalow") || 
                type.equalsIgnoreCase("Any")) {
                return type;
            }
            
            System.out.println("✗ Invalid cottage type. Please enter Terraced, Detached, Bungalow, or Any.");
        }
    }
    
    /**
     * Get valid email input
     * @return Validated email
     */
    private String getEmailInput() {
        while (true) {
            String email = getInput("Enter your email: ").trim();
            if (isValidEmail(email)) {
                return email;
            }
            System.out.println("✗ Invalid email format. Please enter a valid email.");
        }
    }
    
    /**
     * Simple email validation
     * @param email Email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
    
    /**
     * Get yes/no input
     * @param prompt Prompt to display
     * @return true for Y/Yes, false for N/No
     */
    private boolean getYesNoInput(String prompt) {
        while (true) {
            String input = getInput(prompt).toUpperCase();
            if (input.equals("Y") || input.equals("YES")) {
                return true;
            } else if (input.equals("N") || input.equals("NO")) {
                return false;
            }
            System.out.println("✗ Please enter Y or N.");
        }
    }
    
    /**
     * Get positive integer input
     * @param prompt Prompt to display
     * @return Validated positive integer
     */
    private int getPositiveIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (value > 0) {
                    return value;
                }
                System.out.println("✗ Please enter a positive number.");
            } catch (InputMismatchException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    /**
     * Get positive double input
     * @param prompt Prompt to display
     * @return Validated positive double
     */
    private double getPositiveDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = scanner.nextDouble();
                scanner.nextLine(); // Consume newline
                
                if (value > 0) {
                    return value;
                }
                System.out.println("✗ Please enter a positive amount.");
            } catch (InputMismatchException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    /**
     * Get integer within range
     * @param prompt Prompt to display
     * @param min Minimum value
     * @param max Maximum value
     * @return Validated integer within range
     */
    private int getIntInRange(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("✗ Please enter a number between " + min + " and " + max + ".");
            } catch (InputMismatchException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    
    /**
     * Get string input
     * @param prompt Prompt to display
     * @return User input string
     */
    private String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    /**
     * Main method - entry point of the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        CottageManager manager = new CottageManager();
        manager.run();
    }
}