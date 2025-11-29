package objects;

public class Vehicle {

    private final String licensePlate;
    private final int originalValue;

    public Vehicle(String licensePlate, int originalValue) {

        if(originalValue <= 0) {
            throw new IllegalArgumentException("originalValue must be positive");
        }

        if(licensePlate == null || !(licensePlate.length()==7)) {
            throw new IllegalArgumentException("licensePlate is invalid");
        }

        for(char c : licensePlate.toCharArray()) {

            if(!(Character.isDigit(c) || (c>='A' && c<='Z'))) {
                throw new IllegalArgumentException("license plate is invalid");
            }
        }

        this.licensePlate = licensePlate;
        this.originalValue = originalValue;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getOriginalValue() {
        return originalValue;
    }
}
