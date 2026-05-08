package dto;

import java.sql.Timestamp;

public class HopDongDTO {
    private String contractCode;
    private String customerName; // Lấy từ bảng CUSTOMERS
    private String vehicleName;  // Lấy từ bảng VEHICLES (brand + model)
    private Timestamp rentalStart;
    private Timestamp rentalEnd;
    private double depositAmount;
    private String contractStatus;

    // Constructor rỗng
    public HopDongDTO() {}

    // Constructor đầy đủ
    public HopDongDTO(String contractCode, String customerName, String vehicleName,
                      Timestamp rentalStart, Timestamp rentalEnd, double depositAmount, String contractStatus) {
        this.contractCode = contractCode;
        this.customerName = customerName;
        this.vehicleName = vehicleName;
        this.rentalStart = rentalStart;
        this.rentalEnd = rentalEnd;
        this.depositAmount = depositAmount;
        this.contractStatus = contractStatus;
    }

    // Getters and Setters
    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public Timestamp getRentalStart() { return rentalStart; }
    public void setRentalStart(Timestamp rentalStart) { this.rentalStart = rentalStart; }

    public Timestamp getRentalEnd() { return rentalEnd; }
    public void setRentalEnd(Timestamp rentalEnd) { this.rentalEnd = rentalEnd; }

    public double getDepositAmount() { return depositAmount; }
    public void setDepositAmount(double depositAmount) { this.depositAmount = depositAmount; }

    public String getContractStatus() { return contractStatus; }
    public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }
}