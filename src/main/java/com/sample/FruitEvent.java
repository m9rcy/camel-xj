package com.sample;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public class FruitEvent {

    @Size(min = 20)
    public String id;

    @NotEmpty
    public String rarity;
    public Map<String, Object> others;

    @Size(min = 1)
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
