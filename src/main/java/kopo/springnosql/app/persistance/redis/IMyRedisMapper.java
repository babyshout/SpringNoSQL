package kopo.springnosql.app.persistance.redis;

import kopo.springnosql.app.dto.RedisDTO;
import org.springframework.boot.autoconfigure.cache.CacheProperties;

import java.util.List;
import java.util.Set;

public interface IMyRedisMapper {

    /**
     * String 타입 저장하기
     *
     * @param redisKey Redis 저장 키
     * @param pDTO     저장할 정보
     * @return 저장 성공 여부
     */
    int saveString(String redisKey, RedisDTO pDTO);

    /**
     * String 타입 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    RedisDTO getString(String redisKey);


    /**
     * String 타입에 JSON 형태로 저장하기
     *
     * @param redisKey Redis 저장 키 (몽고 db 의 콜렉션 생각)
     * @param pDTO     저장할 정보
     * @return 결과 값
     */
    int saveStringJSON(String redisKey, RedisDTO pDTO);

    /**
     * String 타입에 JSON 형태로 저장된 데이터 가져오기
     *
     * @param redisKey 가져올 redisKey
     * @return 결과 값 (조회한 값)
     */
    RedisDTO getStringJSON(String redisKey);


    /**
     * List 타입에 여러 문자열로 저장하기(동기화)
     *
     * @param redisKey Redis 저장 키
     * @param pList    저장할 정보들
     * @return 저장 성공 여부
     */
    int saveList(String redisKey, List<RedisDTO> pList);

    /**
     * List 타입에 여러 문자열로 저장된 데이터 가져오기
     *
     * @param redisKey 가져올 redisKey
     * @return 결과 값
     */
    List<String> getList(String redisKey);


    /**
     * List 타입에 JSON 형태로 저장하기(동기화)
     *
     * @param redisKey Redis 저장 키
     * @param pList 저장할 정보들
     * @return 저장 성공 여부
     */
    int saveListJSON(String redisKey, List<RedisDTO> pList);

    /**
     * List 타입에 JSON 형태로 저장된 데이터 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    List<RedisDTO> getListJSON(String redisKey);


    /**
     * Hash 타입에 문자열 형태로 저장하기
     * @param redisKey Redis 저장 키
     * @param pDTO 저장할 정보들
     * @return 저장 성공여부
     */
    int saveHash(String redisKey, RedisDTO pDTO);

    /**
     * Hash 타입에 문자열 형태로 저장된 값 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 조회 결과값
     */
    RedisDTO getHash(String redisKey);


    /**
     * Set 타입에 JSON 형태로 람다식을 이용하여 저장하기
     *
     * @param redisKey Redis 저장 키
     * @param pList 저장할 정보들
     * @return 저장성공 여부
     */
    int saveSetJSON(String redisKey, List<RedisDTO> pList);

    /**
     * Set 타입에  JSON 형태로 람다식을 이용하여 저장된 값 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    Set<RedisDTO> getSetJSON(String redisKey);
    /**
     * List 타입에 JSON 형태로 람다식을 이용하여 Set 에 저장된 값 가져오기
     *
     * @param redisKey 가져올 RedisKey
     * @return 결과 값
     */
    List<RedisDTO> getSetJSONAsList(String redisKey);


    /**
     * ZSet 타입에 JSON 형태로 저장하기
     *
     * @param redisKey redis 저장 키
     * @param pList 저장할 정보들
     * @return 저장 성공 여부
     */
    int saveZSetJSON(String redisKey, List<RedisDTO> pList);

    /**
     * ZSet 타입에 JSON 형태로 저장된 값 가져오기
     *
     * @param redisKey 가져올 redis key
     * @return 결과 값
     */
    Set<RedisDTO> getZSetJSON(String redisKey);
}
