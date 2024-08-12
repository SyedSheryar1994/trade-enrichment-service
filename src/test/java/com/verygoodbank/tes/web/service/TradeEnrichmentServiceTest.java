package com.verygoodbank.tes.web.service;

import com.verygoodbank.tes.web.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@ExtendWith(MockitoExtension.class)
public class TradeEnrichmentServiceTest {
    @SpyBean
    private TradeEnrichmentServiceImpl tradeEnrichmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testProcessTradesAndReturnCsv() throws Exception {
        // Load the trade.csv file from the test resources
        Path csvPath = ResourceUtils.getFile("classpath:trade.csv").toPath();
        byte[] fileData = Files.readAllBytes(csvPath);

        // Process the trades
        String resultCsv = tradeEnrichmentService.processTradesAndReturnCsv(fileData);

        // Verify the result
        String expectedCsv = "date,product_name,currency,price\n" +
                "20160101,Test Product,EUR,10.0\n" +
                "20160101,Enrichment Product,EUR,20.1\n" +
                "20160101,Missing product name,EUR,30.34\n" +
                "20160101,Missing product name,EUR,35.34";
        assertEquals(expectedCsv, resultCsv);
    }

    @Test
    void testProcessTradesAndReturnCsvInvalidDateSkipped() throws Exception {
        // Load the trade.csv file from the test resources which contains invalid date format
        Path csvPath = ResourceUtils.getFile("classpath:trade_invalid_date.csv").toPath();
        byte[] fileData = Files.readAllBytes(csvPath);

        // Process the trades
        String resultCsv = tradeEnrichmentService.processTradesAndReturnCsv(fileData);

        // Verify the result
        String expectedCsv = "date,product_name,currency,price\n" +
                "20160101,Test Product,EUR,10.0";
        assertEquals(expectedCsv, resultCsv);
    }
    @Test
    void testProcessTradesAndReturnCsvInvalidTradeDataThrowsException() throws Exception {
        // Load the trade.csv file from the test resources which contains invalid product id in String format
        Path csvPath = ResourceUtils.getFile("classpath:trade_invalid_records.csv").toPath();
        byte[] fileData = Files.readAllBytes(csvPath);

        // assert for exception check
        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            tradeEnrichmentService.processTradesAndReturnCsv(fileData);
        });
        assertEquals("Failed to parse trade CSV file", businessException.getMessage());
    }
}
