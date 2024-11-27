import database.Database;
import importer.DataImporter;
import importer.ParallelDataImporter;

public class Main {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:postgresql://localhost:5432/threads1";
        String user = "postgres";
        String password = "L12y25n23,";

        try (Database database = new Database(url, user, password)) {
            DataImporter dataImporter = new DataImporter(database);

            long startTime = System.currentTimeMillis();
            dataImporter.importData("data.json");
            long endTime = System.currentTimeMillis();
            System.out.println("Tempo de execução serial: " + (endTime - startTime) + " ms");
        }

        int[] threadCounts = {1, 2, 4, 6, 8, 12};
        for (int numThreads : threadCounts) {
                ParallelDataImporter dataImporter = new ParallelDataImporter(url, user, password, numThreads);

                long startTime = System.currentTimeMillis();
                System.out.println("Iniciando a importação paralela com " + numThreads + " threads...");
                dataImporter.importData("data.json");
                long endTime = System.currentTimeMillis();
                System.out.println(
                        "Tempo de execução paralelo com " + numThreads + " threads: " + (endTime - startTime) + " ms");
                dataImporter.closeConnections();
        }
    }
}