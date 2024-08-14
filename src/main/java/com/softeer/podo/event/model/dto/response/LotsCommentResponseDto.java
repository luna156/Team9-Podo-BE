package com.softeer.podo.event.model.dto.response;


import com.softeer.podo.event.model.entity.LotsComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotsCommentResponseDto {
    private LotsComment comment;
}
