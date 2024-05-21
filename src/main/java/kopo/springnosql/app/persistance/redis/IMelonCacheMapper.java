package kopo.springnosql.app.persistance.redis;

import kopo.springnosql.app.dto.MelonDTO;

import java.util.List;

public interface IMelonCacheMapper {
    /**
     * 멜론 노래 리스트 저장하기
     *
     * @param pList 저장할 데이터
     * @param redisKey 저장할 키
     * @return 저장 결과
     */
    int insertSong(List<MelonDTO> pList, String redisKey);

    /**
     * 멜론 노래 키 정보 존재여부 체크하기
     *
     * @param redisKey 저장된 키 이름
     * @return key 존재 여부
     */
    boolean getExistKey(String redisKey);

    /**
     * 오늘 수집된 멜론 노래리스트 가져오기
     *
     * @param redisKey 저장된 키 이름
     * @return 노래 리스트
     */
    List<MelonDTO> getSongList(String redisKey);
}
