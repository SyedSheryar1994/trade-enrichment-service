package com.verygoodbank.tes.web.service;

/**
 * Interface for the service responsible for enriching trades.
 */
public interface TradeEnrichmentService {
    /**
     * Processes the trades received in the CSV file data and returns the enriched trades as a CSV-formatted string.
     *
     * @param fileData the byte array containing the trade CSV data.
     * @return a CSV-formatted string representing the enriched trades.
     */
    String processTradesAndReturnCsv(byte[] fileData);

}
