package dto;

public class XeMayDTO {
    private int vehicleId;
    private String vehicleCode;
    private String brand;
    private String model;
    private String licensePlate;
    private String color;
    private int manufactureYear;
    private double rentalPricePerDay;
    private double rentalPricePerHour;
    private String status;
    private String description;

    public XeMayDTO() {}

    // Getter và Setter
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getVehicleCode() { return vehicleCode; }
    public void setVehicleCode(String vehicleCode) { this.vehicleCode = vehicleCode; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    // Hàm tiện ích để lấy tên xe đầy đủ
    public String getVehicleName() { return brand + " " + model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(int manufactureYear) { this.manufactureYear = manufactureYear; }

    public double getRentalPricePerDay() { return rentalPricePerDay; }
    public void setRentalPricePerDay(double rentalPricePerDay) { this.rentalPricePerDay = rentalPricePerDay; }

    public double getRentalPricePerHour() { return rentalPricePerHour; }
    public void setRentalPricePerHour(double rentalPricePerHour) { this.rentalPricePerHour = rentalPricePerHour; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}