package jsonista.jcs.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.webpki.jcs.NumberToJSON;

public class CanonicalDoubleSerializer extends StdSerializer<Double> {

    public CanonicalDoubleSerializer() {
        this(null);
    }

    public CanonicalDoubleSerializer(Class<Double> t) {
        super(t);
    }

    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        if (Double.isNaN(value)) {
            throw new NotPermitted.NaNException();
        }

        if (Double.isInfinite(value)) {
            throw new NotPermitted.InfiniteException();
        }
        String canonicalDouble = NumberToJSON.serializeNumber(value);
        jgen.writeNumber(canonicalDouble);
    }

}
