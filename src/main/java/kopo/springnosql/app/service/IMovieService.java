package kopo.springnosql.app.service;

import kopo.springnosql.app.dto.MovieDTO;

import java.io.IOException;
import java.util.List;

public interface IMovieService {
    /**
     * 영화 정보 가져오기
     */
    List<MovieDTO> getMovieRank() throws IOException;
}
