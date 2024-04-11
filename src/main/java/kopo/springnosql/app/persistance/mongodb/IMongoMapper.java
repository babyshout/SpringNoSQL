package kopo.springnosql.app.persistance.mongodb;

import kopo.springnosql.app.dto.MongoDTO;

public interface IMongoMapper {

    /**
     * 간단한 데이터 저장하기
     *
     * @param pDto 저장될 정보
     * @param collectionName 저장할 컬렉션 이름
     * @return 저장 결과
     */
    int insertData(MongoDTO pDto, String collectionName);

}
