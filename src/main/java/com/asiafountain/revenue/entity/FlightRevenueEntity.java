package com.asiafountain.revenue.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "flight_revenue")
@NoArgsConstructor
@Setter
@Getter
public class FlightRevenueEntity extends BaseEntity {

    @Column(name="account_id")
    private UUID accountId;

    @Column(name="provider")
    private String provider;

    @Column(name="base_price")
    private double basePrice;

    @Column(name="sell_price")
    private double sellPrice;

    @Column(name="revenue")
    private double revenue;

    @Column(name="booking_id")
    private String bookingId;

    @Column(name="partner_booking_id")
    private String partnerBookingId;

    @Column(name="transaction_date")
    private Date transactionDate;

    @Column(name="booking_status")
    private String bookingStatus = "issued";

    @Column(name="currency")
    private String currency;
}
