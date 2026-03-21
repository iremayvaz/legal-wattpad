package com.iremayvaz.common.model.mapper;

import com.iremayvaz.common.model.dto.response.RatingSummaryDto;
import com.iremayvaz.common.model.entity.StoryRating;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    // RatingSummaryDto genellikle dinamik hesaplandığı için
    // StoryRating -> DTO dönüşümü ihtiyaca göre buraya eklenebilir.
}
