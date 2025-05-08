package com.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class CustomListDiff {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        String json1 = "{\"ids\":[1,2,3,4]}";
        String json2 = "{\"ids\":[1,3,5,6]}";
        
        JsonNode node1 = mapper.readTree(json1);
        JsonNode node2 = mapper.readTree(json2);
        
        JsonNode list1 = node1.get("ids");
        JsonNode list2 = node2.get("ids");
        
        // Convert to Java collections for easier comparison
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        
        list1.forEach(item -> set1.add(item.asInt()));
        list2.forEach(item -> set2.add(item.asInt()));
        
        // Find added items
        Set<Integer> added = new HashSet<>(set2);
        added.removeAll(set1);
        
        // Find removed items
        Set<Integer> removed = new HashSet<>(set1);
        removed.removeAll(set2);
        
        System.out.println("Added items: " + added);    // [5, 6]
        System.out.println("Removed items: " + removed);// [2, 4]
    }
}