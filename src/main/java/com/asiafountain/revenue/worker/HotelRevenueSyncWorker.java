package com.asiafountain.revenue.worker;

import com.asiafountain.revenue.config.GeneralConfig;
import com.asiafountain.revenue.model.CurrencyModel;
import com.asiafountain.revenue.model.HotelRevenueModel;
import com.asiafountain.revenue.remote.ExpediaRemote;
import com.asiafountain.revenue.remote.HotelRevenueRemote;
import com.asiafountain.revenue.utils.FileUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Component
@Log4j2
public class HotelRevenueSyncWorker {

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    HotelRevenueRemote hotelRevenueRemote;

    @Autowired
    ExpediaRemote expediaRemote;

    @Scheduled(fixedRate = 1000000000)
    public void execute() throws Exception {
        log.info("START SYNC HOTEL REVENUE");

        //Load File CSV
        List<String> dataCSV = FileUtils.loadData(generalConfig.getHotelPath());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (int i = 1; i < dataCSV.size(); i++) {
            String data = dataCSV.get(i);
            String[] dataSplit = data.split(",");

            String bookingId = dataSplit[0];
            String partnerBookingId = dataSplit[1];
            double sellPrice = Double.parseDouble(dataSplit[2]);
            String status = dataSplit[3];
            String createdOn = dataSplit[4];
            String provider = dataSplit[5];
            String accountId = dataSplit[6];
            Date transactionDate = dateFormat.parse(createdOn.replace("\"",""));

            HotelRevenueModel model = new HotelRevenueModel();
            model.setBookingId(bookingId);
            model.setPartnerBookingId(partnerBookingId);
            model.setAccountId(UUID.fromString(accountId));
            model.setSellPrice(sellPrice);
            model.setBookingStatus(status);
            model.setTransactionDate(transactionDate);
            model.setCurrency("IDR");

            //Check Provider and Fetch Revenue
            if (provider.equalsIgnoreCase("tvlk:")) {
                updateRevenueTvlk(model);
            } else if (provider.equalsIgnoreCase("expd:")) {
                updateRevenueExpd(model);
            }

            log.info("Save Revenue "+model.getRevenue()+" "+model.getProvider());
            hotelRevenueRemote.save(model);
        }

        //Save Revenue
        log.info("DONE SYNC HOTEL REVENUE");
    }

    private void updateRevenueTvlk(HotelRevenueModel model) {
        double revenuePercentage = 0.04;
        double revenue = revenuePercentage * model.getSellPrice();

        model.setBasePrice(model.getSellPrice());
        model.setRevenue(revenue);
        model.setProvider("TVLK");
    }

    private void updateRevenueExpd(HotelRevenueModel model) {
        CurrencyModel revenue = expediaRemote.fetchHotelRevenue(model.getPartnerBookingId());
        model.setBasePrice(model.getSellPrice());
        model.setRevenue(revenue.getAmount());
        model.setCurrency(revenue.getCurrency());
        model.setProvider("EXPD");
    }

}
