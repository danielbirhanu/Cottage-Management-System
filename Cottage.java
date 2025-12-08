/**
 * Cottage class representing a cottage property
 * @author Student
 * @version 1.0
 */
public class Cottage {
    // Class attributes (nouns from specification)
    private int cottageNum;
    private String cottageType;
    private int maxOccupancy;
    private double cottagePrice;
    private boolean hasSeaView;
    private boolean allowPets;
    private String email; // "free" if not booked
    
    /**
     * Constructor for Cottage object
     * @param cottageNum Cottage number
     * @param cottageType Type of cottage (Terraced, Detached, Bungalow)
     * @param maxOccupancy Maximum number of guests
     * @param cottagePrice Price per night
     * @param hasSeaView Whether cottage has sea view
     * @param allowPets Whether pets are allowed
     * @param email Booking email or "free"
     */
    public Cottage(int cottageNum, String cottageType, int maxOccupancy, 
                   double cottagePrice, boolean hasSeaView, boolean allowPets, String email) {
        this.cottageNum = cottageNum;
        this.cottageType = cottageType;
        this.maxOccupancy = maxOccupancy;
        this.cottagePrice = cottagePrice;
        this.hasSeaView = hasSeaView;
        this.allowPets = allowPets;
        this.email = email;
    }
    
    // Getters and Setters (verbs/actions)
    public int getCottageNum() {
        return cottageNum;
    }
    
    public String getCottageType() {
        return cottageType;
    }
    
    public int getMaxOccupancy() {
        return maxOccupancy;
    }
    
    public double getCottagePrice() {
        return cottagePrice;
    }
    
    public boolean getHasSeaView() {
        return hasSeaView;
    }
    
    public boolean getAllowPets() {
        return allowPets;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Check if cottage is available for booking
     * @return true if cottage is free, false otherwise
     */
    public boolean isAvailable() {
        return email.equalsIgnoreCase("free");
    }
    
    /**
     * Check if cottage matches specific requirements
     * @param cottageType Desired cottage type (or "Any")
     * @param partySize Desired party size
     * @param maxPrice Maximum price per night
     * @param seaViewRequired Whether sea view is required
     * @param petsAllowedRequired Whether pets must be allowed
     * @return true if cottage matches all requirements
     */
    public boolean matchesRequirements(String cottageType, int partySize, 
                                      double maxPrice, boolean seaViewRequired, 
                                      boolean petsAllowedRequired) {
        // Check type (allow "Any" type)
        if (!cottageType.equalsIgnoreCase("Any") && 
            !this.cottageType.equalsIgnoreCase(cottageType)) {
            return false;
        }
        
        // Check party size doesn't exceed max occupancy
        if (partySize > maxOccupancy) {
            return false;
        }
        
        // Check price is within budget
        if (cottagePrice > maxPrice) {
            return false;
        }
        
        // Check sea view requirement
        if (seaViewRequired && !hasSeaView) {
            return false;
        }
        
        // Check pets requirement
        if (petsAllowedRequired && !allowPets) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calculate match score for "next best match" functionality
     * @param cottageType Desired cottage type
     * @param partySize Desired party size
     * @param maxPrice Maximum price per night
     * @param seaViewRequired Whether sea view is required
     * @param petsAllowedRequired Whether pets must be allowed
     * @return Match score (0-5)
     */
    public int calculateMatchScore(String cottageType, int partySize, 
                                  double maxPrice, boolean seaViewRequired, 
                                  boolean petsAllowedRequired) {
        int score = 0;
        
        // Type match (allow "Any")
        if (cottageType.equalsIgnoreCase("Any") || 
            this.cottageType.equalsIgnoreCase(cottageType)) {
            score++;
        }
        
        // Party size match (must be <= maxOccupancy for booking, but we score anyway)
        if (partySize <= maxOccupancy) {
            score++;
        }
        
        // Price match
        if (cottagePrice <= maxPrice) {
            score++;
        }
        
        // Sea view match
        if (!seaViewRequired || (seaViewRequired && hasSeaView)) {
            score++;
        }
        
        // Pets match
        if (!petsAllowedRequired || (petsAllowedRequired && allowPets)) {
            score++;
        }
        
        return score;
    }
    
    /**
     * Convert cottage to string format for file storage
     * @return String representation for file
     */
    public String toFileString() {
        return cottageNum + " " + cottageType + " " + maxOccupancy + " " + 
               cottagePrice + " " + hasSeaView + " " + allowPets + " " + email;
    }
    
    /**
     * Get formatted string for display
     * @return Formatted cottage information
     */
    @Override
    public String toString() {
        String status = isAvailable() ? "Available" : "Booked by: " + email;
        return String.format("Cottage #%d: %s (Max: %d people, £%.2f/night)%n" +
                           "  Sea View: %s, Pets Allowed: %s%n" +
                           "  Status: %s",
                           cottageNum, cottageType, maxOccupancy, cottagePrice,
                           hasSeaView ? "Yes" : "No",
                           allowPets ? "Yes" : "No",
                           status);
    }
}