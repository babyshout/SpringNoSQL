package kopo.springnosql.app.controller;

import kopo.springnosql.app.controller.response.CommonApiResponse;
import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.dto.MsgDTO;
import kopo.springnosql.app.service.IMelonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.Option;
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
}
