package com.asiafountain.revenue.worker;

import com.asiafountain.revenue.config.GeneralConfig;
import com.asiafountain.revenue.model.FlightRevenueModel;
import com.asiafountain.revenue.remote.FlightRevenueRemote;
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
public class FlightRevenueSyncWorker {

    @Autowired
    GeneralConfig generalConfig;

    @Autowired
    FlightRevenueRemote flightRevenueRemote;
    @Scheduled(fixedRate = 1000000000)
    public void execute() throws Exception {
        log.info("START SYNC FLIGHT REVENUE");
        //Load File CSV
        List<String> dataCSV = FileUtils.loadData(generalConfig.getFlightPath());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (int i = 1; i < dataCSV.size(); i++) {
            String data = dataCSV.get(i);
            String[] dataSplit = data.split(",");

            String bookingId = dataSplit[0];
            String partnerBookingId = dataSplit[1];
            double basePrice = Double.parseDouble(dataSplit[2]);
            String status = dataSplit[3];
            String createdOn = dataSplit[4];
            String provider = dataSplit[5];
            String accountId = dataSplit[6];
            double sellPrice = Double.parseDouble(dataSplit[7]);
            Date transactionDate = dateFormat.parse(createdOn.replace("\"",""));

            FlightRevenueModel model = new FlightRevenueModel();
            model.setBookingId(bookingId);
            model.setPartnerBookingId(partnerBookingId);
            model.setAccountId(UUID.fromString(accountId));
            model.setSellPrice(sellPrice);
            model.setBasePrice(basePrice);
            model.setBookingStatus(status);
            model.setTransactionDate(transactionDate);
            model.setCurrency("IDR");

            //Check Provider and Fetch Revenue
            if (provider.equalsIgnoreCase("tvlk:")) {
                updateRevenueTvlk(model);
            } else if (provider.equalsIgnoreCase("tktdcm:")) {
                updateRevenueTkd(model);
            }

            log.info("Save Revenue "+model.getRevenue()+" "+model.getProvider());
            flightRevenueRemote.save(model);
        }
        log.info("DONE SYNC FLIGHT REVENUE");
    }

    private void updateRevenueTvlk(FlightRevenueModel model) {
        double revenuePercentage = 0.012;
        double revenue = revenuePercentage * model.getSellPrice();

        model.setBasePrice(model.getSellPrice());
        model.setRevenue(revenue);
        model.setProvider("TVLK");
    }

    private void updateRevenueTkd(FlightRevenueModel model) {
        double revenue = model.getSellPrice() - model.getBasePrice();

        model.setRevenue(revenue);
        model.setProvider("TKD");
    }

}
