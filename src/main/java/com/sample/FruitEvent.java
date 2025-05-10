package com.sample;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wildfly.common.annotation.NotNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Builder
@Data
@JsonDeserialize(builder = FruitEvent.FruitEventBuilder.class)
public class FruitEvent {

    //@NotEmpty
    @Size(min = 20)
    public String id;

    @NotEmpty
    public String rarity;

    @NotNull
    public String phone;

    @NotNull
    public OffsetDateTime start;

    @NotNull
    public OffsetDateTime end;
    public Map<String, Object> others;

    public List<String> items;

    @JsonPOJOBuilder(withPrefix = "")
    public static class FruitEventBuilder {
    }

}
