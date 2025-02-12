package JSONToSQL;

public class SimpleJsonToSqlConverter {

    /**
     * Converts a JSON query (in a fixed, simple format) to a SQL query.
     * Assumptions:
     * - The JSON is flat and keys appear only once.
     * - Values are either strings (delimited by double quotes) or numbers.
     * - Supported keys: select, from, where, orderBy, limit, offset.
     */
    public static String convert(String json) {
        // Remove whitespace for easier parsing.
        String s = json.replaceAll("\\s+", "");
        
        // Build SQL query.
        StringBuilder sql = new StringBuilder();
        
        // SELECT clause (default "*")
        String select = extractValue(s, "select");
        sql.append("SELECT ");
        sql.append(select.isEmpty() ? "*" : select);
        
        // FROM clause (required)
        String from = extractValue(s, "from");
        if (from.isEmpty()) {
            throw new RuntimeException("FROM clause is required");
        }
        sql.append(" FROM ").append(from);
        
        // WHERE clause (optional)
        String where = extractValue(s, "where");
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(where);
        }
        
        // ORDER BY clause (optional)
        String orderBy = extractValue(s, "orderBy");
        if (!orderBy.isEmpty()) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        
        // LIMIT clause (optional)
        String limit = extractValue(s, "limit");
        if (!limit.isEmpty()) {
            sql.append(" LIMIT ").append(limit);
        }
        
        // OFFSET clause (optional)
        String offset = extractValue(s, "offset");
        if (!offset.isEmpty()) {
            sql.append(" OFFSET ").append(offset);
        }
        
        return sql.toString();
    }
    
    /**
     * Extracts the value for a given key from the simple JSON string.
     * This method assumes the JSON key is in the form: "key":value
     * where value is either a quoted string or a number.
     */
    private static String extractValue(String s, String key) {
        String pattern = "\"" + key + "\":";
        int index = s.indexOf(pattern);
        if (index == -1) {
            return "";
        }
        index += pattern.length();
        char firstChar = s.charAt(index);
        String value = "";
        if (firstChar == '\"') {  // string value
            index++;  // skip opening quote
            int end = s.indexOf('\"', index);
            value = s.substring(index, end);
        } else {  // numeric or unquoted value; end at comma or closing brace
            int endComma = s.indexOf(',', index);
            int endBrace = s.indexOf('}', index);
            int end = (endComma == -1) ? endBrace : Math.min(endComma, endBrace);
            value = s.substring(index, end);
        }
        return value;
    }
    
    // --- Demo ---
    public static void main(String[] args) {
        String jsonQuery = "{\n" +
                "  \"select\": \"col1,col2\",\n" +
                "  \"from\": \"myTable\",\n" +
                "  \"where\": \"col1='value1'\",\n" +
                "  \"orderBy\": \"col1 asc,col2 desc\",\n" +
                "  \"limit\": 100,\n" +
                "  \"offset\": 5\n" +
                "}";
        try {
            String sql = convert(jsonQuery);
            System.out.println("Generated SQL Query:\n" + sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
