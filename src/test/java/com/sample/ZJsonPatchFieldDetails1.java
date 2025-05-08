package com.sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;

public class ZJsonPatchFieldDetails1 {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        // Original JSON
        String originalJson = "{\"workOrderNumbers\": [\"5\", \"4\"]}";

        // Modified JSON
        String modifiedJson = "{\"workOrderNumbers\": [\"3\", \"1\", \"4\", \"5\"]}";

        JsonNode original = mapper.readTree(originalJson);
        JsonNode modified = mapper.readTree(modifiedJson);

        // Generate diff
        JsonNode diff = JsonDiff.asJson(original, modified, DiffFlags.dontNormalizeOpIntoMoveAndCopy());

        System.out.println("JSON Diff:");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(diff));

        for (JsonNode change : diff) {
            String op = change.get("op").asText();
            String path = change.get("path").asText();

            if (op.equals("add") || op.equals("replace")) {
                JsonNode value = change.get("value");
                System.out.printf("Change: %s at %s → New value: %s%n", op, path, value);
            } else if (op.equals("remove")) {
                System.out.printf("Removed field at %s%n", path);
            } else if (op.equals("move") || op.equals("copy")) {
                String from = change.get("from").asText();
                System.out.printf("Moved from %s to %s%n", from, path);
            }
        }
    }
}