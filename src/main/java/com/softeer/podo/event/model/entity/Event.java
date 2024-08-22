package com.softeer.podo.event.model.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.softeer.podo.common.entity.DateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "events")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private EventType eventType;

    private String title;
    private String description;

    /**
     * 형식은 7자리 0과 1로 이루어진 문자열. 월화수목금토일 의미
     */
    private String repeatDay;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime repeatTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time")
    private LocalDateTime startAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_time")
    private LocalDateTime endAt;

    private String tagImage;

    @OneToMany(mappedBy = "event" , orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EventReward> eventRewardList;

    @OneToOne(mappedBy = "event" , orphanRemoval = true, cascade = CascadeType.ALL)
    private EventWeight eventWeight;

    public void updateEvent(String title, String description, String repeatDay, LocalTime repeatTime, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.description = description;
        this.repeatDay = repeatDay;
        this.repeatTime = repeatTime;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void updateEventRewardList(List<EventReward> eventRewardList) {
        this.eventRewardList = eventRewardList;
    }

    public void updateTagImageLink(String imageLink) {
        this.tagImage = imageLink;
    }
}
