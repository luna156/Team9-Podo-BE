package com.softeer.podo.event.model.entity;

import com.softeer.podo.common.entity.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "event_arrival_users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArrivalUser extends DateEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	private String name;

	@Column(name = "phone_number", unique = true)
	private String phoneNum;
	private int arrivalRank;
	@Enumerated(EnumType.STRING)
	private Role role;
}
