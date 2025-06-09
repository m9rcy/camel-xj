package com.sample;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Objects;

public class DataModelComparatorProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        DataModel current = exchange.getIn().getBody(DataModel.class);
        DataModel previous = exchange.getProperty("previous", DataModel.class);

        if (current == null) {
            throw new IllegalArgumentException("Current model cannot be null.");
        }

        DataModelDto dto = new DataModelDto();
        dto.setModifiedOn(current.getModifiedOn());
        dto.setName(current.getName());
        dto.setDescription(current.getDescription());
        dto.setActive(current.isActive());

        boolean hasChange = false;
        boolean nameChanged = false;

        if (previous == null) {
            hasChange = true; // New object, treat as an update
        } else {
            if (previous.getVersion() > current.getVersion()) {
                throw new IllegalArgumentException("Previous version is newer than current version.");
            }

            if (previous.getModifiedOn().isAfter(current.getModifiedOn())) {
                throw new IllegalArgumentException("Previous modifiedOn is after current modifiedOn.");
            }

            nameChanged = !Objects.equals(previous.getName(), current.getName());
            boolean statusChanged = previous.isActive() != current.isActive();
            boolean descriptionChanged = !Objects.equals(previous.getDescription(), current.getDescription());

            hasChange = nameChanged || statusChanged || descriptionChanged;

            if (nameChanged) {
                dto.setOldName(previous.getName());
            }
        }

        dto.setHasChange(hasChange);
        dto.setNameChanged(nameChanged);

        exchange.getMessage().setBody(dto);
    }
}
