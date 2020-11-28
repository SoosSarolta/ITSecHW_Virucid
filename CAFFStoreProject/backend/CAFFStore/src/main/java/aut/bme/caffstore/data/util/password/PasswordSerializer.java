package aut.bme.caffstore.data.util.password;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class PasswordSerializer extends StdSerializer<byte[]> {

    public PasswordSerializer(Class<byte[]> t) {
        super(t);
    }

    @Override
    public void serialize(byte[] password, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeObject(null);
    }
}