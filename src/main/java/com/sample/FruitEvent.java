package com.sample;

import java.util.List;
import java.util.Map;

public class FruitEvent {
    public String id;
    public String rarity;
    public Map<String, Object> others;
    public List<String> items;

    public FruitEvent() {}  // Default constructor

    public FruitEvent(String id, String rarity, Map<String, Object> others, List<String> items) {
        this.id = id;
        this.rarity = rarity;
        this.others = others;
        this.items = items;
    }

    @Override
    public String toString() {
        return "FruitEvent{" +
                "id='" + id + '\'' +
                ", rarity='" + rarity + '\'' +
                ", others=" + others +
                ", items=" + items +
                '}';
    }
}
