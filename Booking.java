import java.io.*;

class Booking implements Serializable {
    private String bookingId;
    private String customerId;
    private String customerName;
    private Flight flight;
    private String seatCategory;
    private String checkInOption;
    private int price;

    public Booking(String id, String name, Flight flight, String seatCategory, String checkInOption, int price) {
        this.bookingId = generateBookingId(flight.getName()); // Generate booking ID based on flight name
        this.customerId = id;
        this.customerName = name;
        this.flight = flight;
        this.seatCategory = seatCategory;
        this.checkInOption = checkInOption;
        this.price = price;
        updateBookingIdCounter(flight.getName()); // Increment the counter for the flight name
    }

    // Getter for bookingId
    public String getBookingId() {
        return bookingId;
    }

    // Setter for bookingId
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    // Getter for customerId
    public String getCustomerId() {
        return customerId;
    }

    // Getter for customerName
    public String getCustomerName() {
        return customerName;
    }

    // Setter for customerName
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Getter for flight
    public Flight getFlight() {
        return flight;
    }

    // Setter for flight
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    // Getter for seatCategory
    public String getSeatCategory() {
        return seatCategory;
    }

    // Setter for seatCategory
    public void setSeatCategory(String seatCategory) {
        this.seatCategory = seatCategory;
    }

    // Getter for checkInOption
    public String getCheckInOption() {
        return checkInOption;
    }

    // Setter for checkInOption
    public void setCheckInOption(String checkInOption) {
        this.checkInOption = checkInOption;
    }

    // Getter for price
    public int getPrice() {
        return price;
    }

    // Setter for price
    public void setPrice(int price) {
        this.price = price;
    }

    // Method to generate a booking ID based on flight name
    private String generateBookingId(String flightName) {
        String classCode = ""; // You need to extract the class code from the flight name

        // Assuming you can extract the class code from the flight name (e.g., first 3 characters)
        if (flightName.length() >= 3) {
            classCode = flightName.substring(0, 3);
        }

        int counter = getBookingIdCounter(flightName); // Get the counter for the flight name
        String counterStr = String.format("%03d", counter); // Format the counter as a 3-digit string

        // Create a booking ID by combining the class code and the counter
        return classCode + counterStr;
    }

    // Method to get the current booking ID counter for a flight name from a file
    private int getBookingIdCounter(String flightName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(flightName + "_counter.dat"));
            int counter = Integer.parseInt(reader.readLine());
            reader.close();
            return counter;
        } catch (IOException | NumberFormatException e) {
            // Handle exceptions, e.g., file not found or invalid counter
            return 1; // Default to 1 if counter file is not found or invalid
        }
    }

    // Method to update the booking ID counter for a flight name in a file
    private void updateBookingIdCounter(String flightName) {
        int counter = getBookingIdCounter(flightName);
        counter++; // Increment the counter
        try {
            FileWriter writer = new FileWriter(flightName + "_counter.dat");
            writer.write(Integer.toString(counter));
            writer.close();
        } catch (IOException e) {
            // Handle exceptions, e.g., file write error
        }
    }
}
