package kopo.springnosql.app.persistance.mongodb;


import com.mongodb.client.model.Indexes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMongoDBCommon {

    /**
     * 컬렉션 생성
     *
     * @param mongodb        접속된 MongoDB
     * @param collectionName 생성할 컬렉션명
     * @return 생성결과
     */
    protected boolean createCollection(MongoTemplate mongodb, String collectionName) {
        boolean res;

        if (mongodb.collectionExists(collectionName)) {
            res = false;
        } else {
            mongodb.createCollection(collectionName);
            res = true;
        }

        return true;
    }

    /**
     * 인덱스 컬럼이 여러개 일때 컬렉션 생성
     *
     * @param mongodb        접속된 mongoDB
     * @param collectionName 생성할 컬렉션명
     * @param index          생성할 인덱스
     * @return 생성결과
     */
    protected boolean createCollection(MongoTemplate mongodb, String collectionName, String[] index) {
        log.info(this.getClass().getName() + ".createCollection START!!!");

        boolean res = false;

        // 기존에 등록된 컬렉션 이름이 존재하는지 체크하고, 컬렉션이 없는 경우 생성함
        if (!mongodb.collectionExists(collectionName)) {
            // 인덱스 값이 존재한다면..
            if (index.length > 0) {
                // 컬렉션 생성 및 인덱스 생성, MongoDB 에서 데이터 가져오는 방식에 맞게 인덱스는 반드시 생성하자!!
                // 데이터 양이 많지 않으면 문제되지 않으나, 최소 10만건 이상 데이터 저장시 속도가 약 10배 이상 발생함
                mongodb.createCollection(collectionName)
                        .createIndex(Indexes.ascending(index));
            } else {
                // 인덱스가 없으면 인덱스 없이 컬렉션 생성
                mongodb.createCollection(collectionName);
            }
            res = true;
        }

        log.info(this.getClass().getName() + ".createCollection END!!!");

        return res;
    }

    /**
     * 인덱스 컬럼이 한개일때 컬렉션 생성
     *
     * @param mongodb 접속된 mongoDB
     * @param collectionName 생성할 컬렉션명
     * @param index 생성할 인덱스
     * @return 생성결과
     */
    protected boolean createCollection(MongoTemplate mongodb, String collectionName, String index) {
        String[] indexArr = {index};

        return createCollection(mongodb, collectionName, indexArr);
    }

    /**
     * 컬렉션 삭제
     *
     * @param mongodb 접속된 MongoDB
     * @param collectionName 생성할 컬렉션명
     * @return 삭제결과
     */
    protected boolean dropCollection(MongoTemplate mongodb, String collectionName) {
        boolean res = false;

        if (mongodb.collectionExists(collectionName)) {
            mongodb.dropCollection(collectionName);
            res = true;
        }

        return res;
    }
}
