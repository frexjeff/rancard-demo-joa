package joa.rancard.exception;


import java.util.NoSuchElementException;

public class TransactionNotFoundException extends NoSuchElementException {

    public TransactionNotFoundException(String s) {
        super(s);
    }
}
