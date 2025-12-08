import java.io.*;
import java.util.ArrayList;

/**
 * FileHandler class for reading and writing cottage data
 * @author Student
 * @version 1.0
 */
public class FileHandler {
    private static final String FILENAME = "cottages.txt";
    
    /**
     * Read cottages from file
     * @return ArrayList of Cottage objects
     * @throws IOException if file cannot be read
     */
    public static ArrayList<Cottage> readCottagesFromFile() throws IOException {
        ArrayList<Cottage> cottages = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Cottage cottage = parseCottageLine(line);
                    if (cottage != null) {
                        cottages.add(cottage);
                    }
                }
            }
        }
        
        return cottages;
    }
    
    /**
     * Parse a line from the file into a Cottage object
     * @param line Line from file
     * @return Cottage object or null if invalid
     */
    private static Cottage parseCottageLine(String line) {
        try {
            String[] parts = line.split(" ");
            if (parts.length >= 7) {
                int cottageNum = Integer.parseInt(parts[0]);
                String cottageType = parts[1];
                int maxOccupancy = Integer.parseInt(parts[2]);
                double cottagePrice = Double.parseDouble(parts[3]);
                boolean hasSeaView = Boolean.parseBoolean(parts[4]);
                boolean allowPets = Boolean.parseBoolean(parts[5]);
                
                // Combine remaining parts as email (could contain spaces)
                StringBuilder emailBuilder = new StringBuilder();
                for (int i = 6; i < parts.length; i++) {
                    if (i > 6) emailBuilder.append(" ");
                    emailBuilder.append(parts[i]);
                }
                String email = emailBuilder.toString();
                
                return new Cottage(cottageNum, cottageType, maxOccupancy, 
                                 cottagePrice, hasSeaView, allowPets, email);
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line);
        }
        return null;
    }
    
    /**
     * Save cottages to file
     * @param cottages ArrayList of Cottage objects to save
     * @throws IOException if file cannot be written
     */
    public static void saveCottagesToFile(ArrayList<Cottage> cottages) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
            for (Cottage cottage : cottages) {
                writer.write(cottage.toFileString());
                writer.newLine();
            }
        }
    }
    
    /**
     * Backup cottages file before modification
     * @throws IOException if backup fails
     */
    public static void backupFile() throws IOException {
        File original = new File(FILENAME);
        File backup = new File("cottages_backup.txt");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(original));
             BufferedWriter writer = new BufferedWriter(new FileWriter(backup))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}