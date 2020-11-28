package aut.bme.caffstore.data.util;

import com.devskiller.friendly_id.FriendlyId;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class UUIDGenerator implements IdentifierGenerator {

    public static String generateFriendlyUUID() {
        return FriendlyId.toFriendlyId(UUID.randomUUID());
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return FriendlyId.toFriendlyId(UUID.randomUUID());
    }
}