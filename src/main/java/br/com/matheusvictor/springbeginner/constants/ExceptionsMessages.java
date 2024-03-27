package br.com.matheusvictor.springbeginner.constants;


import lombok.Getter;

@Getter
public enum ExceptionsMessages {

    PRODUCT_NOT_FOUND("Product not found"),
    PRODUCT_EXISTS("A product with the same name and description already exists.");

    private final String message;

    ExceptionsMessages(String message) {
        this.message = message;
    }

}
