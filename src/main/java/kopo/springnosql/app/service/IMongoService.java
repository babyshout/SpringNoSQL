package kopo.springnosql.app.service;

import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.dto.MongoDTO;

import java.util.List;

public interface IMongoService {
    /**
     * 간단한 데이터 저장하기
     * @param pDTO MongoDTO 받아와서 사용
     * @return 성공1, 실패0
     */
    int mongoTest(MongoDTO pDTO);


}
