package kopo.springnosql.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record MovieDTO(
        // 수집시간
        String collectTime,
        // 영화 순위
        String rank,
        // 영화제목
        String name,
        // 예매율
        String reserve,
        // 영화 평점
        String score,
        // 개봉일
        String openDay,

        /**
         * 음성 명령 메시지
         */
        @NotBlank(message = "음성 명령 메시지는 필수 입력 사항입니다.")
        String speechCommand
) {

}
