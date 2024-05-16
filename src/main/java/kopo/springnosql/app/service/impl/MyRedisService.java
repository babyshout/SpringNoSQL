package kopo.springnosql.app.service.impl;

import jdk.jshell.spi.ExecutionControlProvider;
import kopo.springnosql.app.dto.RedisDTO;
import kopo.springnosql.app.persistance.redis.IMyRedisMapper;
import kopo.springnosql.app.service.IMyRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class MyRedisService implements IMyRedisService {

    private final IMyRedisMapper myRedisMapper;

    /**
     * String 타입 저장 및 가져오기
     *
     * @param pDTO
     */
    @Override
    public RedisDTO saveString(RedisDTO pDTO) {
        // 저장할 RedisDB 키
        String redisKey = "myRedis_String";

        // 저장 결과
        RedisDTO rDTO = null;

        int res = myRedisMapper.saveString(redisKey, pDTO);

        if (res == 1) {
            // Redis 저장이 성공하면, 저장된 데이터 가져오기
            rDTO = myRedisMapper.getString(redisKey);
        } else {
            log.warn("Redis 저장 실패!!");
            new Exception("Redis 저장 실패!!");
        }

        return rDTO;
    }

    /**
     * String 타입에 JSON 형태로 저장하기
     *
     * @param pDTO
     */
    @Override
    public RedisDTO saveStringJSON(RedisDTO pDTO) throws Exception {
        
        // 저장할 Redis Key
        String redisKey = "myRedis_String_JSON";
        

        // 정신못차리고 saveString 넣음..
        // saveStringJSON 넣어야 되는데... 이거 어떻게 해결할수 잇는 방법이 있나??
        // -> TODO 파라미터를 String 으로 강제하자!!!
        int res = myRedisMapper.saveString(redisKey, pDTO);
        
        
        // 0 -> 값 가져오기 실패
        if (res == 0) {
            log.error("Redis 저장 실패!!");
            throw new Exception("Redis 저장 실패!!");
        }

        // 로그찍고싶어서 rDTO 에 담음
        RedisDTO rDTO = myRedisMapper.getStringJSON(redisKey);
        log.trace("rDTO : " + rDTO);

        return rDTO;
    }

    /**
     * List 타입에 여러 문자열로 저장하기
     *
     * @param pList
     */
    @Override
    public List<String> saveList(List<RedisDTO> pList) throws Exception {
        // 저장할 RedisDB 키
        String redisKey = "myRedis_List";

        // 저장 결과
        List<String> rList = null;

        int res = myRedisMapper.saveList(redisKey, pList);

        if (res != 1) {
            log.error("Redis 저장 실패!!");
            throw new Exception("Redis 저장 실패!!");
        }

        return myRedisMapper.getList(redisKey);
    }

    /**
     * List 타입에 JSON 형태로 저장하기
     *
     * @param pList
     */
    @Override
    public List<RedisDTO> saveListJSON(List<RedisDTO> pList) throws Exception {
        // 저장할 RedisDB 키
        String redisKey = "myRedis_List_JSON";

        int res = myRedisMapper.saveListJSON(redisKey, pList);
        if (res != 1) {
            log.error("Redis 저장 실패!!");
            throw new Exception("Redis 저장 실패!!");
        }

        return myRedisMapper.getListJSON(redisKey);

    }

    /**
     * Hash 타입에 문자열 형태로 저장하기
     *
     * @param pDTO
     */
    @Override
    public RedisDTO saveHash(RedisDTO pDTO) throws Exception {
        // 저장할 Redis DB 키
        String redisKey = "myRedis_Hash";


        int res = myRedisMapper.saveHash(redisKey, pDTO);
        if (res != 1){
            log.error("Redis 저장 실패!!");
            throw new Exception("Redis 저장 실패!!");
        }

        return myRedisMapper.getHash(redisKey);
    }

    // NOTE 졸려서 안만들어짐..
//    private boolean isSaveResSuccessful(int saveRes) {
//        return saveRes != 1;
//    }

    /**
     * Set 타입에 JSON 형태로 저장하기
     *
     * @param pList
     */
    @Override
    public Set<RedisDTO> saveSetJSON(List<RedisDTO> pList) throws Exception {

        // 저장할 RedisDB 키
        String redisKey = "myRedis_Set_JSON";

        // 저장 결과
        Set<RedisDTO> rSet;

        int saveRes = myRedisMapper.saveSetJSON(redisKey, pList);
        if (saveRes != 1) {
            log.error("Redis 저장 실패!!");
            throw new Exception("Redis 저장 실패!!");
        }

        return myRedisMapper.getSetJSON(redisKey);
    }

    /**
     * ZSet 타입에 JSON 형태로 저장하기
     *
     * @param pList
     */
    @Override
    public Set<RedisDTO> saveZSetJSON(List<RedisDTO> pList) throws Exception {

        // 저장할 RedisDB 키
        String redisKey = "myRedis_ZSet_JSON";

        // 저장 결과
        Set<RedisDTO> rSet;

        int saveRes = myRedisMapper.saveZSetJSON(redisKey, pList);
        if (saveRes != 1) {
            log.error("Redis 저장 실패!!");
            throw new Exception("Redis 저장 실패!!");
        }

        return myRedisMapper.getZSetJSON(redisKey);
    }
}
