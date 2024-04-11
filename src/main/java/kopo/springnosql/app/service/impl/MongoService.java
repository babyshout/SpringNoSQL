package kopo.springnosql.app.service.impl;

import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.dto.MongoDTO;
import kopo.springnosql.app.persistance.mongodb.IMongoMapper;
import kopo.springnosql.app.service.IMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoService implements IMongoService {

    private final IMongoMapper mongoMapper; // MongoDB 에 저장할 Mapper

    /**
     * 간단한 데이터 저장하기
     *
     * @param pDTO MongoDTO 받아와서 사용
     * @return 성공1, 실패0
     */
    @Override
    public int mongoTest(MongoDTO pDTO) {
        log.info(this.getClass().getName() + ".mongoTest START!!!");

        // 생성할 컬렉션명
        String collectionName = "MONGODB_TEST";

        // MongoDB 에 데이터저장하기
        int res = mongoMapper.insertData(pDTO, collectionName);


        log.info(this.getClass().getName() + ".mongoTest END!!!");

        return res;
    }




}
