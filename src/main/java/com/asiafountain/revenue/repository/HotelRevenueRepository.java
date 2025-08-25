package com.asiafountain.revenue.repository;

import com.asiafountain.revenue.entity.HotelRevenueEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRevenueRepository extends CrudRepository<HotelRevenueEntity, Integer> {

    @Query(value = "SELECT * FROM hotel_revenue WHERE booking_id = :bookingId AND provider = :provider",nativeQuery = true)
    public HotelRevenueEntity findByBookingId(@Param("bookingId") String bookingId, @Param("provider") String provider);
}
