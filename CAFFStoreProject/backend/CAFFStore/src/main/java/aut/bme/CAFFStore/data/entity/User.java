package aut.bme.CAFFStore.data.entity;

import aut.bme.CAFFStore.data.util.password.PasswordSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GenericGenerator(name = "sequence_uuid", strategy = "aut.bme.CAFFStore.data.util.UUIDGenerator")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "sequence_uuid")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private String id;

    @Column(name = "person_name", nullable = false)
    private String personName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    @JsonSerialize(using = PasswordSerializer.class)
    private byte[] password;

    @Column(name = "salt", nullable = false)
    @JsonSerialize(using = PasswordSerializer.class)
    private byte[] salt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }
}
