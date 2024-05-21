package kopo.springnosql.app.service.impl;

import kopo.springnosql.app.dto.MovieDTO;
import kopo.springnosql.app.persistance.redis.IMovieMapper;
import kopo.springnosql.app.service.IMovieService;
import kopo.springnosql.common.util.CmmUtil;
import kopo.springnosql.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieService implements IMovieService {
    private final IMovieMapper movieMapper;
    /**
     * 영화 정보 가져오기
     */
    @Override
    public List<MovieDTO> getMovieRank() throws IOException {
        String redisKey = "CGV_" + DateUtil.getDateTime("yyyyMMdd");

        // 오늘 CGV 영화정보를 수집정보가 없다면, 영화 정보 수집하기
        if (!movieMapper.getExistKey(redisKey)) {
            int res = this.collectMovie(redisKey);

            log.info("수집된 영화 수 : " + res);
        }

        // 수집된 정보에서 가져오기
        List<MovieDTO> rList = movieMapper.getMovieList(redisKey);

        return rList;
    }

    private int collectMovie(String redisKey) throws IOException {
        int res = 0;

        // CGV 영화 순위 정보 가져올 사이트 주소
        String url = "http://www.cgv.co.kr/movies/";

        // JSOUP 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML 소스 저장할 변수
        Document doc = null;

        // 사이트 접속(http 프로토콜만 가능, https 프로토콜은 보안상 안됨) ??? 왜??
        doc = Jsoup.connect(url).get();

        // CGV 웹페이지의 전체 소스 중 일부 태그를 선택하기 위해 사용
        // <div class="sect-movie-chart"> 이 태그 내에 있는 HTML 소스만 element에 저장됨
        Elements element = doc.select("div.sect-movie-chart");

        // Iterator 을 사용하여 영화순위 정보를 가져오기
        // 영화순위는 기본적으로 1개 이상의 영화가 존재하기 때문에 태그의 반복이 존재할 수 밖에 없음
        Iterator<Element> movie_rank = element.select("strong.rank").iterator();    // 영화 순위
        Iterator<Element> movie_name = element.select("strong.title").iterator();
        Iterator<Element> movie_reserve = element.select("strong.percent span").iterator();
        Iterator<Element> score = element.select("strong.percent").iterator();
        Iterator<Element> open_day = element.select("strong.txt-info").iterator();

        // 수집된 데이터 DB에 저장하기
        while (movie_rank.hasNext()) {
            MovieDTO pDTO = MovieDTO.builder()
                    .collectTime(DateUtil.getDateTime("yyyyMMdd"))
                    .rank(CmmUtil.nvl(movie_rank.next().text()).trim())
                    .name(CmmUtil.nvl(movie_name.next().text()).trim())
                    .reserve(CmmUtil.nvl(movie_reserve.next().text()).trim())
                    .score(CmmUtil.nvl(score.next().text()).trim())
                    .openDay(CmmUtil.nvl(open_day.next().text()).trim().substring(0, 10))
                    .build();

            // 영화 한개씩 추가
            res += movieMapper.insertMovie(pDTO, redisKey);
        }

        return res;
    }
}
