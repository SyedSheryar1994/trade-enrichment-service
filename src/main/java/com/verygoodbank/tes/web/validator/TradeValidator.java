package com.verygoodbank.tes.web.validator;

import com.verygoodbank.tes.web.model.Trade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TradeValidator {
    private static final Logger LOGGER = Logger.getLogger(TradeValidator.class.getName());
    private static final ExecutorService logExecutor = Executors.newSingleThreadExecutor();
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = ThreadLocal.withInitial(() ->
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        simpleDateFormat.setLenient(false);
        return simpleDateFormat;

    });

    /**
     * Validates the trade object by checking the date format.
     *
     * @param trade the trade object to validate
     * @return true if the trade is valid, false otherwise
     */
    public static boolean isValid(Trade trade) {
        if (!isValidDate(trade.getDate())) {
            logExecutor.submit(()->LOGGER.warning("Invalid date format for trade with product id : " + trade.getProductId())); //In case of millions of trades, do logging in separate thread will reduce response time
            return false;
        }
        return true;
    }

    /**
     * Checks if the provided date string is valid and in the format yyyyMMdd.
     *
     * @param date the date string to validate
     * @return true if the date is valid, false otherwise
     */
    private static boolean isValidDate(String date) {
        if (date==null || date.trim().isEmpty()){
            return false;
        }
        try {
            SimpleDateFormat dateFormat = DATE_FORMAT.get();
            dateFormat.parse(date);
            return true;

        } catch (ParseException e) {
            return false;
        } finally {
            DATE_FORMAT.remove();
        }
    }
}
