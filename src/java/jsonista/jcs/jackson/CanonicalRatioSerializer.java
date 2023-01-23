package jsonista.jcs.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.webpki.jcs.NumberToJSON;

import clojure.lang.Ratio;

public class CanonicalRatioSerializer extends StdSerializer<Ratio> {

    public CanonicalRatioSerializer() {
        this(null);
    }

    public CanonicalRatioSerializer(Class<Ratio> t) {
        super(t);
    }

    @Override
    public void serialize(Ratio value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        String canonicalRatio = NumberToJSON.serializeNumber(value.doubleValue());
        jgen.writeNumber(canonicalRatio);
    }

}
