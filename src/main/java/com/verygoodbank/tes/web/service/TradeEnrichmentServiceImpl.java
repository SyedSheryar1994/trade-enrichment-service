package com.verygoodbank.tes.web.service;

import com.verygoodbank.tes.web.model.Trade;
import com.verygoodbank.tes.web.util.CsvUtils;
import com.verygoodbank.tes.web.validator.TradeValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Implementation of the TradeEnrichmentService interface.
 * Responsible for enriching trades by adding product names based on product IDs.
 */
@Service
public class TradeEnrichmentServiceImpl implements TradeEnrichmentService {
    private final Map<Integer, String> productCache;
    @Value("${trade.enrichment.batch-size}")
    private Integer batchSize;

    @Value("${product.csv.filepath}")
    private String productCsvFilePath;
    private final ExecutorService logExecutor = Executors.newSingleThreadExecutor();

    private static final Logger LOGGER = Logger.getLogger(TradeValidator.class.getName());
    /**
     * Constructor for TradeEnrichmentServiceImpl.
     * Initializes the product cache and trade validator.
     */
    public TradeEnrichmentServiceImpl() {
        this.productCache = new ConcurrentHashMap<>();
    }

    /**
     * Loads the product data from the CSV file into the product cache.
     */
    @PostConstruct
    private void loadProductMap() {
        CsvUtils.parseProductsAndPopulate(productCsvFilePath, productCache);
    }

    /**
     * Processes the trades and returns the enriched trades as a CSV-formatted string.
     *
     * @param fileData       the byte array containing the trade CSV data.
     * @return a CSV-formatted string representing the enriched trades.
     */
    @Override
    public String processTradesAndReturnCsv(byte[] fileData) {
        List<Trade> trades = CsvUtils.parseTrades(fileData);
        Queue<Trade> enrichedTrades = processTradesInBatches(trades, batchSize);
        return CsvUtils.convertToCsv(enrichedTrades);
    }

    /**
     * Processes trades in batches to handle large datasets efficiently.
     *
     * @param trades          the list of trades to be processed.
     * @param customBatchSize the custom batch size for processing trades.
     * @return a list of enriched trades.
     */
    private Queue<Trade> processTradesInBatches(List<Trade> trades, int customBatchSize) {
        int effectiveBatchSize = customBatchSize > 0 ? customBatchSize : this.batchSize;
        Queue<Trade> result = new ConcurrentLinkedDeque<>();

        IntStream.range(0, (trades.size() + effectiveBatchSize - 1) / effectiveBatchSize)
                .parallel()
                .forEach(i -> {
                    int start = i * effectiveBatchSize;
                    int end = Math.min(start + effectiveBatchSize, trades.size());
                    List<Trade> batch = trades.subList(start, end);

                    List<Trade> enrichedBatch = enrichTrades(batch);
                    result.addAll(enrichedBatch);
                });

        return result;

    }

    /**
     * Enriches a list of trades by adding the corresponding product names.
     *
     * @param trades the list of trades to be enriched.
     * @return a list of enriched trades.
     */
    private List<Trade> enrichTrades(List<Trade> trades) {
        return trades.parallelStream()
                .filter(TradeValidator::isValid)
                .map(this::enrichTrade)
                .collect(Collectors.toList());
    }

    /**
     * Enriches a single trade by adding the corresponding product name.
     * If the product is not found, sets the product name as "Missing Product Name".
     *
     * @param trade the trade to be enriched.
     * @return the enriched trade.
     */
    private Trade enrichTrade(Trade trade) {
        String productName = productCache.getOrDefault(trade.getProductId(), "Missing product name");
        trade.setProductName(productName);

        if (productName.equals("Missing product name")) {
            logExecutor.submit(() -> LOGGER.info("Product name is missing for product Id = "
                    + trade.getProductId())); //In case of millions of trades, do logging in separate thread will reduce response time
        }

        return trade;
    }
}
