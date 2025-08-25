package com.asiafountain.revenue.model;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class FlightRevenueModel extends BaseModel {
    private UUID accountId;
    private String provider;
    private double basePrice;
    private double sellPrice;
    private double revenue;
    private String bookingId;
    private String partnerBookingId;
    private String bookingStatus;
    private Date transactionDate;
    private String currency;

}
