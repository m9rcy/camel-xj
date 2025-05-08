package com.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderComparator {

    public static class ComparisonResult {
        private List<String> added;
        private List<String> deleted;

        public ComparisonResult(List<String> added, List<String> deleted) {
            this.added = added;
            this.deleted = deleted;
        }

        public List<String> getAdded() {
            return added;
        }

        public List<String> getDeleted() {
            return deleted;
        }

        @Override
        public String toString() {
            return "Added: " + added + "\nDeleted: " + deleted;
        }
    }

    public static ComparisonResult compareWorkOrders(JsonNode current, JsonNode previous) {
        List<String> currentList = extractStringList(current);
        List<String> previousList = extractStringList(previous);

        List<String> added = new ArrayList<>();
        List<String> deleted = new ArrayList<>();

        // Find added items (in current but not in previous)
        for (String item : currentList) {
            if (!previousList.contains(item)) {
                added.add(item);
            }
        }

        // Find deleted items (in previous but not in current)
        for (String item : previousList) {
            if (!currentList.contains(item)) {
                deleted.add(item);
            }
        }

        return new ComparisonResult(added, deleted);
    }

    private static List<String> extractStringList(JsonNode node) {
        List<String> result = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode element : node) {
                if (element.isTextual()) {
                    result.add(element.asText());
                }
            }
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Test cases
        testComparison(mapper.readTree("[\"5\", \"4\"]"), mapper.readTree("[\"3\", \"1\", \"4\", \"5\"]"));
        testComparison(mapper.readTree("[]"), mapper.readTree("[\"3\", \"1\", \"4\", \"5\"]"));
        testComparison(mapper.readTree("[\"3\", \"1\", \"4\", \"5\", \"6\"]"), mapper.readTree("[\"3\", \"1\", \"4\", \"5\"]"));
        testComparison(mapper.readTree("[\"3\", \"1\"]"), mapper.readTree("[]"));
    }

    private static void testComparison(JsonNode current, JsonNode previous) {
        System.out.println("Current: " + current);
        System.out.println("Previous: " + previous);
        ComparisonResult result = compareWorkOrders(current, previous);
        System.out.println(result);
        System.out.println("-------------------");
    }
}