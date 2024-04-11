package kopo.springnosql.app.service.impl;

import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.persistance.mongodb.IMelonMapper;
import kopo.springnosql.app.service.IMelonService;
import kopo.springnosql.common.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MelonService implements IMelonService {

    private final IMelonMapper melonMapper;

    /**
     * 멜론 차트 수집 함수(웹 크롤링)
     */
    @SneakyThrows
    private List<MelonDTO> doCollect() {
        log.info(this.getClass().getName() + ".doCollect START!!");

        List<MelonDTO> pList = new LinkedList<>();

        // 멜론 Top100 중 50위까지 정보 가져오는 페이지
        String url = "https://www.melon.com/chart/index.htm";

        // JSOUP 라이브러리를 통해 사이트 접속되면, 그 사이트의 전체 HTML 소스 저장할 변수
        Document document = Jsoup.connect(url).get();

        // <div class="service_list_song"> 이 태그 내에서 있는 HTML 소스만 element 에 저장됨
        Elements elements = document.select("div.service_list_song");

        // Iterator 를 사용하여 멜론차트 정보를 가져오기
        for (Element songInfo : elements.select("div.wrap_song_info")) {
            // 크롤링을 통해 데이터 저장하기
            String song = CmmUtil.nvl(
                    songInfo.select("div.ellipsis.rank01 a").text()
            ); //노래
            String singer = CmmUtil.nvl(
                    songInfo.select("div.ellipsis.rank02 a").eq(0).text()
            );  // 가수

            log.info("song : " + song);
            log.info("singer : " + singer);

            // 가수와 노래 정보가 모두 수집되었다면, 저장함
            if ((!song.isEmpty()) && (!singer.isEmpty())) {
                MelonDTO pDTO = MelonDTO.builder()
                        .collectTime(LocalDateTime.now())
                        .song(song)
                        .singer(singer).build();

                log.debug("pDTO : " + pDTO);

                // 한번에 여러개의 데이터를 MongoDB 에 저장할 List 형태의 데이터 저장하기
                pList.add(pDTO);
            }
        }

        log.debug("pList.size() : " + pList.size());

        log.info(this.getClass().getName() + ".doCollect END!!!!");
        return pList;
    }

    /**
     * 멜론 노래 리스트 저장하기
     */
    @Override
    public int collectMelonSong() {
        log.info(this.getClass().getName() + ".collectMelonSong START!!!");

        int res = 0;

        // 생성할 컬렉션명
        String collectionName = getCollectionName();

        log.debug("collectionName : " + collectionName);

        // private 함수로 선언된 doCollect 함수를 호출하여 결과를 받기
        List<MelonDTO> rList = this.doCollect();

        // MongoDB 에 데이터 저장하기
        res = melonMapper.insertSong(rList, collectionName);

        // 로그 찍기(추후 찍은 로그를 통해 이 함수에 접근했는지 파악하기 용이하다.)
        log.info(this.getClass().getName() + ".collectMelonSong END!!!");

        return res;
    }

    /**
     * 오늘 수집된 멜론 노래리스트 가져오기
     */
    @Override
    public List<MelonDTO> getSongList() {
        log.info(this.getClass().getName() + ".getSongList START!!!");

        // MongoDB 에 저장된 컬렉션 이름
        String collectionName = getCollectionName();
        log.debug("collectionName : " +collectionName);

        // MongoDB 에서 데이터 가져오기
        List<MelonDTO> rList = melonMapper.getSongList(collectionName);

        log.debug("rList.size() : " + rList.size());


        log.info(this.getClass().getName() + ".getSongList END!!!");

        return rList;
    }

    /**
     * 멜론 가수별 노래 수 가져오기
     *
     * @return
     */
    @Override
    public List<MelonDTO> getSingerSongCount() {
        log.info(this.getClass().getName() + ".getSingerSongCount START!!!!");

        String collectionName = getCollectionName();

        List<MelonDTO> rList = melonMapper.getSingerSongCount(collectionName);

        log.info(this.getClass().getName() + ".getSingerSongCount END!!!!!!");

        return rList;
    }

    /**
     * 가수의 노래 가져오기
     *
     * @param pDTO singer 가 담겨있는 pDTO
     * @return 결과 리스트
     */
    @Override
    public List<MelonDTO> getSingerSong(MelonDTO pDTO) {
        log.info(this.getClass().getName() + ".getSingerSong START!!!");

        // MongoDB 에 저장된 컬렉션 이름
        String collectionName = getCollectionName();

        // 결과값
        List<MelonDTO> rList = null;

        // Melon 노래 수집하기
        if (this.collectMelonSong() == 1) {
            // 가수 노래 조회하기
            rList = melonMapper.getSingerSong(collectionName, pDTO);
        }


        log.info(this.getClass().getName() + ".getSingerSong START!!!");

        return rList;
    }

    /**
     * 수집된 멜론 차트 저장된 MongoDB 컬렉션 삭제하기
     */
    @Override
    public int dropCollection() {

        int res = 0;

        // MongoDB 에 저장된 컬렉션 이름
        String collectionName = getCollectionName();

        // 기존 수집된 멜론Top100 수집한 컬렉션 삭제하기
        res = melonMapper.dropCollection(collectionName);

        return res;
    }

    /**
     * 멜론 노래 리스트 한번에 저장하기
     */
    @Override
    public List<MelonDTO> insertManyField() {
        List<MelonDTO> rList = null;    // 변경된 데이터 조회 결과

        // 생성할 컬렉션명
        String collectionName = getCollectionName();

        // MongoDB 에 데이터 저장하기
        if (melonMapper.insertManyField(collectionName, this.doCollect()) == 1) {
            // 변경된 값을 확인하기 위해 MongoDB 로부터 데이터 조회하기
            rList = melonMapper.getSongList(collectionName);
        }

        return rList;
    }

    private String getCollectionName() {
        return "MELON_" + LocalDate.now();
    }
}
