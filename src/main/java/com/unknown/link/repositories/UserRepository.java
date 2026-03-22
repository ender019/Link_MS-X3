package com.unknown.link.repositories;

import com.unknown.link.entities.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface UserRepository extends Neo4jRepository<User, Long> {
    Optional<User> findUserByUserId(String userId);
}
