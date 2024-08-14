package com.softeer.podo.event.model.entity;


import com.softeer.podo.common.entity.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_lots_comments")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotsComment extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private LotsUser lotsUser;

    @Column(name = "content")
    private String comment;
}
