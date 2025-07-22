package com.unknown.link.repositories;

import com.unknown.link.entities.Subscribe;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SubRepository extends Neo4jRepository<Subscribe, Long> {
    @Query("""
        MATCH (source:users)-[r:SUBSCRIBED_TO]->(target:users)
        WHERE id(target) = $sub_id
        RETURN r;
    """)
    List<Map<String, String>> findSubscribesBySubId(@Param("sub_id") Long sub_id);
}
