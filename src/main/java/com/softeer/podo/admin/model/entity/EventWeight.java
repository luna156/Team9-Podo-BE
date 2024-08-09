package com.softeer.podo.admin.model.entity;

import com.softeer.podo.common.entity.DateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_weights")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventWeight extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_id")
    private Long id;
    /**
     * 가중치 배수
     */
    private int times;
    @Column(name = "weight_condition")
    private String weightCondition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name =  "event_id")
    private Event event;

    public void updateWeightCondition(String weightCondition) {this.weightCondition = weightCondition;}
    public void updateTimes(int times) {this.times = times;}
}
