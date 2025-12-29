package com.iremayvaz.content.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RatingSummaryDto {
    private BigDecimal average;
    private Long ratingCount;
}
