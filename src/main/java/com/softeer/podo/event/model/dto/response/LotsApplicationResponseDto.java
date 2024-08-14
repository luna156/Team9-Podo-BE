package com.softeer.podo.event.model.dto.response;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LotsApplicationResponseDto {
    @NotBlank
    private String uniqueLink;
}
