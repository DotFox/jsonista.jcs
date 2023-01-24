package jsonista.jcs.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;

public class NotPermitted {
    public static class NaNException extends JsonProcessingException {
        NaNException() {
            super("NaN is not permitted in JSON.");
        }
    }

    public static class InfiniteException extends JsonProcessingException {
        InfiniteException() {
            super("Infinite is not permitted in JSON.");
        }
    }

    public static class DuplicateKeyException extends JsonProcessingException {
        DuplicateKeyException(Object originKey, String serializedKey) {
            super("Duplicate key found: " + originKey.toString() + " serialized as " + serializedKey);
        }
    }
}
