package my.project.jackson;

import java.io.IOException;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.quarkus.jackson.ObjectMapperCustomizer;

/**
 * Customizer for {@link ObjectMapper} to serialize and deserialize {@link MyData}
 */
@Singleton
public class MyDataCustomizer implements ObjectMapperCustomizer {
    @Override
    public int priority() {
        return MINIMUM_PRIORITY;
    }

    @Override
    public void customize(final ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MyData.class, MyDataSerializer.INSTANCE);
        module.addDeserializer(MyData.class, MyDataDeserializer.INSTANCE);
        mapper.registerModule(module);
    }

    /**
     * Jackson serializer for {@link MyData}
     */
    private static class MyDataSerializer extends JsonSerializer<MyData> {

        /**
         * Create instance of {@link MyDataSerializer}
         */
        public static final MyDataSerializer INSTANCE = new MyDataSerializer();

        /** Private constructor */
        private MyDataSerializer() {
        }

        @Override
        public void serialize(final MyData value, final JsonGenerator gen,
                final SerializerProvider serializers) throws IOException {
            gen.writeStartArray();
            gen.writeString(value.getLineOne());
            gen.writeString(value.getLineTwo());
            gen.writeEndArray();
        }
    }

    /**
     * Jackson deserializer for {@link MyData}
     */
    private static class MyDataDeserializer extends JsonDeserializer<MyData> {

        /**
         * Create instance of {@link MyDataDeserializer}
         */
        public static final MyDataDeserializer INSTANCE = new MyDataDeserializer();

        /** Private constructor */
        private MyDataDeserializer() {
        }

        @Override
        public MyData deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            String lineOne = node.get(0).asText();
            String lineTwo = node.get(1).asText();
            return new MyData(lineOne, lineTwo);
        }
    }
}