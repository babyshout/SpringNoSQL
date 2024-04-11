package kopo.springnosql.app.persistance.mongodb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import kopo.springnosql.app.dto.MongoDTO;
import kopo.springnosql.app.persistance.mongodb.AbstractMongoDBCommon;
import kopo.springnosql.app.persistance.mongodb.IMongoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class MongoMapper extends AbstractMongoDBCommon implements IMongoMapper {
    private final MongoTemplate mongodb;
    /**
     * 간단한 데이터 저장하기
     *
     * @param pDTO           저장될 정보
     * @param collectionName 저장할 컬렉션 이름
     * @return 저장 결과
     */
    @Override
    public int insertData(MongoDTO pDTO, String collectionName) {
        log.info(this.getClass().getName() + ".insertData START!!!!");

        int res = 0;

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, collectionName);

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        // DTO 를 Map 데이터타입으로 변경한 뒤, 변경된 Map 데이터타입을 Document 로 변경하기
        collection.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));

        res = 1;

        log.info(this.getClass().getName() + ".insertData START!!!!");

        return res;
    }
}
