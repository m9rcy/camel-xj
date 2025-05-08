package com.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import java.util.*;

public class JsonListDiffAccumulator {

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Original JSON
        String originalJson = "{\n" +
                              "    \"products\": [1, 2, 3, 4],\n" +
                              "    \"config\": {\n" +
                              "        \"features\": [\"auth\", \"logging\"]\n" +
                              "    }\n" +
                              "}\n";

        // Modified JSON
        String modifiedJson = "{\n" +
                              "    \"products\": [1, 3, 5],\n" +
                              "    \"config\": {\n" +
                              "        \"features\": [\"auth\", \"caching\", \"metrics\"]\n" +
                              "    }\n" +
                              "}\n";

        JsonNode original = mapper.readTree(originalJson);
        JsonNode modified = mapper.readTree(modifiedJson);
        JsonNode diff = JsonDiff.asJson(original, modified, DiffFlags.dontNormalizeOpIntoMoveAndCopy());

        Map<String, Map<String, List<String>>> changes = accumulateListChanges(diff);
        
        System.out.println(mapper.writerWithDefaultPrettyPrinter()
                              .writeValueAsString(changes));
    }

    public static Map<String, Map<String, List<String>>> accumulateListChanges(JsonNode diff) {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        
        // First pass: collect all removes and their values
        Map<String, Map<Integer, String>> removedValues = new HashMap<>();
        for (JsonNode change : diff) {
            if (change.get("op").asText().equals("remove") && 
                change.get("path").asText().matches(".*/\\d+$")) {
                
                String path = change.get("path").asText();
                String parentPath = path.substring(0, path.lastIndexOf('/'));
                int index = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                String value = change.get("value").toString();
                
                removedValues.computeIfAbsent(parentPath, k -> new HashMap<>())
                           .put(index, value);
            }
        }
        
        // Second pass: process adds and matches removes with adds
        for (JsonNode change : diff) {
            String op = change.get("op").asText();
            String path = change.get("path").asText();
            
            if (path.matches(".*/\\d+$")) { // Array change
                String parentPath = path.substring(0, path.lastIndexOf('/'));
                int index = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                
                if (op.equals("add")) {
                    String newValue = change.get("value").toString();
                    
                    // Check if this is a replacement (same index as a remove)
                    if (removedValues.containsKey(parentPath)) {
                        Map<Integer, String> removes = removedValues.get(parentPath);
                        if (removes.containsKey(index)) {
                            // This is a replace operation
                            result.computeIfAbsent(parentPath, k -> new HashMap<>())
                                 .computeIfAbsent("replaced", k -> new ArrayList<>())
                                 .add(removes.get(index) + " → " + newValue);
                            removes.remove(index);
                            continue;
                        }
                    }
                    
                    // Regular add
                    result.computeIfAbsent(parentPath, k -> new HashMap<>())
                         .computeIfAbsent("added", k -> new ArrayList<>())
                         .add(newValue);
                }
            }
        }
        
        // Add remaining removes (pure deletions)
        removedValues.forEach((parentPath, indexMap) -> {
            indexMap.values().forEach(value -> {
                result.computeIfAbsent(parentPath, k -> new HashMap<>())
                     .computeIfAbsent("removed", k -> new ArrayList<>())
                     .add(value);
            });
        });
        
        return result;
    }
}