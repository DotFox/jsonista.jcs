package jsonista.jcs.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import clojure.lang.IFn;

public class CanonicalMapSerializer extends StdSerializer<Map<Object, Object>> {

    private final IFn keyEncoder;

    public CanonicalMapSerializer(IFn keyEncoder) {
        super(CanonicalMapSerializer.class, true);
        this.keyEncoder = keyEncoder;
    }

    @Override
    public void serialize(Map<Object, Object> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        Map<String, Object> m = new TreeMap<String, Object>();
        for (Object key : value.keySet()) {
            m.put((String)keyEncoder.invoke(key), value.get(key));
        }

        jgen.writeStartObject();
        for (String key : m.keySet()) {
            jgen.writeObjectField(key, m.get(key));
        }
        jgen.writeEndObject();
    }

}
