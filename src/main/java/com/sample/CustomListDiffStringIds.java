package com.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.*;

public class CustomListDiffStringIds {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Input JSONs with string IDs
        String json1 = "{\"ids\":[\"id1\",\"id2\",\"id3\",\"id4\"]}";
        String json2 = "{\"ids\":[\"id1\",\"id3\",\"id5\",\"id6\"]}";
        
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);
        
        JsonNode list1 = node1.get("ids");
        JsonNode list2 = node2.get("ids");
        
        // Convert to Java collections for easier comparison
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();
        
        list1.forEach(item -> set1.add(item.asText()));
        list2.forEach(item -> set2.add(item.asText()));
        
        // Find added items
        Set<String> added = new HashSet<>(set2);
        added.removeAll(set1);
        
        // Find removed items
        Set<String> removed = new HashSet<>(set1);
        removed.removeAll(set2);
        
        // Create result JSON
        ObjectNode result = mapper.createObjectNode();
        result.putArray("added").addAll(mapper.valueToTree(added));
        result.putArray("removed").addAll(mapper.valueToTree(removed));
        
        // Print the JSON result
        System.out.println(mapper.writerWithDefaultPrettyPrinter()
                              .writeValueAsString(result));
    }
}