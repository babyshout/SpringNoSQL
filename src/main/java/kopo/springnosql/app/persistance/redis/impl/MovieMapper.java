package kopo.springnosql.app.persistance.redis.impl;

import kopo.springnosql.app.dto.MovieDTO;
import kopo.springnosql.app.persistance.redis.IMovieMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class MovieMapper implements IMovieMapper {

    private final RedisTemplate<String, Object> redisDB;

    /**
     * CGV 영화 정보 저장하기
     *
     * @param pDTO     저장할 데이터
     * @param redisKey 저장할 키
     * @return 저장 결과
     */
    @Override
    public int insertMovie(MovieDTO pDTO, String redisKey) {
        // redisDB 의 키의 데이터 타입을 String 으로 정의(항상 String 으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer());
        // MovieDTO 에 저장된 데이터를 자동으로 JSON 으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(MovieDTO.class));

        // 오름차순으로 저장하기
        redisDB.opsForList().rightPush(redisKey, pDTO);

        // 데이터 유효시간 1시간 연장하기
        this.setTimeOutHour(redisKey, 1);

        return 1;
    }

    /**
     * 수집된 영화 정보 존재여부 체크하기
     *
     * @param redisKey 저장된 키 이름
     * @return key 존재여부
     */
    @Override
    public boolean getExistKey(String redisKey) {
        return Optional.ofNullable(redisDB.hasKey(redisKey))
                .orElse(false);
    }

    /**
     * 1시간 이내 수집 및 호출된 영화 정보 가져오기
     *
     * @param redisKey 저장된 키 이름
     * @return 영화 정보
     */
    @Override
    public List<MovieDTO> getMovieList(String redisKey) {

        // redisDB 의 키의 데이터 타입을 String 으로 정의(항상 String 으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer());
        // MovieDTO 에 저장된 데이터를 자동으로 JSON 으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(MovieDTO.class));

        List<MovieDTO> rList = null;
        if (this.getExistKey(redisKey)) {
            rList = (List) redisDB.opsForList().range(redisKey, 0, -1);

            this.setTimeOutHour(redisKey, 1);
        }

        return rList;
    }

    private void setTimeOutHour(String redisKey, int hour) {
        redisDB.expire(redisKey, hour, TimeUnit.HOURS);
    }
}
