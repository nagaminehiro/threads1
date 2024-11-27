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

public class DataImporter {
    private final Database database;

    public DataImporter(Database database) {
        this.database = database;
    }

    public void importData(String filePath) throws IOException, SQLException {
        try (InputStream fis = new FileInputStream(filePath);
             JsonReader reader = Json.createReader(fis)) {
            JsonObject jsonObject = reader.readObject();
            JsonArray productsArray = jsonObject.getJsonArray("products");
            JsonArray clientsArray = jsonObject.getJsonArray("clients");

            for (JsonObject productObj : productsArray.getValuesAs(JsonObject.class)) {
                Products product = new Products(productObj.getString("name"), productObj.getString("description"));
                database.addProducts(product);
            }

            for (JsonObject clientObj : clientsArray.getValuesAs(JsonObject.class)) {
                Client client = new Client(clientObj.getString("name"), clientObj.getInt("age"));
                database.addClients(client);
            }
        }
    }
}