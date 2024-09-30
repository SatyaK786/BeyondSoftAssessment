import java.util.ArrayList;
import java.util.HashMap;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONToCSVMapper {

    public static void main(String[] args) {
        try {
            String inputJSON = "{ \"rows\": [ { \"headers\": [ \"AC_Steam Produced\", \"No Operation Type\", \"MT_Boiler Systems\", \"EN_PALM1\", \"No Customer\", \"FY22\", \"FD_Actuals\", \"OEP_Working\", \"OFS_Direct Input\" ], \"data\": [ \"43.77\", \"12.119999999999\", \"134.81\" ] }, { \"headers\": [ \"AC_Steam Produced\", \"No Operation Type\", \"MT_Boiler Systems\", \"EN_PALM2\", \"No Customer\", \"FY22\", \"FD_Actuals\", \"OEP_Working\", \"OFS_Direct Input\" ], \"data\": [ \"18.76806640625\", \"17.80078125\", \"14.87060546875\" ] }, { \"headers\": [ \"AC_Steam Produced\", \"No Operation Type\", \"MT_Total Boilers\", \"EN_PALM1\", \"No Customer\", \"FY22\", \"FD_Actuals\", \"OEP_Working\", \"OFS_Direct Input\" ], \"data\": [ \"15.77\", \"12.119999999999\", \"14.81\" ] }, { \"headers\": [ \"AC_Steam Produced\", \"No Operation Type\", \"MT_Total Boilers\", \"EN_PALM2\", \"No Customer\", \"FY22\", \"FD_Actuals\", \"OEP_Working\", \"OFS_Direct Input\" ], \"data\": [ \"133.76806640625\", \"140.80078125\", \"174.87060546875\" ] } ], \"pov\": [ \"USD\" ], \"columns\": [ [ \"Jan_01\", \"Jan_02\", \"Jan_03\" ] ] }";

            // Input values for row and pov dimensions
            String[] rowDimensions = { "Account", "Operation Type", "Material Type", "Entity", "Customer", "Years", "Scenario", "Version", "Plan Element" };
            String[] povDimensions = { "Currency" };

            // Create ObjectMapper instance for Jackson parsing
            ObjectMapper mapper = new ObjectMapper();

            // Parse the input JSON into a HashMap
            HashMap<String, Object> jsonResponse = mapper.readValue(inputJSON, new TypeReference<HashMap<String, Object>>() {});

            // Extract rows, pov, and columns
            ArrayList<HashMap<String, Object>> rows = (ArrayList<HashMap<String, Object>>) jsonResponse.get("rows");
            ArrayList<String> pov = jsonResponse.containsKey("pov") ? (ArrayList<String>) jsonResponse.get("pov") : new ArrayList<>();
            ArrayList<ArrayList<String>> columns = jsonResponse.containsKey("columns") ? (ArrayList<ArrayList<String>>) jsonResponse.get("columns") : new ArrayList<>();

            // Build dynamic CSV headers based on the input dimensions and JSON structure
            StringBuilder csvOutput = new StringBuilder();
            appendCSVHeader(csvOutput, rowDimensions, povDimensions, columns);

            // Process each row and dynamically construct the CSV row
            for (HashMap<String, Object> row : rows) {
                buildCSVRow(csvOutput, row, rowDimensions.length, pov, columns);
            }

            // Print the CSV output
            System.out.println(csvOutput.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to build the CSV header dynamically
    private static void appendCSVHeader(StringBuilder csvOutput, String[] rowDimensions, String[] povDimensions, ArrayList<ArrayList<String>> columns) {
        for (String rowDim : rowDimensions) {
            csvOutput.append("\"").append(rowDim).append("\"");
        }
        for (String povDim : povDimensions) {
            csvOutput.append("\"").append(povDim).append("\"");
        }

        // Add column headers if available
        if (!columns.isEmpty()) {
            for (String columnHeader : columns.get(0)) {
                csvOutput.append("\"").append(columnHeader).append("\"");
            }
        }
        csvOutput.append("\n");  // End the header row
    }

    // Method to build a CSV row based on the JSON row structure
    private static void buildCSVRow(StringBuilder csvOutput, HashMap<String, Object> row, int rowDimCount, ArrayList<String> pov, ArrayList<ArrayList<String>> columns) {
        // Extract headers and data from each row
        ArrayList<String> headers = (ArrayList<String>) row.get("headers");
        ArrayList<String> data = (ArrayList<String>) row.get("data");

        // Append headers (row dimensions)
        for (int i = 0; i < rowDimCount; i++) {
            csvOutput.append("\"").append(headers.size() > i ? headers.get(i) : "").append("\"");
        }

        // Append POV (filter dimensions)
        for (String povValue : pov) {
            csvOutput.append("\"").append(povValue).append("\"");
        }

        // Append data (column values)
        for (String dataValue : data) {
            csvOutput.append("\"").append(dataValue).append("\"");
        }
        csvOutput.append("\n");  // End the data row
    }
}
