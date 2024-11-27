import database.Database;
import importer.DataImporter;
import importer.ParallelDataImporter;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        String url = "jdbc:postgresql://localhost:5432/threads1";
        String user = "postgres";
        String password = "L12y25n23,";

        Database database = new Database(url, user, password);
        DataImporter dataImporter = new DataImporter(database);
        ParallelDataImporter parallelDataImporter = new ParallelDataImporter(database);

        long startTime = System.currentTimeMillis();
        dataImporter.importData("data.json");
        long endTime = System.currentTimeMillis();
        System.out.println("Tempo de execução serial: " + (endTime - startTime) + " ms");

        int[] threadCounts = {1, 2, 4, 6, 8, 12};
        for (int numThreads : threadCounts) {
            database = new Database(url, user, password); // Reset database for each run
            parallelDataImporter = new ParallelDataImporter(database);

            startTime = System.currentTimeMillis();
            parallelDataImporter.importData("data.json", numThreads);
            endTime = System.currentTimeMillis();
            System.out.println("Tempo de execução paralelo com " + numThreads + " threads: " + (endTime - startTime) + " ms");
        }

        database.close();
    }
}