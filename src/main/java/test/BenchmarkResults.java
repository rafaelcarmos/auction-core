package test;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BenchmarkResults {


    public static void ExportToCSV(String name, String csv) throws Exception {
        FileWriter writer = new FileWriter("Results_" + name + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
        writer.write(csv);
        writer.close();
    }

}
