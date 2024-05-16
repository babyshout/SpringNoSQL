package kopo.springnosql.app.persistance.redis.impl;


import kopo.springnosql.app.dto.RedisDTO;
import kopo.springnosql.app.persistance.redis.IMyRedisMapper;
import kopo.springnosql.common.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class MyRedisMapper implements IMyRedisMapper {

    private final RedisTemplate<String, Object> redisDB;


    /**
     * String 타입 저장하기
     *
     * @param redisKey Redis 저장 키
     * @param pDTO     저장할 정보
     * @return 저장 성공 여부
     */
    @Override
    public int saveString(String redisKey, RedisDTO pDTO) {
        int res;

        String saveData = CmmUtil.nvl(pDTO.text()); // 저장할 값

        /*
        redis 저장 및 읽기에 대한 데이터 타입 지정 (String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer());
        redisDB.setValueSerializer(new StringRedisSerializer());

        this.deleteRedisKey(redisKey);  // RedisDB 저장된 키 삭제

        // 데이터 저장하기
        redisDB.opsForValue().set(redisKey, saveData);

        // RedisDB 에 저장되는 데이터의 유효시간 설정 (TTL 설정)
        // 2일이 지나면, 자동으로 데이터가 삭제되도록 설정함
        redisDB.expire(redisKey, 2, TimeUnit.DAYS);

        res = 1;

        return res;
    }

    /**
     * String 타입 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    @Override
    public RedisDTO getString(String redisKey) {

        log.trace("String redisKey : " + redisKey);

        /**
         * redis 저장 및 읽기에 대한 데이터 타입 지정 (String 타입으로 지정함)
         */
        redisDB.setKeySerializer(new StringRedisSerializer());
        redisDB.setValueSerializer(new StringRedisSerializer());

        RedisDTO rDTO = null;

        if (redisDB.hasKey(redisKey)) {
            String res = (String) redisDB.opsForValue().get(redisKey);  // redisKey 통해 조회하기

            log.trace("reds : " + res);

            // RedisDB 에 저장된 데이터를 DTO 에 저장하기
            rDTO = RedisDTO.builder()
                    .text(res).build();
        }

        return rDTO;
    }

    /**
     * String 타입에 JSON 형태로 저장하기
     *
     * @param redisKey Redis 저장 키 (몽고 db 의 콜렉션 생각)
     * @param pDTO     저장할 정보
     * @return 결과 값
     */
    @Override
    public int saveStringJSON(String redisKey, RedisDTO pDTO) {

        // redisDB 의 데이터 타입을 String 으로 정의 (항상 String 으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer());

        // ReidsDTO 에 저장된 데이터를 자동으로 JSON 으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));

//        this.deleteRedisKey(redisKey);

        // 데이터 저장하기
        redisDB.opsForValue().set(redisKey, pDTO);

        // RedisDB 에 저장되는 데이터의 유효시간 설정 (TTL 설정)
        // 2일이 지나면, 자동으로 데이터가 삭제되도록 설정함
        redisDB.expire(redisKey, 2, TimeUnit.DAYS);


        return 1;
    }

    /**
     * String 타입에 JSON 형태로 저장된 데이터 가져오기
     *
     * @param redisKey 가져올 redisKey
     * @return 결과 값 (조회한 값)
     */
    @Override
    public RedisDTO getStringJSON(String redisKey) {

        RedisDTO rDTO = null;

        // redisDB 의 키와 데이터타입을 String 으로 정의 (항상 String 으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer());

        // RedisDTO 에 저장된 데이터를 자동으로 JSON 으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisDTO.class));


        if (redisDB.hasKey(redisKey)) {
            rDTO = (RedisDTO) redisDB.opsForValue().get(redisKey);
        }

        return rDTO;
    }

    private void set_stringKeyAndStringValue_toRedisDB() {
        redisDB.setKeySerializer(new StringRedisSerializer());
        redisDB.setValueSerializer(new StringRedisSerializer());

    }

    /**
     * List 타입에 여러 문자열로 저장하기(동기화)
     *
     * @param redisKey Redis 저장 키
     * @param pList    저장할 정보들
     * @return 저장 성공 여부
     */
    @Override
    public int saveList(String redisKey, List<RedisDTO> pList) {

        // redis 저장 및 읽기에 대한 데이터 타입 지정 (String 타입으로 지정함)
        // TODO 일반화 하기
        set_stringKeyAndStringValue_toRedisDB();

        this.deleteRedisKey(redisKey);

        pList.forEach(dto -> {
//            // 오름차순으로 저장하기
//            redisDB.opsForList().rightPush(redisKey, CmmUtil.nvl(dto.text()));

            // 내림차순으로 저장하기
            redisDB.opsForList().leftPush(redisKey, CmmUtil.nvl(dto.text()));
        });

        // 저장되는 데이터의 유효기간(TTL) 은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);


        return 1;
    }

    /**
     * List 타입에 여러 문자열로 저장된 데이터 가져오기
     *
     * @param redisKey 가져올 redisKey
     * @return 결과 값
     */
    @Override
    public List<String> getList(String redisKey) {

        this.set_stringKeyAndStringValue_toRedisDB();

        if (Boolean.FALSE.equals(redisDB.hasKey(redisKey))) {
            return null;
        }

        // FIXME 노란줄 해결하기
        // TODO 범위 지정 변경해보기..!
        return (List) redisDB.opsForList().range(redisKey, 0, -1);
    }

    private <T> void set_stringKeyAndJsonObjectValue_toRedisDB(Class<T> DTOClass) {

        // redisDB 의 키의 데이터 타입을 String 으로 정의 (항상 String 으로 설정함)
        redisDB.setKeySerializer(new StringRedisSerializer());
        // RedisDTO 에 저장된 데이터를 자동으로 JSON 으로 변경하기
        redisDB.setValueSerializer(new Jackson2JsonRedisSerializer<>(DTOClass));
    }

    /**
     * List 타입에 JSON 형태로 저장하기(동기화)
     *
     * @param redisKey Redis 저장 키
     * @param pList    저장할 정보들
     * @return 저장 성공 여부
     */
    @Override
    public int saveListJSON(String redisKey, List<RedisDTO> pList) {
        this.set_stringKeyAndJsonObjectValue_toRedisDB(RedisDTO.class);

        this.deleteRedisKey(redisKey);

        // 람다식 사용하여 데이터 저장
        pList.forEach(dto -> redisDB.opsForList().rightPush(redisKey, dto));

        // 저장되는 데이터의 유효기간(TTL) 은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        return 1;
    }

    /**
     * List 타입에 JSON 형태로 저장된 데이터 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    @Override
    public List<RedisDTO> getListJSON(String redisKey) {

        this.set_stringKeyAndJsonObjectValue_toRedisDB(RedisDTO.class);

        if (!redisDB.hasKey(redisKey)) {
            return null;
        }
        return (List) redisDB.opsForList().range(redisKey, 0, -1);
    }


    private void set_stringKeyForRedisKey_AndStringHashKeyAndValue_toRedisDB() {
        // redis 저장 및 읽기에 대한 데이터 타입 지정 (String 타입으로 지정함)
        redisDB.setKeySerializer(new StringRedisSerializer());  // String 타입
        redisDB.setHashKeySerializer(new StringRedisSerializer());  // Hash 구조의 키 타입, String 타입
        redisDB.setHashValueSerializer(new StringRedisSerializer());    // Hash 구조의 값 타입, String 타입

    }

    /**
     * Hash 타입에 문자열 형태로 저장하기
     *
     * @param redisKey Redis 저장 키
     * @param pDTO     저장할 정보들
     * @return 저장 성공여부
     */
    @Override
    public int saveHash(String redisKey, RedisDTO pDTO) {
        // FIXME 일반화가 하나도 안되어있는데... 괜찮은지
        this.set_stringKeyForRedisKey_AndStringHashKeyAndValue_toRedisDB();

        this.deleteRedisKey(redisKey);

        // FIXME 중복..
        redisDB.opsForHash().put(redisKey, "name", CmmUtil.nvl(pDTO.name()));
        redisDB.opsForHash().put(redisKey, "email", CmmUtil.nvl(pDTO.email()));
        redisDB.opsForHash().put(redisKey, "addr", CmmUtil.nvl(pDTO.addr()));

        // 저장되는 데이터의 유효기간(TTL) 은 100분으로 정의
        redisDB.expire(redisKey, 100, TimeUnit.MINUTES);

        return 1;
    }

    /**
     * Hash 타입에 문자열 형태로 저장된 값 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 조회 결과값
     */
    @Override
    public RedisDTO getHash(String redisKey) {

        // redis 저장 및 읽기에 대한 데이터타입 지정(String 타입으로 지정함)
        this.set_stringKeyForRedisKey_AndStringHashKeyAndValue_toRedisDB();

        if (!redisDB.hasKey(redisKey)) {
            return null;
        }

        String name = CmmUtil.nvl((String) redisDB.opsForHash().get(redisKey, "name"));
        String email = CmmUtil.nvl((String) redisDB.opsForHash().get(redisKey, "email"));
        String addr = CmmUtil.nvl((String) redisDB.opsForHash().get(redisKey, "addr"));

        RedisDTO rDTO = RedisDTO.builder()
                .name(name)
                .email(email)
                .addr(addr)
                .build();
        log.trace(rDTO.toString());
        return rDTO;
    }

    /**
     * Set 타입에 JSON 형태로 람다식을 이용하여 저장하기
     *
     * @param redisKey Redis 저장 키
     * @param pList    저장할 정보들
     * @return 저장성공 여부
     */
    @Override
    public int saveSetJSON(String redisKey, List<RedisDTO> pList) {
        this.set_stringKeyAndJsonObjectValue_toRedisDB(RedisDTO.class);

        this.deleteRedisKey(redisKey);

        log.trace("입력받은 데이터 수 : " + pList.size());

        // Set 구조는 저장 순서에 상관없이 저장하기 때문에 List 구조와 달리 방향이 존재하지 않음
        pList.forEach(dto -> redisDB.opsForSet().add(redisKey, dto));

        // 저장되는 데이터의 유효기간(TTL) 은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        return 1;

    }

    /**
     * Set 타입에  JSON 형태로 람다식을 이용하여 저장된 값 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    @Override
    public Set<RedisDTO> getSetJSON(String redisKey) {

        // 결과값을 전달할 객체
        Set<RedisDTO> rSet = null;

        // Redis 의 키와 밸류타입을 정의
        this.set_stringKeyAndJsonObjectValue_toRedisDB(RedisDTO.class);

        if (!redisDB.hasKey(redisKey)) {
        return null;
        }

        return (Set) redisDB.opsForSet().members(redisKey);

    }

    /**
     * List 타입에 JSON 형태로 람다식을 이용하여 Set 에 저장된 값 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    @Override
    public List<RedisDTO> getSetJSONAsList(String redisKey) {
        return null;
    }

    /**
     * ZSet 타입에 JSON 형태로 저장하기
     *
     * @param redisKey redis 저장 키
     * @param pList    저장할 정보들
     * @return 저장 성공 여부
     */
    @Override
    public int saveZSetJSON(String redisKey, List<RedisDTO> pList) {

        // redisDB 의 키의 데이터타입을 String 으로 정의(항상 String 으로 설정함)
        this.set_stringKeyAndJsonObjectValue_toRedisDB(RedisDTO.class);

        this.deleteRedisKey(redisKey);  // Redis 저장된 키 삭제

        pList.forEach(dto -> {
            redisDB.opsForZSet().add(redisKey, dto, dto.order());
        });

        // 저장되는 데이터의 유효기간(TTL) 은 5시간으로 정의
        redisDB.expire(redisKey, 5, TimeUnit.HOURS);

        return 1;
    }

    /**
     * ZSet 타입에 JSON 형태로 저장된 값 가져오기
     *
     * @param redisKey 가져올 redis key
     * @return 결과 값
     */
    @Override
    public Set<RedisDTO> getZSetJSON(String redisKey) {


        this.set_stringKeyAndJsonObjectValue_toRedisDB(RedisDTO.class);

        if (!redisDB.hasKey(redisKey)) {
            return null;
        }

        return (Set) redisDB.opsForZSet().range(redisKey, 0, -1);
    }

    /**
     * RedisDB 저장된 키 삭제하는 공통 함수
     */
    private void deleteRedisKey(String redisKey) {
        if (redisDB.hasKey(redisKey)) {
            redisDB.delete(redisKey);

            log.info(redisKey + "삭제 성공!!");
        }
    }
}
