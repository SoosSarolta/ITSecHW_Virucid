package aut.bme.CAFFStore.data.util;

import com.devskiller.friendly_id.FriendlyId;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.UUID;

public class UUIDGenerator implements IdentifierGenerator {

    public static String generateFriendlyUUID() {
        return FriendlyId.toFriendlyId(UUID.randomUUID());
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return FriendlyId.toFriendlyId(UUID.randomUUID());
    }
}