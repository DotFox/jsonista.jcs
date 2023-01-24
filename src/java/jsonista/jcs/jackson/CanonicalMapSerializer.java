package jsonista.jcs.jackson;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import clojure.lang.IFn;

public class CanonicalMapSerializer extends StdSerializer<Map<Object, Object>> {

    private final IFn keyEncoder;

    public CanonicalMapSerializer(IFn keyEncoder) {
        super(CanonicalMapSerializer.class, true);
        this.keyEncoder = keyEncoder;
    }

    class ExplicitLexicographicComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }

    @Override
    public void serialize(Map<Object, Object> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        Map<String, Object> m = new TreeMap<String, Object>(new ExplicitLexicographicComparator());
        for (Object originKey : value.keySet()) {
            String serializedKey = (String)keyEncoder.invoke(originKey);
            if (m.containsKey(serializedKey)) {
                throw new NotPermitted.DuplicateKeyException(originKey, serializedKey);
            }
            m.put(serializedKey, value.get(originKey));
        }

        jgen.writeStartObject();
        for (String key : m.keySet()) {
            jgen.writeObjectField(key, m.get(key));
        }
        jgen.writeEndObject();
    }

}
