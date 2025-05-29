package com.sample;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.time.OffsetDateTime;
import java.util.Objects;

public class DataModelComparatorProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        DataModel current = exchange.getIn().getBody(DataModel.class);
        DataModel previous = exchange.getProperty("previous", DataModel.class); // Assuming "previous" is in exchange property

        if (current == null) {
            throw new IllegalArgumentException("Current model cannot be null.");
        }

        DataModelDto dto = new DataModelDto();

        // Default values
        dto.setModifiedOn(current.getModifiedOn());
        dto.setName(current.getName());
        dto.setDescription(current.getDescription());
        dto.setActive(current.isActive());

        if (previous == null) {
            dto.setHasChange(true);
            dto.setChangeAction(DataModelDto.ChangeAction.UPDATE);
            exchange.getMessage().setBody(dto);
            return;
        }

        if (previous.getVersion() > current.getVersion()) {
            throw new IllegalArgumentException("Previous version is newer than current version.");
        }

        if (previous.getModifiedOn().isAfter(current.getModifiedOn())) {
            throw new IllegalArgumentException("Previous modifiedOn is after current modifiedOn.");
        }

        // Compare fields
        if (!Objects.equals(previous.getName(), current.getName())) {
            dto.setHasChange(true);
            dto.setChangeAction(DataModelDto.ChangeAction.RENAME);
        } else if (previous.isActive() != current.isActive()) {
            dto.setHasChange(true);
            dto.setChangeAction(current.isActive() ? 
                DataModelDto.ChangeAction.ACTIVATE : 
                DataModelDto.ChangeAction.DEACTIVATE);
        } else if (!Objects.equals(previous.getDescription(), current.getDescription())) {
            dto.setHasChange(true);
            dto.setChangeAction(DataModelDto.ChangeAction.UPDATE);
        } else {
            dto.setHasChange(false); // No meaningful change
        }

        exchange.getMessage().setBody(dto);
    }
}
