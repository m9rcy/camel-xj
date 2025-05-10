package com.sample;

import org.apache.camel.Message;
import org.apache.camel.ValidationException;
import org.apache.camel.spi.DataType;
import org.apache.camel.spi.Validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static java.lang.String.format;


@Component
public class CustomValidator extends Validator {

    private final org.springframework.validation.Validator validator;

    public CustomValidator(org.springframework.validation.Validator validator) {
        this.validator = validator;
    }
    @Override
    public void validate(Message message, DataType type) throws ValidationException {
        Object body = message.getBody();
        System.out.println("Validating message body: " + body);

        if (!(body instanceof FruitEvent)) {
            throw new ValidationException(message.getExchange(), "Expected OrderResponse, but was " + body.getClass());
        }

        Errors errors = new BeanPropertyBindingResult(body, FruitEvent.class.getSimpleName());

        // Validate the object using Spring's validator
        validator.validate(body, errors);

        if (errors.hasErrors()) {
            // Format validation errors
            String errorMessage = errors.getAllErrors().stream()
                    .map(error -> error.getObjectName() + "." + error.getCode() + ": " + error.getDefaultMessage())
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("Unknown validation error");

            throw new ValidationException(message.getExchange(), errorMessage);
        }
    }
}
