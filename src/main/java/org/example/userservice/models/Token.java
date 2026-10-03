package org.example.userservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tokens")
@Getter
@Setter
public class Token extends BaseModel {
    private String value;

    @ManyToOne
    private User user;

    private Date expiryAt;
}
