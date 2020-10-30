package aut.bme.CAFFStore.data.util.password;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PasswordSerializer extends StdSerializer<byte[]> {

    public PasswordSerializer() {
        this(null);
    }

    public PasswordSerializer(Class<byte[]> t) {
        super(t);
    }

    @Override
    public void serialize(
            byte[] password,
            JsonGenerator generator,
            SerializerProvider provider)
            throws IOException, JsonProcessingException {

        password = null;

        generator.writeObject(password);
    }
}