package kopo.springnosql.app.controller;

import kopo.springnosql.app.controller.response.CommonApiResponse;
import kopo.springnosql.app.dto.RedisDTO;
import kopo.springnosql.app.service.IMyRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RequestMapping(value = "/redis/v1")
@RequiredArgsConstructor
@RestController
public class RedisController {

    private final IMyRedisService myRedisService;

    /**
     * Redis 문자열 저장 실습
     */
    @PostMapping("saveString")
    public ResponseEntity saveString(@RequestBody RedisDTO pDTO) {
        // 전달받은 값 로그로 확인하기!(반드시 작성하기)
        log.trace("pDTO : " + pDTO);

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception 처리)
        RedisDTO rDTO = Optional.ofNullable(
                myRedisService.saveString(pDTO)
        ).orElseGet(() -> RedisDTO.builder().build());

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rDTO
                )
        );
    }

    /**
     * Redis 문자열을 JSON 으로 저장 실습
     */
    @PostMapping(value = "saveStringJson")
    public ResponseEntity saveStringJSON(@RequestBody RedisDTO pDTO) throws Exception {
        log.trace("pDTO : " + pDTO);

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveStringJSON(pDTO))
//                .orElseGet(() -> RedisDTO.builder().build());
                .orElseGet(RedisDTO.builder()::build);
        // NOTE 얘 안됨!! :: 는 처음 찍힌 순간부터 객체를 고정하는?? 듯함
//                .orElseGet(RedisDTO::builder::build);

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rDTO
                )
        );
    }

    /**
     * List 타입에 여러 문자열로 저장하기 (동기화)
     */
    @PostMapping("saveList")
    public ResponseEntity saveList(@RequestBody List<RedisDTO> pList) throws Exception {
        log.trace("pList : " + pList);

        // Java8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<String> rList = Optional.ofNullable(myRedisService.saveList(pList))
                .orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rList
                )
        );
    }

    /**
     * List 타입에 JSON 형태로 저장하기(동기화)
     */
    @PostMapping(value = "saveListJSON")
    public ResponseEntity saveListJSON(@RequestBody List<RedisDTO> pList) throws Exception {
        log.trace("pList : " + pList);

        // Java8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<RedisDTO> rList = Optional.ofNullable(myRedisService.saveListJSON(pList))
                .orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rList
                )
        );
    }

    private final String saveHashPath = "saveHash";

    final public class HandlingURLClass {
        public final static String BASE = "/redis/v1";
        public final static String Save_HASH = "saveHash";
    }

    @RequiredArgsConstructor
    public enum HandlingURL {
        BASE("/redis/v1"),
        SAVE_HASH("saveHash"),
        ;

        public final String path;

        final public String path() {
            return this.path;
        }

    }

    /**
     * Hash 타입에 문자열 형태로 저장하기
     */
    @PostMapping(value = HandlingURLClass.Save_HASH)
//    @PostMapping(value = HandlingURLClass.Save_HASH)
    public ResponseEntity saveHash(@RequestBody RedisDTO pDTO) throws Exception {
        log.trace("pDTO : " + pDTO);
        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        RedisDTO rDTO = Optional.ofNullable(myRedisService.saveHash(pDTO))
                .orElseGet(RedisDTO.builder()::build);

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rDTO
                )
        );
    }

    /**
     * Set 타입에 JSON 형태로 람다식을 이용하여 저장하기
     */
    @PostMapping("saveSetJSON")
    public ResponseEntity saveSetJSON(@RequestBody List<RedisDTO> pList) throws Exception {
        log.trace("pList : " + pList);

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        Set<RedisDTO> rSet = Optional.ofNullable(myRedisService.saveSetJSON(pList))
                .orElseGet(HashSet::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rSet
                )
        );
    }

    /**
     * ZSet 타입에 JSON 형태로 저장하기
     */
    @PostMapping("saveZSetJSON")
    public ResponseEntity savedRedisZSetJSON(@RequestBody List<RedisDTO> pList) throws Exception {
        log.info("pList : " + pList);

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        Set<RedisDTO> rSet = Optional.ofNullable(myRedisService.saveZSetJSON(pList))
                .orElseGet(HashSet::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rSet)
        );
    }
}
