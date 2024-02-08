package joa.rancard.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * Not used for the reasons that -> directly throwing a custom exception extending MethodArgumentNotValidException
 * from a controller method doesn't work as expected because Spring MVC internally catches MethodArgumentNotValidException
 * and handles it during the validation process.
 */
@Getter
public class InvalidTransactionException extends RuntimeException {

    private final BindingResult bindingResult;

    public InvalidTransactionException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

}
