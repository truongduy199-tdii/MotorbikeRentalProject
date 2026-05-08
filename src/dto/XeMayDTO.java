package dto;

public class XeMayDTO {
    private String vehicleCode;
    private String vehicleName; // Cột này gộp từ brand + model bên DAO
    private String licensePlate;
    private double rentalPricePerDay;
    private String status;

    public XeMayDTO() {}

    public XeMayDTO(String vehicleCode, String vehicleName, String licensePlate, double rentalPricePerDay, String status) {
        this.vehicleCode = vehicleCode;
        this.vehicleName = vehicleName;
        this.licensePlate = licensePlate;
        this.rentalPricePerDay = rentalPricePerDay;
        this.status = status;
    }

    // Getters and Setters
    public String getVehicleCode() { return vehicleCode; }
    public void setVehicleCode(String vehicleCode) { this.vehicleCode = vehicleCode; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public double getRentalPricePerDay() { return rentalPricePerDay; }
    public void setRentalPricePerDay(double rentalPricePerDay) { this.rentalPricePerDay = rentalPricePerDay; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}