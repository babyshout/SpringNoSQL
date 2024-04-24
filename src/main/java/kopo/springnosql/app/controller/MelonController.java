package kopo.springnosql.app.controller;

import kopo.springnosql.app.controller.response.CommonApiResponse;
import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.dto.MsgDTO;
import kopo.springnosql.app.service.IMelonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/melon/v1")
@RequiredArgsConstructor
@RestController
public class MelonController {

    private final IMelonService melonService;

    /**
     * 멜론 노래 리스트 저장하기
     */
    @PostMapping("collectMelonSong")
    public ResponseEntity collectMelonSong() {
        log.info(this.getClass().getName() + ".collectMelonSong START!!!");

        // 수집결과 출력
        String msg = "";

        int res = melonService.collectMelonSong();

        if (res == 1) {
            msg = "멜론차트 수집 성공!";
        } else {
            msg = "멜론차트 수집 실패!";
        }

        MsgDTO dto = MsgDTO.builder()
                .result(res)
                .msg(msg).build();


        log.info(this.getClass().getName() + ".collectMelonSong END!!!");

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.name(), dto)
        );
    }

    /**
     * 오늘 수집된 멜론 노래리스트 가져오기
     */
    @PostMapping("getSongList")
    public ResponseEntity getSongList() {
        log.info(this.getClass().getName() + ".collectMelonSong START!!!");

        // Java8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(
                melonService.getSongList()
        ).orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".collectMelonSong END!!!");

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.name(), rList)
        );
    }

    /**
     * 가수별 수집된 노래의 수 가져오기
     */
    @PostMapping("getSingerSongCount")
    public ResponseEntity getSingerSongCount() {
        log.info(this.getClass().getName() + ".getSingerSongCount START!!!!");

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(melonService.getSingerSongCount())
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getSingerSongCount END!!!!");
        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );
    }

    /**
     * 가수 이름으로 조회하기
     */
    @PostMapping("getSingerSong")
    public ResponseEntity getSingerSong(@RequestBody MelonDTO pDTO) {
        log.info(this.getClass().getName() + ".getSingerSong START!!!");

        log.trace("pDTO : " + pDTO);

        // Java 8 부터 제공되는 Optional 활용하여 NPE (Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(
                melonService.getSingerSong(pDTO)
        ).orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getSingerSong END!!!");
        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );
    }

    /**
     * 수집된 멜론 차트 컬렉션 삭제하기
     */
    @PostMapping("dropCollection")
    public ResponseEntity dropCollection() {
        // 삭제 결과 출력
        String msg = "";

        int res = melonService.dropCollection();

        if (res == 1) {
            msg = "멜론 차트 삭제 성공!";
        } else {
            msg = "멜론차트 삭제 실패!";
        }

        MsgDTO dto = MsgDTO.builder()
                .result(res)
                .msg(msg).build();

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto)
        );

    }

    /**
     * 멜론 노래 리스트 저장하기
     */
    @PostMapping("insertManyField")
    public ResponseEntity insertManyField() {
        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(
                melonService.insertManyField()
        ).orElseGet(ArrayList::new);


        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );
    }

    @PostMapping("updateField")
    public ResponseEntity updateField(@RequestBody MelonDTO pDTO) {
        log.trace("pDTO : " + pDTO);

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(melonService.updateField(pDTO)
        ).orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );
    }

    /**
     * 가수 별명 추가하기
     * 예 : 방탄소년단을 BTS 별명 추가하기
     */
    @PostMapping("updateAddField")
    public ResponseEntity updateAddField(@RequestBody MelonDTO pDTO) {
        log.trace(String.valueOf(pDTO));

        // Java 8 부터 제공되는 Optional 활용하여 NPE (Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(melonService.updateAddField(pDTO))
                .orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );

    }

    /**
     * 가수 멤버 이름들(List 구조 필드) 추가하기
     */
    @PostMapping("updateAddListField")
    public ResponseEntity updateAddListField(@RequestBody MelonDTO pDTO) {
        log.trace("pDTO : " + pDTO);

        // Java 8 부터 제공하는 Optional 활용하여 NPE(Null Pointer Exception 처리)
        List<MelonDTO> rList = Optional.ofNullable(
                melonService.updateAddListField(pDTO)
        ).orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );
    }

    /**
     * 가수이름이 방탄소년단을 BTS 로 변경 및 필드 추가하기
     */
    @PostMapping("updateFieldAndAddField")
    public ResponseEntity updateFiledAndAddField(@RequestBody MelonDTO pDTO) {
        log.info("pDTO : " + pDTO);

        // Java 8 부터 제공되는 optional 활용하여 NPE 처리
        List<MelonDTO> rList = Optional.ofNullable(
                melonService.updateFieldAndAddField(pDTO)
        ).orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );
    }

    /**
     * 가수이름이 singer 인 노래 삭제하기
     */
    @PostMapping("deleteDocument")
    public ResponseEntity deleteDocument(@RequestBody MelonDTO pDTO) {
        log.info("pDTO : " + pDTO); //JSON 구조로 받은 값이 잘 받았는지 확인하기 위해 로그찍기

        // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
        List<MelonDTO> rList = Optional.ofNullable(
                melonService.deleteDocument(pDTO)
        ).orElseGet(ArrayList::new);

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rList
                )
        );
    }
}
