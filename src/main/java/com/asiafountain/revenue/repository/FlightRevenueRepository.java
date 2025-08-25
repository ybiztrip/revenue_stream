package com.asiafountain.revenue.repository;

import com.asiafountain.revenue.entity.FlightRevenueEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRevenueRepository extends CrudRepository<FlightRevenueEntity, Integer> {

    @Query(value = "SELECT * FROM flight_revenue WHERE booking_id = :bookingId AND provider = :provider",nativeQuery = true)
    public FlightRevenueEntity findByBookingId(@Param("bookingId") String bookingId, @Param("provider") String provider);

}
