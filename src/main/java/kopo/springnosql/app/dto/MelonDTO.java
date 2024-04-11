package kopo.springnosql.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder
public record MelonDTO(
        /**
         *   Java 8 date/time type java.time.LocalDateTime not supported by default
         *   LocalDataTime을 역직렬화하지 못해서 생기는 문제입니다.
         *   추가적으로 만약 캐시로 사용할 객체에 LocalDateTime 타입의 값이
         *   존재한다면 위처럼 @JsonSerialize, @JsonDeserialize 어노테이션을 기입해줘야 합니다.
         *  그렇지 않으면 오류가 발생합니다.
         */
        @NotBlank(message = "수집 시간은 필수 입력 사항입니다.")
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @CreatedDate
        LocalDateTime collectTime, // 수집 시간
        String song,    //가수
        String singer,  // 차트에 등록된 가수별 노래 수
        int singerCount,        // 차트에 등록된 가수별 노래 수
        String updateSinger,    // 수정할 가수명 (MongoDB 필드 추가 교육용)
        String nickname,    // 추가될 닉네임 (MongoDB 필드 추가 교육용)
        List<String> member,    // 추가될 그룹 멤버 이름들(MongoDB 필드 추가 교육용)
        String addFieldValue    // 추가될 필드 값 (MongoDB 필드 추가 교육용)
        ) {
}
