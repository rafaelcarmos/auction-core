package test.util;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkResults {

    private final Path resultsDirectory = Paths.get("F:/AuctionCoreResults/");
    private List<List<String>> columns;
    private int maxLines = 0;

    public BenchmarkResults() throws Exception {

        this.columns = new ArrayList<>();

        if (Files.notExists(resultsDirectory))
            Files.createDirectory(resultsDirectory);

    }

    public void addColumn(List<String> column) {

        this.columns.add(column);

        if (column.size() > maxLines) {
            maxLines = column.size();
        }
    }


    public void ExportToCSV(String name) throws Exception {

        StringBuilder csv = new StringBuilder();

        for (int line = 0; line < maxLines; line++) {

            for (List<String> col : columns) {
                csv.append(col.get(line)).append(';');
            }

            csv.append('\n');
        }

        FileWriter writer = new FileWriter("Results_" + name + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        writer.write(csv.toString());
        writer.close();
    }
}
