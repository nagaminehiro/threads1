package importer;

import database.Database;
import model.Client;
import model.Products;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelDataImporter {
    private final Database database;

    public ParallelDataImporter(Database database) {
        this.database = database;
    }

    public void importData(String filePath, int numThreads) throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try (InputStream fis = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(fis)) {
            JsonObject jsonObject = reader.readObject();
            JsonArray productsArray = jsonObject.getJsonArray("products");
            JsonArray clientsArray = jsonObject.getJsonArray("clients");

            for (JsonObject productObj : productsArray.getValuesAs(JsonObject.class)) {
                executor.submit(() -> {
                    try {
                        Products product = new Products(productObj.getString("name"), productObj.getString("description"));
                        database.addProducts(product);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            for (JsonObject clientObj : clientsArray.getValuesAs(JsonObject.class)) {
                executor.submit(() -> {
                    try {
                        Client client = new Client(clientObj.getString("name"), clientObj.getInt("age"));
                        database.addClients(client);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
        }
    }
}