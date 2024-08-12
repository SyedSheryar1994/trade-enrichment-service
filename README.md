# Trade Enrichment Service

## Overview

This project is designed to process and enrich trade data by associating each trade with a corresponding product based on a product ID. The service has been optimized to handle millions of trades efficiently, ensuring high performance and scalability.

## How to Run the Service

1. **Build the Project:**
   Ensure you have Maven and JDK 17 installed.
   ```bash
   mvn clean install
   ```

2. **Run the Application:**
   ```bash
   java -jar target/trade-enrichment-service.jar
   ```

3. **Configure Application Properties:**

   The application uses `application.properties` for configuration. Key properties include:

   ```properties
   product.csv.filepath=product.csv
   trade.batch.size=1000
   ```

   Adjust these properties as needed to fit specific environment.

## How to Use the API

The API provides an endpoint to enrich trade data. The endpoint expects a CSV file containing trade data, and it returns an enriched CSV file.

### Endpoint

```
POST /api/v1/enrich
```

### Request

- **Content-Type:** `text/csv`
- **Body:** CSV file containing trade data

### Response

- **Content-Type:** `text/csv`
- **Body:** CSV file containing enriched trade data

### Sample cURL Command
- Make sure to run this curl command from project directory so that relative path resolves correctly
```bash
curl --data-binary "@src/test/resources/trade.csv" -H 'Content-Type: text/csv' http://localhost:8080/api/v1/enrich
```

## Limitations of the Code

1. **Concurrency:**
   - The application uses `ConcurrentHashMap` for caching product data and `ConcurrentLinkedQueue` for batch records collection and processing trades in parallel. While this improves performance, it could still be impacted by the size of the data and the available CPU resources.

2. **No External Libraries:**
   - The implementation does not use any external libraries for performance optimization, which could limit the ability to further optimize the code.

## Discussion/Comment on the Design

### Design Decisions

1. **Removal of `Product Model Class`:**
   - To reduce object creation overhead, the `Product` class was removed. Instead, a `ConcurrentHashMap` with `Integer` keys (Product IDs) and `String` values (Product Names) was used for caching.

2. **Batch Processing:**
   - Trades are processed in batches to balance between memory usage and processing overhead. The batch size is configurable to allow tuning based on available resources.

3. **Property Management:**
   - The service is highly configurable through properties, which can be overridden during tests to simulate different environments.

4. **Logging:**
   - Missing product IDs and data with Invalid date formats are logged for debugging and tracking purposes. Logging has been optimized to avoid impacting performance.

### Potential Improvements

1. **Async Processing:**
   - Implementing asynchronous processing might improve responsiveness, but it was not implemented to keep the design simple.

2. **Third-Party Libraries:**
   - Using third-party libraries like `Netty` or `Guava` could significantly improve performance, but this was avoided as per project requirements.

3. **Refactoring:**
   - Further refactoring could be done to reduce the complexity of batch processing and improve the clarity of the codebase.

## Ideas for Improvement

1. **Use of a Dedicated Cache Layer:**
   - Implement a dedicated caching layer (e.g., Redis) to improve the performance of product lookups, especially for large datasets.

2. **Database Integration:**
   - Store product data in a database and use a lightweight ORM or direct SQL queries to fetch and cache data as needed.

3. **Load Testing:**
   - Perform extensive load testing to identify bottlenecks and optimize the code further.

4. **Improved Logging:**
   - Move logging to an asynchronous process to ensure it doesn’t impact the main processing thread.


