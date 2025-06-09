package com.sample;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.OffsetDateTime;

@Data
@Builder
@Jacksonized
public class DataModel {

    Long blockId;
    String name;

    Integer version;
    OffsetDateTime modifiedOn;
    String description;
    boolean active;
}
