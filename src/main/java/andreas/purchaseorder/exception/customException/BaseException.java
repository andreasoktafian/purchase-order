package andreas.purchaseorder.exception.customException;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String message;

    public BaseException(String message) {
        super(message);
        this.message = message;
    }
}
