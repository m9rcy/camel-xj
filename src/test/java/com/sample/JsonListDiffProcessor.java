package com.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import java.util.*;

public class JsonListDiffProcessor {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Original JSON with list
        String originalJson = "{\n" +
                              "    \"users\": [\"alice\", \"bob\", \"charlie\"],\n" +
                              "    \"products\": [1, 2, 3, 4],\n" +
                              "    \"config\": {\n" +
                              "        \"features\": [\"auth\", \"logging\"]\n" +
                              "    }\n" +
                              "}\n";

        // Modified JSON with list changes
        String modifiedJson = "{\n" +
                              "    \"users\": [\"alice\", \"dave\", \"charlie\"],\n" +
                              "    \"products\": [1, 3, 5],\n" +
                              "    \"config\": {\n" +
                              "        \"features\": [\"auth\", \"caching\", \"metrics\"]\n" +
                              "    }\n" +
                              "}\n";

        // Parse JSON
        JsonNode original = mapper.readTree(originalJson);
        JsonNode modified = mapper.readTree(modifiedJson);

        // Get the diff
        JsonNode diff = JsonDiff.asJson(original, modified, DiffFlags.dontNormalizeOpIntoMoveAndCopy());

        // Process the diff into a structured map
        Map<String, Map<String, List<String>>> changes = processListChanges(diff);

        // Print the result
        System.out.println(mapper.writerWithDefaultPrettyPrinter()
                               .writeValueAsString(changes));
    }

    public static Map<String, Map<String, List<String>>> processListChanges(JsonNode diff) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        
        for (JsonNode change : diff) {
            String op = change.get("op").asText();
            String path = change.get("path").asText();

            // We only care about array changes (add/remove)
            if (path.matches(".*/\\d+$")) { // Path ends with /number (array index)
                String parentPath = path.substring(0, path.lastIndexOf('/'));
                
                if (op.equals("add")) {
                    String addedValue = change.get("value").toString();
                    result.computeIfAbsent(parentPath, k -> new HashMap<>())
                          .computeIfAbsent("added", k -> new ArrayList<>())
                          .add(addedValue);
                } 
                else if (op.equals("remove")) {
                    String removedValue = change.get("value").toString();
                    result.computeIfAbsent(parentPath, k -> new HashMap<>())
                          .computeIfAbsent("removed", k -> new ArrayList<>())
                          .add(removedValue);
                }
            }
        }
        
        return result;
    }
}