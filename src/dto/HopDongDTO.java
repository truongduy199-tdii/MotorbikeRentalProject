package dto;

import java.sql.Timestamp;

public class HopDongDTO {
    private String contractCode;
    private int customerId;  // Thêm ID khách hàng (dùng để thêm mới)
    private int vehicleId;   // Thêm ID xe (dùng để thêm mới)
    private String customerName;
    private String vehicleName;
    private Timestamp rentalStart;
    private Timestamp rentalEnd;
    private double depositAmount;
    private double totalAmount;
    private String rentalType; // 'DAY' hoặc 'HOUR'
    private String contractStatus;

    public HopDongDTO() {}

    // ================= GETTERS VÀ SETTERS =================

    public String getContractCode() { return contractCode; }
    public void setContractCode(String contractCode) { this.contractCode = contractCode; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

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

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getRentalType() { return rentalType; }
    public void setRentalType(String rentalType) { this.rentalType = rentalType; }

    public String getContractStatus() { return contractStatus; }
    public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }
}