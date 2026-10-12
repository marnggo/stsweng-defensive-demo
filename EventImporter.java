import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventImporter {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public static List<Event> importEvents(String filename)
            throws IOException {

        List<Event> events = new ArrayList<>();

        int imported = 0, rejected = 0, lineNo = 0;

        List<String> lines = Files.readAllLines(Path.of(filename));

        for (String line : lines) {

            String[] values = line.split(",");

            lineNo++;
            String reason = null;
            
            if (values.length != 3) {
                reason = "expected 3 values but found " + values.length;
            } else if (values[1].isBlank()) {
                reason = "blank title";
            } else if (!values[2].trim().matches("red|green|blue")) {
                reason = "invalid color '" + values[2].trim() + "'";
            } else {
                try {
                    String d = values[0].trim();
                    if (!LocalDate.parse(d, FORMAT).format(FORMAT).equals(d)) {
                        reason = "invalid date '" + d + "'";
                    }
                } catch (java.time.format.DateTimeParseException e) {
                    reason = "missing or invalid date '" + values[0].trim() + "'";
                }
            }

            if (reason != null) {
                System.out.println("Rejected line " + lineNo + ": " + reason + " -> " + line);
                rejected++;
                continue;
            }

            LocalDate date =
                    LocalDate.parse(values[0].trim(), FORMAT);

            String title = values[1].trim();
            String color = values[2].trim();

            events.add(
                    new Event(date, title, color)
            );
            imported++;
        }

        System.out.println("Imported: " + imported + ", Rejected: " + rejected);

        return events;
    }
}