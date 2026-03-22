package com.unknown.link.entities;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
@Data
public class Subscribe {
    @RelationshipId
    private Long id;

    @TargetNode
    private User subscribe;

    @CreatedBy
    private String userId;

    @CreatedDate
    private LocalDateTime date;
}
