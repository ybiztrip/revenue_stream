package com.asiafountain.revenue.remote;

import com.asiafountain.revenue.entity.FlightRevenueEntity;
import com.asiafountain.revenue.entity.HotelRevenueEntity;
import com.asiafountain.revenue.model.FlightRevenueModel;
import com.asiafountain.revenue.model.HotelRevenueModel;
import com.asiafountain.revenue.repository.HotelRevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HotelRevenueRemote {

    @Autowired
    HotelRevenueRepository hotelRevenueRepository;

    public HotelRevenueModel save(HotelRevenueModel model) {
        HotelRevenueEntity entity = hotelRevenueRepository.findByBookingId(model.getBookingId(), model.getProvider());
        if (entity == null) {
            entity = new HotelRevenueEntity();
        }

        updateDataEntity(entity, model);
        entity = hotelRevenueRepository.save(entity);

        return generateModel(entity);
    }

    private void updateDataEntity(HotelRevenueEntity entity, HotelRevenueModel model) {
        entity.setAccountId(model.getAccountId());
        entity.setBookingId(model.getBookingId());
        entity.setProvider(model.getProvider());
        entity.setBasePrice(model.getBasePrice());
        entity.setSellPrice(model.getSellPrice());
        entity.setRevenue(model.getRevenue());
        entity.setStatus(model.getStatus());
        entity.setBookingStatus(model.getBookingStatus());
        entity.setPartnerBookingId(model.getPartnerBookingId());
        entity.setTransactionDate(model.getTransactionDate());
        entity.setCurrency(model.getCurrency());
    }

    private HotelRevenueModel generateModel(HotelRevenueEntity entity) {
        HotelRevenueModel model = new HotelRevenueModel();
        model.setId(entity.getId());
        model.setStatus(entity.getStatus());
        model.setCreatedOn(entity.getCreatedOn());
        model.setUpdatedOn(entity.getUpdatedOn());
        model.setAccountId(entity.getAccountId());
        model.setBookingId(entity.getBookingId());
        model.setProvider(entity.getProvider());
        model.setBasePrice(entity.getBasePrice());
        model.setSellPrice(entity.getSellPrice());
        model.setRevenue(entity.getRevenue());
        model.setBookingStatus(entity.getBookingStatus());
        model.setPartnerBookingId(entity.getPartnerBookingId());
        model.setTransactionDate(entity.getTransactionDate());
        model.setCurrency(entity.getCurrency());

        return model;
    }

}
