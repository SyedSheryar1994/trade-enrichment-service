package com.verygoodbank.tes.web.controller;

import com.verygoodbank.tes.web.service.TradeEnrichmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@Validated
public class TradeEnrichmentController {

    private final TradeEnrichmentService tradeEnrichmentService;

    /**
     * Constructor for TradeEnrichmentController.
     *
     * @param tradeEnrichmentService the service responsible for enriching trades.
     */
    public TradeEnrichmentController(TradeEnrichmentService tradeEnrichmentService) {
        this.tradeEnrichmentService = tradeEnrichmentService;
    }

    /**
     * Endpoint for enriching trades received as a CSV file.
     *
     * @param fileData    the byte array of the CSV file content.
     * @return a list of enriched trades in CSV format.
     */
    @RequestMapping(value = "/enrich", method = RequestMethod.POST, consumes = "text/csv", produces = "text/csv")
    public ResponseEntity<String> enrichTrades(@RequestBody byte[] fileData) {
        String csvResponse = tradeEnrichmentService.processTradesAndReturnCsv(fileData);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

        return ResponseEntity.ok().headers(headers).body(csvResponse);
    }
}



