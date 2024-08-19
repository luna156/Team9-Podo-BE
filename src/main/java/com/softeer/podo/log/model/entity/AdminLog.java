package com.softeer.podo.log.model.entity;

import com.softeer.podo.common.entity.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_logs")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminLog extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String requestPath;
    @Column(length = 1000)
    private String requestHeader;
    @Column(length = 1000)
    private String requestBody;

}
