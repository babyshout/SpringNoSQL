package kopo.springnosql.app.persistance.mongodb;

import kopo.springnosql.app.dto.MelonDTO;

import java.util.List;

public interface IMelonMapper {

    /**
     * 멜론 노래 리스트 저장하기
     *
     * @param pList 저장될 정보
     * @param collectionName 저장할 컬렉션 이름
     * @return 저장결과
     */
    int insertSong(List<MelonDTO> pList, String collectionName);

    /**
     * 오늘 수집한 멜론 노래리스트 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @return 노래 리스트
     */
    List<MelonDTO> getSongList(String collectionName);

    /**
     * 가수별 수집된 노래의 수 가져오기
     *
     * @param CollectionName 조회할 컬렉션 이름
     * @return 노래 리스트 {@link MelonDTO}
     *
     */
    List<MelonDTO> getSingerSongCount(String CollectionName);

    /**
     * 가수 이름으로 조회하기
     * 
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO 가수명
     * @return 노래 리스트
     */
    List<MelonDTO> getSingerSong(String collectionName, MelonDTO pDTO);

    /**
     * 컬렉션 삭제하기
     *
     * @param collectionName 삭제할 컬렉션 이름
     * @return 저장 결과
     */
    int dropCollection(String collectionName);

    /**
     * MongoDB insertMany 함수를 통해 멜론차트 저장하기
     * 한건 insert 대비 속도가 빠름
     * 기존 collection 삭제함!!
     *
     * @param CollectionName 저장할 컬렉션 이름
     * @param pList 저장될 정보
     * @return 저장 결과
     */
    int insertManyField(String CollectionName, List<MelonDTO> pList);

    /**
     * 필드 값 수정하기
     * 예 : 가수의 이름 수정하기
     * <a href='www.naver.com'>asdf</a>
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param  pDTO 수정할 가수 이름, 수정될 가수 이름 정보
     * @return 저장 결과
     */
    int updateField(String collectionName, MelonDTO pDTO);

    /**
     * 수정된 가수이름의 노래 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO 가수명
     * @return 노래 리스트
     */
    List<MelonDTO> getUpdateSinger(String collectionName, MelonDTO pDTO);

    /**
     * 필드 추가하기
     * 예 : 가수의 nickname 필드 추가하기
     * @param collectionName 저장할 컬렉션 이름
     * @param pDTO 추가를 위해 검색할 가수이름, 추가할 서브 가수이름
     * @return 저장 결과
     */
    int updateAddField(String collectionName, MelonDTO pDTO);

    /**
     * 가수의 노래 가져오기(Nickname 포함 조회)
     *
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO 가수명
     * @return 노래 리스트
     */
    List<MelonDTO> getSingerSongNickname(String collectionName, MelonDTO pDTO);

    /**
     * 가수의 List 구조의 member 필드 추가하기
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param pDTO 추가를 위해 검색할 가수이름, 추가할 멤버 이름
     * @return 저장 결과
     */
    int updateAddListField(String collectionName, MelonDTO pDTO);

    /**
     * 가수의 노래 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO 가수명
     * @return 노래 리스트
     */
    List<MelonDTO> getSingerSongMember(String collectionName, MelonDTO pDTO);
}
