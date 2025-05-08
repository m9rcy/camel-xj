package com.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkOrderComparator1 {
    
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
    
    public static ComparisonResult compareWorkOrders(List<String> current, List<String> previous) {
        List<String> added = new ArrayList<>();
        List<String> deleted = new ArrayList<>();
        
        // Find added items (in current but not in previous)
        for (String item : current) {
            if (!previous.contains(item)) {
                added.add(item);
            }
        }
        
        // Find deleted items (in previous but not in current)
        for (String item : previous) {
            if (!current.contains(item)) {
                deleted.add(item);
            }
        }
        
        return new ComparisonResult(added, deleted);
    }
    
    public static void main(String[] args) {
        // Test cases
        testComparison(Arrays.asList("5", "4"), Arrays.asList("3", "1", "4", "5"));
        testComparison(Arrays.asList(), Arrays.asList("3", "1", "4", "5"));
        testComparison(Arrays.asList("3", "1", "4", "5", "6"), Arrays.asList("3", "1", "4", "5"));
        testComparison(Arrays.asList("3", "1"), Arrays.asList());
    }
    
    private static void testComparison(List<String> current, List<String> previous) {
        System.out.println("Current: " + current);
        System.out.println("Previous: " + previous);
        ComparisonResult result = compareWorkOrders(current, previous);
        System.out.println(result);
        System.out.println("-------------------");
    }
}