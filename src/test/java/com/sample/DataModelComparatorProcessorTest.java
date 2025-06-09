package com.sample;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DataModelComparatorProcessorTest {

    private DataModelComparatorProcessor processor;
    private DefaultCamelContext camelContext;

    @BeforeEach
    void setup() {
        processor = new DataModelComparatorProcessor();
        camelContext = new DefaultCamelContext();
    }

    private DataModel createModel(Long blockId, String name, String description, boolean active, int version, OffsetDateTime modifiedOn) {
        return DataModel.builder()
                .blockId(blockId)
                .name(name)
                .description(description)
                .active(active)
                .version(version)
                .modifiedOn(modifiedOn)
                .build();
    }

    @Test
    void testNullPrevious_setsHasChangeTrue() throws Exception {
        Exchange exchange = new DefaultExchange(camelContext);
        DataModel current = createModel(1L, "A", "test", true, 1, OffsetDateTime.now());

        exchange.getIn().setBody(current);
        exchange.setProperty("previous", null);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertFalse(dto.isNameChanged());
        assertEquals("A", dto.getName());
    }

    @Test
    void testNoChange_setsHasChangeFalse() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel model = createModel(1L, "A", "desc", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(model);
        exchange.setProperty("previous", model);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertFalse(dto.isHasChange());
        assertFalse(dto.isNameChanged());
    }

    @Test
    void testNameChange_setsHasChangeTrueAndOldName() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "Old", "desc", true, 1, timestamp.minusDays(1));
        DataModel current = createModel(1L, "New", "desc", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertTrue(dto.isNameChanged());
        assertEquals("New", dto.getName());
        assertEquals("Old", dto.getOldName());
    }

    @Test
    void testStatusChange_setsHasChangeTrue() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "A", "desc", false, 1, timestamp.minusDays(1));
        DataModel current = createModel(1L, "A", "desc", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertFalse(dto.isNameChanged());
        assertTrue(dto.isActive());
    }

    @Test
    void testDescriptionChange_setsHasChangeTrue() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "A", "desc1", true, 1, timestamp.minusDays(1));
        DataModel current = createModel(1L, "A", "desc2", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertFalse(dto.isNameChanged());
        assertEquals("desc2", dto.getDescription());
    }

    @Test
    void testOlderVersion_throwsException() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "A", "desc", true, 3, timestamp);
        DataModel current = createModel(1L, "A", "desc", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> processor.process(exchange));
        assertEquals("Previous version is newer than current version.", exception.getMessage());
    }

    @Test
    void testPreviousModifiedOnAfterCurrent_throwsException() {
        OffsetDateTime now = OffsetDateTime.now();
        DataModel previous = createModel(1L, "A", "desc", true, 1, now.plusMinutes(1));
        DataModel current = createModel(1L, "A", "desc", true, 2, now);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> processor.process(exchange));
        assertEquals("Previous modifiedOn is after current modifiedOn.", exception.getMessage());
    }

    @Test
    void testNameAndDescriptionChange_setsHasChangeAndOldName() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "Old", "desc1", true, 1, timestamp.minusDays(1));
        DataModel current = createModel(1L, "New", "desc2", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertTrue(dto.isNameChanged());
        assertEquals("Old", dto.getOldName());
        assertEquals("desc2", dto.getDescription());
    }

    @Test
    void testStatusAndDescriptionChange_setsHasChange() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "A", "desc1", false, 1, timestamp.minusDays(1));
        DataModel current = createModel(1L, "A", "desc2", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertFalse(dto.isNameChanged());
        assertTrue(dto.isActive());
        assertEquals("desc2", dto.getDescription());
    }

    @Test
    void testAllFieldsChanged_setsHasChangeAndOldName() throws Exception {
        OffsetDateTime timestamp = OffsetDateTime.now();
        DataModel previous = createModel(1L, "Old", "desc1", false, 1, timestamp.minusDays(1));
        DataModel current = createModel(1L, "New", "desc2", true, 2, timestamp);

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody(current);
        exchange.setProperty("previous", previous);

        processor.process(exchange);
        DataModelDto dto = exchange.getMessage().getBody(DataModelDto.class);

        assertTrue(dto.isHasChange());
        assertTrue(dto.isNameChanged());
        assertEquals("Old", dto.getOldName());
        assertEquals("New", dto.getName());
        assertEquals("desc2", dto.getDescription());
        assertTrue(dto.isActive());
    }

}
