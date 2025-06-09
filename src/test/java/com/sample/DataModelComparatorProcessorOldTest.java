package com.sample;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DataModelComparatorProcessorOldTest {

    private DataModelComparatorProcessorOld processor;
    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        processor = new DataModelComparatorProcessorOld();
        now = OffsetDateTime.now();
    }

    @Test
    void testCurrentIsNull() {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        assertThrows(IllegalArgumentException.class, () -> processor.process(exchange));
    }

    @Test
    void testPreviousIsNull_shouldSetUpdateAction() throws Exception {
        DataModel current = DataModel.builder()
                .blockId(1L)
                .name("Item A")
                .version(1)
                .modifiedOn(now)
                .description("Initial")
                .active(true)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);

        processor.process(exchange);

        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);
        assertTrue(dto.isHasChange());
        assertEquals(DataModelDto.ChangeAction.UPDATE, dto.getChangeAction());
    }

    @Test
    void testPreviousNewerVersion_shouldThrow() {
        DataModel previous = DataModel.builder()
                .version(2)
                .modifiedOn(now.minusDays(1))
                .build();

        DataModel current = DataModel.builder()
                .version(1)
                .modifiedOn(now)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        assertThrows(IllegalArgumentException.class, () -> processor.process(exchange));
    }

    @Test
    void testPreviousModifiedOnAfterCurrent_shouldThrow() {
        DataModel previous = DataModel.builder()
                .version(1)
                .modifiedOn(now.plusDays(1))
                .build();

        DataModel current = DataModel.builder()
                .version(1)
                .modifiedOn(now)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        assertThrows(IllegalArgumentException.class, () -> processor.process(exchange));
    }

    @Test
    void testNameChanged_shouldSetRename() throws Exception {
        DataModel previous = DataModel.builder()
                .name("Old Name")
                .version(1)
                .modifiedOn(now.minusMinutes(1))
                .active(true)
                .description("Same")
                .build();

        DataModel current = DataModel.builder()
                .name("New Name")
                .version(2)
                .modifiedOn(now)
                .active(true)
                .description("Same")
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);

        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);
        assertTrue(dto.isHasChange());
        assertEquals(DataModelDto.ChangeAction.RENAME, dto.getChangeAction());
    }

    @Test
    void testActivated_shouldSetActivate() throws Exception {
        DataModel previous = DataModel.builder()
                .name("Name")
                .description("Desc")
                .version(1)
                .modifiedOn(now.minusMinutes(2))
                .active(false)
                .build();

        DataModel current = DataModel.builder()
                .name("Name")
                .description("Desc")
                .version(2)
                .modifiedOn(now)
                .active(true)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);

        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);
        assertTrue(dto.isHasChange());
        assertEquals(DataModelDto.ChangeAction.ACTIVATE, dto.getChangeAction());
    }

    @Test
    void testDeactivated_shouldSetDeactivate() throws Exception {
        DataModel previous = DataModel.builder()
                .name("Name")
                .description("Desc")
                .version(1)
                .modifiedOn(now.minusMinutes(2))
                .active(true)
                .build();

        DataModel current = DataModel.builder()
                .name("Name")
                .description("Desc")
                .version(2)
                .modifiedOn(now)
                .active(false)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);

        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);
        assertTrue(dto.isHasChange());
        assertEquals(DataModelDto.ChangeAction.DEACTIVATE, dto.getChangeAction());
    }

    @Test
    void testDescriptionChanged_shouldSetUpdate() throws Exception {
        DataModel previous = DataModel.builder()
                .name("Same")
                .description("Old")
                .version(1)
                .modifiedOn(now.minusMinutes(2))
                .active(true)
                .build();

        DataModel current = DataModel.builder()
                .name("Same")
                .description("New")
                .version(2)
                .modifiedOn(now)
                .active(true)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);

        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);
        assertTrue(dto.isHasChange());
        assertEquals(DataModelDto.ChangeAction.UPDATE, dto.getChangeAction());
    }

    @Test
    void testNoChange_shouldSetHasChangeFalse() throws Exception {
        DataModel previous = DataModel.builder()
                .name("Same")
                .description("Same")
                .version(1)
                .modifiedOn(now.minusMinutes(2))
                .active(true)
                .build();

        DataModel current = DataModel.builder()
                .name("Same")
                .description("Same")
                .version(2)
                .modifiedOn(now)
                .active(true)
                .build();

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);

        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);
        assertFalse(dto.isHasChange());
        assertNull(dto.getChangeAction());
    }
}
