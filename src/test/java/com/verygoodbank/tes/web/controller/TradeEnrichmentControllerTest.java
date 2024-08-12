package com.verygoodbank.tes.web.controller;

import com.verygoodbank.tes.web.service.TradeEnrichmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class TradeEnrichmentControllerTest {

    @Mock
    private TradeEnrichmentService tradeEnrichmentService;

    @InjectMocks
    private TradeEnrichmentController tradeEnrichmentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tradeEnrichmentController).build();
    }

    @Test
    void testProcessTradesAndReturnCsv() throws Exception {
        Path csvPath = ResourceUtils.getFile("classpath:trade.csv").toPath();
        byte[] mockFileData = Files.readAllBytes(csvPath);

        // Mock the service method
        when(tradeEnrichmentService.processTradesAndReturnCsv(any(byte[].class)))
                .thenReturn("date,product_name,currency,price\n20160101,Treasury Bills Domestic,EUR,10.0");

        mockMvc.perform(post("/api/v1/enrich")
                        .content(mockFileData)
                        .contentType("text/csv"))
                .andExpect(status().isOk())
                .andExpect(content().string("date,product_name,currency,price\n20160101,Treasury Bills Domestic,EUR,10.0"));

        // Verify that the service method was called
        verify(tradeEnrichmentService).processTradesAndReturnCsv(any(byte[].class));
    }
}