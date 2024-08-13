package com.softeer.podo.admin.model.entity;

import com.softeer.podo.common.entity.DateEntity;
import com.softeer.podo.event.model.entity.LotsComment;
import com.softeer.podo.event.model.entity.TestResult;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "event_lots_users")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotsUser extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    private TestResult testResult;

    private String name;
    @Column(name = "phone_number", unique = true)
    private String phoneNum;
    @Setter
    private String reward;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Setter
    @OneToOne(mappedBy = "lotsUser", orphanRemoval = true, cascade = CascadeType.ALL)
    private LotsComment lotsComment;
}
