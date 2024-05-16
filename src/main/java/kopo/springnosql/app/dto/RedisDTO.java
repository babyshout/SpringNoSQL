package kopo.springnosql.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record RedisDTO(
        String name,
        String email,
        String addr,
        String text,
        float order
) {
}
