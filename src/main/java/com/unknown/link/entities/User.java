package com.unknown.link.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.neo4j.core.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Node("users")
@Data
@NoArgsConstructor
public class User {
    @Id @GeneratedValue
    private Long id;

    private String userId;

    @Relationship(type = "SUBSCRIBED_TO", direction = Relationship.Direction.OUTGOING)
    @Lazy
    @JsonIgnore
    private List<Subscribe> subs = new ArrayList<>();

    public User(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId);
    }
}
