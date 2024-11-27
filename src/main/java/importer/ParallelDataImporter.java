package importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import database.Database;
import model.Client;
import model.Products;

public class ParallelDataImporter {

    private final int numThreads;
    private final Queue<Database> databases;

    public ParallelDataImporter(String url, String user, String password, int numThreads) throws SQLException {
        this.numThreads = numThreads;
        this.databases = new ArrayBlockingQueue<>(numThreads);

        for (int i = 0; i < numThreads; i++) {
            databases.add(new Database(url, user, password));
        }

        databases.peek().truncateTables();
    }

    public void importData(String filePath) {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        try (InputStream fis = new FileInputStream(filePath); JsonReader reader = Json.createReader(fis)) {
            JsonObject jsonObject = reader.readObject();
            JsonArray productsArray = jsonObject.getJsonArray("products");
            JsonArray clientsArray = jsonObject.getJsonArray("clients");

            productsArray.getValuesAs(JsonObject.class) //
                    .forEach(productObj -> executor.execute(importProduct(productObj)));

            clientsArray.getValuesAs(JsonObject.class) //
                    .forEach(clientObj -> executor.execute(importClient(clientObj)));

        } catch (IOException e) {
            executor.shutdown();
            e.printStackTrace();
        }

        try {
            System.out.println("Aguardando a finalização das threads...");
            executor.shutdown();
            var resultadoTermination = executor.awaitTermination(5, TimeUnit.MINUTES);
            if (!resultadoTermination) {
                System.out.println("Timeout ao aguardar a finalização das threads");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Runnable importProduct(JsonObject productObj) {
        return () -> {
            try {
                Database database = databases.poll();
                Products product = new Products(productObj.getString("name"), productObj.getString("description"));
                database.addProducts(product);
                databases.add(database);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    private Runnable importClient(JsonObject clientObj) {
        return () -> {
            try {
                Database database = databases.poll();
                Client client = new Client(clientObj.getString("name"), clientObj.getInt("age"));
                database.addClients(client);
                databases.add(database);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    public void closeConnections() {
        databases.forEach(database -> {
            try {
                database.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}