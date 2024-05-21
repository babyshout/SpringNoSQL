package kopo.springnosql.app.controller;

import jakarta.validation.Valid;
import kopo.springnosql.app.controller.response.CommonApiResponse;
import kopo.springnosql.app.dto.MovieDTO;
import kopo.springnosql.app.service.IMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/movie/v1")
@RequiredArgsConstructor
@RestController
public class MovieController {
    private final IMovieService movieService;

    /**
     * CGV 영화 순위 가져오기
     */
    @PostMapping("speechcommand")
    public ResponseEntity getMovie(
            @Valid @RequestBody MovieDTO pDTO,
            BindingResult bindingResult
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            return CommonApiResponse.getErrors(bindingResult);
        }

        List<MovieDTO> rList = null;

        log.trace("pDTO : " + pDTO);

        // 영화와 비슷한 단어가 존재하면 CGV 영화 순위 가져오기 수행
        if ((pDTO.speechCommand().contains("영화")) || (pDTO.speechCommand().contains("영하"))
                || (pDTO.speechCommand().contains("연하")) || (pDTO.speechCommand().contains("연화"))) {
            // Java 8 부터 제공되는 Optional 활용하여 NPE(Null Pointer Exception) 처리
            rList = Optional.ofNullable(movieService.getMovieRank()).orElseGet(ArrayList::new);
        }

        return ResponseEntity.ok(
                CommonApiResponse.of(
                        HttpStatus.OK,
                        HttpStatus.OK.series().name(),
                        rList
                )
        );
    }


}
