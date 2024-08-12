package com.verygoodbank.tes.web.util;

import com.verygoodbank.tes.web.exception.BusinessException;
import com.verygoodbank.tes.web.model.Trade;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Utility class for parsing CSV data.
 */
public class CsvUtils {
    /**
     * Parses a byte array representing a trade CSV file and converts it into a list of Trade objects.
     *
     * @param fileData the byte array containing the trade CSV data.
     * @return a list of Trade objects parsed from the CSV data.
     */
    public static List<Trade> parseTrades(byte[] fileData) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(fileData), StandardCharsets.UTF_8))) {

            return reader.lines()
                    .parallel()
                    .skip(1) // Skip header line
                    .map(CsvUtils::mapToTrade)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new BusinessException("Failed to parse trade CSV file");
        }
    }

    /**
     * Parses a line of CSV data and converts it into a Trade object.
     *
     * @param line the line of CSV data representing a trade.
     * @return a Trade object representing the CSV data.
     */
    private static Trade mapToTrade(String line) {
        String[] fields = line.split(",");
        return new Trade(
                Integer.parseInt(fields[1]), // productId
                fields[0], // date
                fields[2], // currency
                new BigDecimal(fields[3]), // price
                null // productName will be added during enrichment
        );
    }

    /**
     * Parses a product CSV file located at the given file path and converts it into a productCache of Product objects.
     *
     * @param filePath the file path to the product CSV file.
     * @return a productCache of Product objects keyed by their productId.
     */
    public static void parseProductsAndPopulate(String filePath, Map<Integer, String> productCache) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(filePath).getInputStream(), StandardCharsets.UTF_8))) {

            reader.lines()
                    .parallel()
                    .skip(1) // Skip header line
                    .map(CsvUtils::mapToProduct)
                    .forEach(entry -> productCache.put(entry.getKey(), entry.getValue()));

        } catch (Exception e) {
            throw new BusinessException("Failed to parse product CSV file");
        }
    }

    /**
     * Parses a line of CSV data and converts it into a Product object.
     *
     * @param line the line of CSV data representing a product.
     * @return a Product object representing the CSV data.
     */
    private static Map.Entry<Integer, String> mapToProduct(String line) {
        String[] fields = line.split(",");
        return new AbstractMap.SimpleEntry<>(
                Integer.parseInt(fields[0]), // productId
                fields[1] // productName
        );
    }

    /**
     * Converts a list of Trade objects into a CSV-formatted string.
     *
     * @param trades the list of Trade objects to be converted.
     * @return a CSV-formatted string representing the list of Trade objects.
     */
    public static String convertToCsv(Queue<Trade> trades) {
        return "date,product_name,currency,price\n" +
                trades.stream()
                        .map(trade -> String.join(",", trade.getDate(), trade.getProductName(),
                                trade.getCurrency(), trade.getPrice().toString()))
                        .collect(Collectors.joining("\n"));
    }
}
