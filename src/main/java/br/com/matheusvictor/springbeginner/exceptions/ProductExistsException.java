package br.com.matheusvictor.springbeginner.exceptions;

public class ProductExistsException extends IllegalArgumentException {

    public ProductExistsException() {
        super("Product already exists");
    }
}
