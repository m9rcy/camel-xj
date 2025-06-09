package com.sample;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class DataModelDto {
    private String name;
    private String oldName;
    private String description;
    private boolean active;
    private OffsetDateTime modifiedOn;
    private boolean hasChange;
    private boolean nameChanged;

    private ChangeAction changeAction;

    public enum ChangeAction {
        UPDATE,
        ACTIVATE,
        DEACTIVATE,
        RENAME
    }
}
