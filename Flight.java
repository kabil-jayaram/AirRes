import java.io.Serializable;

// Flight class represents flight information
class Flight implements Serializable {
    // Fields to store flight information
    private String id;
    private String name;
    private String source;
    private String destination;
    private String time;
    private String status;
    private int price;
    private int normalSeats;
    private int businessSeats;
    private int firstClassSeats;
    private String weightLimit;

    // Constructor to initialize flight information
    public Flight(String id, String name, String source, String destination, String time, String status, int price, int normalSeats, int businessSeats, int firstClassSeats, String weightLimit) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.time = time;
        this.status = status;
        this.price = price;
        this.normalSeats = normalSeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
        this.weightLimit = weightLimit;
    }

    // Getters and setters for all fields

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNormalSeats() {
        return normalSeats;
    }

    public void setNormalSeats(int normalSeats) {
        this.normalSeats = normalSeats;
    }

    public int getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(int businessSeats) {
        this.businessSeats = businessSeats;
    }

    public int getFirstClassSeats() {
        return firstClassSeats;
    }

    public void setFirstClassSeats(int firstClassSeats) {
        this.firstClassSeats = firstClassSeats;
    }

    public String getLuggageWeightLimit() {
        return weightLimit;
    }

    public void setLuggageWeightLimit(String weightLimit) {
        this.weightLimit = weightLimit;
    }

    // Check if there are available seats in a given seat category
    public boolean hasAvailableSeat(String seatCategory) {
        switch (seatCategory.toLowerCase()) {
            case "normal class":
                return normalSeats > 0;
            case "business class":
                return businessSeats > 0;
            case "first class":
                return firstClassSeats > 0;
            default:
                return false;
        }
    }

    // Increment the seat count for a given seat category
    public void incrementSeat(String seatCategory) {
        switch (seatCategory.toLowerCase()) {
            case "normal class":
                normalSeats++;
                break;
            case "business class":
                businessSeats++;
                break;
            case "first class":
                firstClassSeats++;
                break;
        }
    }

    // Decrement the seat count for a given seat category
    public void decrementSeat(String seatCategory) {
        switch (seatCategory.toLowerCase()) {
            case "normal class":
                normalSeats--;
                break;
            case "business class":
                businessSeats--;
                break;
            case "first class":
                firstClassSeats--;
                break;
        }
    }
}
