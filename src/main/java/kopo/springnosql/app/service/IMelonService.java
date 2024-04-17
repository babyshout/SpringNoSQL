package kopo.springnosql.app.service;

import kopo.springnosql.app.dto.MelonDTO;

import java.util.List;

public interface IMelonService {

    /**
     * 멜론 노래 리스트 저장하기
     */
    int collectMelonSong();

    /**
     * 오늘 수집된 멜론 노래리스트 가져오기
     */
    List<MelonDTO> getSongList();

    /**
     * 멜론 가수별 노래 수 가져오기
     * @return
     */
    List<MelonDTO> getSingerSongCount();

    /**
     * 가수의 노래 가져오기
     * @param pDTO singer 가 담겨있는 pDTO
     * @return 결과 리스트
     */
    List<MelonDTO> getSingerSong(MelonDTO pDTO);

    /**
     * 수집된 멜론 차트 저장된 MongoDB 컬렉션 삭제하기
     */
    int dropCollection();

    /**
     * 멜론 노래 리스트 한번에 저장하기
     */
    List<MelonDTO> insertManyField();

    /**
     * singer 필드의 값인 방탄소년단을 BTS 로 변경하기
     */
    List<MelonDTO> updateField(MelonDTO pDTO);
}
