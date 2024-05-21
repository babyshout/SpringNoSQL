package kopo.springnosql.app.persistance.redis.impl;

import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.persistance.redis.IMelonCacheMapper;
import kopo.springnosql.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class MelonCacheMapper implements IMelonCacheMapper {

    private final RedisTemplate<String, Object> redisDB;

    /**
     * 멜론 노래 리스트 저장하기
     *
     * @param pList    저장할 데이터
     * @param redisKey 저장할 키
     * @return 저장 결과
     */
    @Override
    public int insertSong(List<MelonDTO> pList, String redisKey) {
        // Redis 에 저장될 키
//        String key = "MELON_" + DateUtil.getDateTime("yyyyMMdd");

        redisDB.setKeySerializer(new StringRedisSerializer());
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(MelonDTO.class));

        // 람다식으로 데이터 저장하기
        pList.forEach(melonDTO -> redisDB.opsForList().leftPush(redisKey, melonDTO));

        // 저장된 데이터는 1시간동안 보관하기
        redisDB.expire(redisKey, 1, TimeUnit.HOURS);

        int res = 1;

        return res;
    }

    /**
     * 멜론 노래 키 정보 존재여부 체크하기
     *
     * @param redisKey 저장된 키 이름
     * @return key 존재 여부
     */
    @Override
    public boolean getExistKey(String redisKey) {
        return Boolean.TRUE.equals(redisDB.hasKey(redisKey));
    }

    /**
     * 오늘 수집된 멜론 노래리스트 가져오기
     *
     * @param redisKey 저장된 키 이름
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getSongList(String redisKey) {
        redisDB.setKeySerializer(new StringRedisSerializer());
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(MelonDTO.class));

        List<MelonDTO> rList = null;
        // 저장된 키가 존재한다면
        if (redisDB.hasKey(redisKey)) {
            rList = (List) redisDB.opsForList().range(redisKey, 0, -1);
        }

        // 저장된 데이터는 1시간동안 연장하기
        redisDB.expire(redisKey, 1, TimeUnit.HOURS);

        return rList;
    }
}
