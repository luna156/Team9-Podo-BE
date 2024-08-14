package com.softeer.podo.event.model.entity;

import com.softeer.podo.common.entity.DateEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "event_rewards")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventReward extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long id;
    private int rewardRank;
    /**
     * 당첨자수
     */
    @Column(name = "winner_number")
    private int numWinners;
    private String reward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
