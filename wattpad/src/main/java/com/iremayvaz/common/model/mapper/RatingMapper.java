package com.iremayvaz.common.model.mapper;

import com.iremayvaz.common.model.dto.response.RatingSummaryDto;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    RatingSummaryDto toSummaryDto(BigDecimal average, Long ratingCount);
}
