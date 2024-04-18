package kopo.springnosql.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record TokenDTO(
        String userId,
        String role
) {
}
