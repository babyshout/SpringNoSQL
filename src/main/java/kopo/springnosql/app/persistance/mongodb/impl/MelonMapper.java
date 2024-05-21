package kopo.springnosql.app.persistance.mongodb.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.springnosql.app.persistance.mongodb.AbstractMongoDBCommon;
import kopo.springnosql.app.dto.MelonDTO;
import kopo.springnosql.app.persistance.mongodb.IMelonMapper;
import kopo.springnosql.common.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.schema.TypedJsonSchemaObject;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mongodb.client.model.Updates.set;


@Slf4j
@Component
@RequiredArgsConstructor
public class MelonMapper extends AbstractMongoDBCommon implements IMelonMapper {

    private final MongoTemplate mongodb;

    /**
     * 멜론 노래 리스트 저장하기
     *
     * @param pList          저장될 정보
     * @param collectionName 저장할 컬렉션 이름
     * @return 저장결과
     */
    @Override
    public int insertSong(List<MelonDTO> pList, String collectionName) {
        log.info(this.getClass().getName() + ".insertSong START!!");

        int res = 0;

        if (pList == null) {
            log.warn("parameter is NULL!");
            pList = new LinkedList<>();
        }

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, collectionName, "collectionTime");

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(collectionName);

        for (MelonDTO pDTO : pList) {
            // 레코드 한개씩 저장하기
            col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));

        }

        res = 1;


        log.info(this.getClass().getName() + ".insertSong END!!");


        return res;
    }

    /**
     * 오늘 수집한 멜론 노래리스트 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getSongList(String collectionName) {
        log.info(this.getClass().getName() + ".getSongList START!!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);
        log.trace("collection : " + collection.toString());

        // 조회 결과 중 출력할 컬럼들(SQL 의 SELECT 절과 FROM 절 가운데 컬럼들과 유사함
        // SELECT "여기" FROM USERINFO
        Document projection = new Document();

        projection.append("song", "$song");
        projection.append("singer", "$singer");

        // MongoDB 는 무조건 ObjectId 가 자동 생성되며, ObjectID 는 사용하지 않을때, 조회할 필요가 없음..
        // ObjectId 를 가지고 오지 않을때 사용함..
        projection.append("_id", 0);

        log.trace("projection : " + projection);

        // MongoDB 의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find 를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        // FIXME find() 에 document 넣기
        FindIterable<Document> rs = collection.find().projection(projection);

        FindIterable<Document> myRs = collection.find();

        for (Document doc : myRs) {
//            log.trace("myRs : " + myRs);
        }


        for (Document doc : rs) {
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

//            log.info("song : " + song + "/ singer : " + singer);

            MelonDTO rDTO = MelonDTO.builder()
                    .song(song)
                    .singer(singer).build();

            // 레코드 결과를 List 에 저장하기
            rList.add(rDTO);

        }


        log.info(this.getClass().getName() + ".getSongList END!!");

        return rList;
    }

    /**
     * 가수별 수집된 노래의 수 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @return 노래 리스트 {@link MelonDTO}
     */
    @Override
    public List<MelonDTO> getSingerSongCount(String collectionName) {
        log.info(this.getClass().getName() + ".getSingerSongCount START!!!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        // MongoDB 조회 쿼리
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().append("$group",
                        new Document().append("_id", new Document().append("singer", "$singer")).append("COUNT(singer)",
                                new Document().append("$sum", 1))),
                new Document().append(
                        "$project",
                        new Document()
                                .append(
                                        "singer",
                                        "$_id.singer")
                                .append(
                                        "singerCount",
                                        "$COUNT(singer)"
                                )
                ),
                new Document().append("$sort",
                        new Document().append("singerCount", -1)
                )
        );

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);
        AggregateIterable<Document> rs = collection.aggregate(pipeline).allowDiskUse(true);

        for (Document doc : rs) {
            String singer = doc.getString("singer");
            int singerCount = doc.getInteger("singerCount", 0);

            log.debug("singer : " + singer + "/ singerCount : " + singerCount);

            MelonDTO rDTO = MelonDTO.builder()
                    .singer(singer)
                    .singerCount(singerCount).build();

            log.debug("rDTO : " + rDTO);

            rList.add(rDTO);

        }

        log.info(this.getClass().getName() + ".getSingerSongCount END!!!");
        return rList;

    }

    /**
     * 가수 이름으로 조회하기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO           가수명
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getSingerSong(String collectionName, MelonDTO pDTO) {
        log.info(this.getClass().getName() + ".getSingerSong START!!!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        // 조회할 조건(SQL의 SELECT 역할 / SELECT song, singer FROM MELON_20220321 WHERE singer = '방탄소년단')
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.singer()));

        log.trace("query : " + query);

        // 조회 결과 중 출력할 컬럼들 (SQL 의 SELECT 와 FROM 절 가운데 컬럼들과 유사함
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");


        // MongoDB 는 무조건 ObjectID 가 자동생성되며 ObjectID 는 사용하지 않을때, 조회할 필요가 없음
        // ObjectID 를 가지고 오지 않을때 사용함
        projection.append("_id", 0);

        log.trace("projection : " + projection);

        // MongoDB 의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find 를 사용하고, 데이터양이 많은경우 무조건 Aggregate 사용한다!!
        FindIterable<? extends Document> rs = collection.find(query).projection(projection);

        for (Document doc : rs) {
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

            MelonDTO rDTO = MelonDTO.builder()
                    .song(song)
                    .singer(singer).build();

            log.trace("rDTO : " + rDTO);
            // 레코드 결과를 List 에 저장하기
            rList.add(rDTO);
        }

        log.info(this.getClass().getName() + ".getSingerSong END!!!!");

        return rList;
    }

    /**
     * 컬렉션 삭제하기
     *
     * @param collectionName 삭제할 컬렉션 이름
     * @return 저장 결과
     */
    @Override
    public int dropCollection(String collectionName) {

        int res = 0;

        super.dropCollection(mongodb, collectionName);

        res = 1;

        return res;
    }

    /**
     * MongoDB insertMany 함수를 통해 멜론차트 저장하기
     * 한건 insert 대비 속도가 빠름
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param pList          저장될 정보
     * @return 저장 결과
     */
    @Override
    public int insertManyField(String collectionName, List<MelonDTO> pList) {

        int res = 0;

        if (pList == null) {
            pList = new LinkedList<>();
        }

        pList.parallelStream().limit(5).forEach(value -> log.trace("pList head 5: " + value));

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, collectionName, "collectTime");

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(collectionName);

        List<Document> list = new ArrayList<>();

        // 람다식 활용하여 병렬처리 (순서 상관없이 저장) parallelStream 과 -> 사용
        pList.parallelStream().forEach(melon -> list.add(new Document(new ObjectMapper().convertValue(melon, Map.class))));

        // ObjectMapper 이용한 List<MelonDTO> -> MeList<Document> 변경하기
//        List<Document> list = new ObjectMapper().convertValue(pList,
//                new TypeReference<List<Document>>() {
//                });

        col.insertMany(list);

        res = 1;

        return res;
    }

    /**
     * 필드 값 수정하기
     * 예 : 가수의 이름 수정하기
     * <a href='www.naver.com'>asdf</a>
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param pDTO           수정할 가수 이름, 수정될 가수 이름 정보
     * @return 저장 결과
     */
    @Override
    public int updateField(String collectionName, MelonDTO pDTO) {
        int res = 0;

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        String singer = CmmUtil.nvl(pDTO.singer());
        String updateSinger = CmmUtil.nvl(pDTO.updateSinger());

        log.trace("pCollectionName" + collectionName);
        log.trace("singer : " + singer);
        log.trace("updateSinger : " + updateSinger);

        // 조회할 조건(SQL 의 where 역할 / SELECT * FROM MELON_20220321 where singer = '방탄소년단'
        Document query = new Document();
        query.append("singer", singer);

        // MongoDB 데이터 수정은 반드시 컬렉션을 조회하고, 조회된 ObjectID 를 기반으로 데이터를 수정함
        // MongoDB 환경은 분산환경(Sharding) 으로 구성될 수 있기 때문에 정확한 PK 에 매핑하기 위해서임
        FindIterable<Document> rs = collection.find(query);

        // 람다식 활용하여 컬렉션에 조회된 데이터들을 수정하기
        rs.forEach(doc -> collection.updateOne(
                doc,
                new Document("$set", new Document("singer", updateSinger))
        ));

        res = 1;

        return res;
    }

    /**
     * 수정된 가수이름의 노래 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO           가수명
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getUpdateSinger(String collectionName, MelonDTO pDTO) {

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        // 조회할 조건(SQL 의 WHERE 역할 / SELECT song, singer FROM MELON_yyMMdd where singer = '방탄소년단)
        Document query = new Document();
        query.append(
                "singer", CmmUtil.nvl(pDTO.updateSinger())
        );

        // 조회 결과 중 출력할 컬럼들(SQL 의 SELECT 절과 FROM 절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");

        // MongoDB 는 무조건 ObjectID 가 자동생성되며, ObjectId 는 사용하지 않을 때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을때 사용함
        projection.append("_id", 0);

        // MongoDB 의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find 를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = collection.find(query).projection(projection);

        for (Document doc : rs) {
            // MongoDB 조회 결과를 MelonDTO 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

            log.trace("song : " + song + "/ singer : " + singer);
            MelonDTO rDTO = MelonDTO.builder()
                    .song(song)
                    .singer(singer).build();

            // 레코드 결과를 List 에 저장하기
            rList.add(rDTO);
        }


        return rList;
    }

    /**
     * 필드 추가하기
     * 예 : 가수의 nickname 필드 추가하기
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param pDTO           추가를 위해 검색할 가수이름, 추가할 서브 가수이름
     * @return 저장 결과
     */
    @Override
    public int updateAddField(String collectionName, MelonDTO pDTO) {
        int res = 0;

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        String singer = CmmUtil.nvl(pDTO.singer());
        String nickname = CmmUtil.nvl(pDTO.nickname());

        log.trace(pDTO.toString());

        // 조회할 조건(SQL 의 where 역할 / SELECT * FROM MELON_yyMMdd where singer = '방탄소년단'
        Document query = new Document();
        query.append("singer", singer);

        // MongoDB 데이터 삭제는 반드시 컬렉션을 조회하고, 조회된 ObjectId 를 기반으로 데이터를 삭제함
        // MongoDB 환경은 분산환경(Sharding) 으로 구성될 수 있기 때문에 정확한 PK 에 매핑하기 위해서임
        FindIterable<Document> rs = collection.find(query);

        // 람다식 활용하여 컬렉션에 조회된 데이터들을 수정하기
        // MongoDB Driver 는 MongoDB 의 "$set" 함수를 대신할 자바 함수를 구현함
        rs.forEach(doc -> collection.updateOne(doc, set("nickname", nickname)));

        res = 1;

        return res;
    }

    /**
     * 가수의 노래 가져오기(Nickname 포함 조회)
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO           가수명
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getSingerSongNickname(String collectionName, MelonDTO pDTO) {

        // 조회결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> mongodbCollection = mongodb.getCollection(collectionName);

        // 조회할 조건 (SQL 의 where 역할 / select song, singer from melon_20220321 where singer = '방탄소년단'
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.singer()));

        // 조회결과 중 출력할 컬럼들 (SQL 의 SELECT 절과 FROM 절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");
        projection.append("nickname", "$nickname");

        // MongoDB 는 무조건 ObjectId 가 자동생성 되며, ObjectId 는 사용하지 않을때, 조회할 필요가 없음 (성능 문제)
        // ObjectId 를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB 의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find 를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        log.trace("query : " + query);
        log.trace("projection : " + projection.toString());

        FindIterable<Document> documents = mongodbCollection
                .find(query)
                .projection(projection);

        for (Document doc : documents) {
            // MongoDB 조회 결과를 MelonDTO 에 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));
            String nickname = CmmUtil.nvl(doc.getString("nickname"));

            MelonDTO rDTO = MelonDTO.builder()
                    .song(song)
                    .singer(singer)
                    .nickname(nickname).build();

            rList.add(rDTO);
        }
        rList.parallelStream().limit(5).forEach(melonDTO -> log.trace("find results : " + melonDTO));

        return rList;
    }

    /**
     * 가수의 List 구조의 member 필드 추가하기
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param pDTO           추가를 위해 검색할 가수이름, 추가할 멤버 이름
     * @return 저장 결과
     */
    @Override
    public int updateAddListField(String collectionName, MelonDTO pDTO) {
        int res = 0;

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        String singer = CmmUtil.nvl(pDTO.singer());
        List<String> member = pDTO.member();

        log.trace("singer : " + singer);
        log.trace("member : " + member);

        // 조회할 조건(SQL 의 WHERE 역할 / SELECT * FROM MELON_20220321 where singer = '방탄소년단'
        Document query = new Document();
        query.append("singer", singer);

        // MongoDB 데이터 삭제는 반드시 컬렉션을 조회하고, 조회된 ObjectID 를 기반으로 데이터를 삭제함
        // MongoDB 환경은 분산환경(Sharding) 으로 구성될 수 있기 때문에, 정확한 PK 에 매핑하기 위해서임
        FindIterable<Document> resultDocuments = collection.find(query);

        // 람다식 활용하여 컬렉션에 조회된 데이터들을 수정하기
        // List 구조는 String 구조와 동일하게 set 에 List 객체를 저장하면 된다.
        // MongoDB 의 저장단위는 Document 객체는 자바의 Map 을 상속받아 구현한 것이며, Map 특징인 값은 모든 객체가 저장 가능하다.
        resultDocuments.forEach(doc -> collection.updateOne(doc, set("member", member)));

        res = 1;

        return res;
    }

    /**
     * 가수의 노래 가져오기
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO           가수명
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getSingerSongMember(String collectionName, MelonDTO pDTO) {

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> collection = mongodb.getCollection(collectionName);

        // 조회할 조건(SQL 의 WHERE 역할
        Document query = new Document();
        query.append("singer", CmmUtil.nvl(pDTO.singer()));

        // 조회 결과 중 출력할 컬럼들(SQL 의 SELECT 절과 FROM 절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");
        projection.append("member", "$member");

        // MongoDB 는 무조건 ObjectId 가 자동생성되며, ObjectId 는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId 를 가지고오지 않을때 사용함
        projection.append("_id", 0);

        // MongoDB 의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find 를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다
        FindIterable<Document> resultDocuments = collection.find(query).projection(projection);

        for (Document doc : resultDocuments) {
            // MongoDB 조회 결과를 MelonDTO 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));
            List<String> member = doc.getList("member", String.class, new ArrayList<>());


            MelonDTO rDTO = MelonDTO.builder()
                    .song(song)
                    .singer(singer)
                    .member(member).build();

            rList.add(rDTO);
        }

        rList.parallelStream().limit(5).forEach(
                melonDTO -> log.trace(melonDTO.toString())
        );

        return rList;
    }

    /**
     * 가수이름, 노래제목 수정 및 신규필드 추가(복합 수정하기)
     *
     * @param collectionName 저장할 콜렉션 이름
     * @param pDTO           저장할 가수이름, 수정될 노래제목, 추가 필드값
     * @return 저장 결과
     */
    @Override
    public int updateFieldAndAddField(String collectionName, MelonDTO pDTO) {
        int res = 0;

        MongoCollection<Document> col = mongodb.getCollection(collectionName);

        String singer = CmmUtil.nvl(pDTO.singer());
        String updateSinger = CmmUtil.nvl(pDTO.updateSinger());
        String addFieldValue = CmmUtil.nvl(pDTO.addFieldValue());

        log.info("pColNm : " + collectionName);
        log.info("pDTO : " + pDTO);

        // 조회할 조건(SQL 의 WHERE 역할 / SELECT * FROM MELON_20220321 where singer = '방탄소년단'
        Document query = new Document();
        query.append("singer", singer);

        // MongoDB 데이터 삭제는 반드시 컬렉션을 조회하고, 조회된 ObjectId 를 기반으로 데이터를 삭제함
        // MongoDB 환경은 분산환경(Sharding) 으로 구성될수 있기 때문에, 정확한 PK 에 매핑하기 위해서임
        FindIterable<Document> rs = col.find(query);

        // 한줄로 append 에서 수정할 필드 추가해도 되지만, 가독성이 떨어져 줄마다 append 함
        Document updateDoc = new Document();
        updateDoc.append("singer", updateSinger);   // 기존 필드 수정
        updateDoc.append("addData", addFieldValue); // 신규 필드 추가

        rs.forEach(doc -> col.updateOne(doc, new Document("$set", updateDoc)));

        res = 1;

        return res;
    }

    /**
     * 가수의 노래 가져오기(임의 추가한 필드 포함 조회)
     *
     * @param collectionName 조회할 컬렉션 이름
     * @param pDTO           가수명
     * @return 노래 리스트
     */
    @Override
    public List<MelonDTO> getSingerSongAddData(String collectionName, MelonDTO pDTO) {

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<MelonDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(collectionName);

        // 조회할 조건(SQL 의 where 역할)
        Document query = new Document();
        // 이전 실행에서 가수이름이 변경되어 변경시킨 값으로 적용
        query.append("singer", CmmUtil.nvl(pDTO.updateSinger()));

        // 조회 결과 중 출력할 컬럼들(SQL의 SELECT 절과 from 절 가운데 컬럼들과 유사)
        Document projection = new Document();
        projection.append("song", "$song");
        projection.append("singer", "$singer");
        projection.append("addData", "$addData");

        // MongoDB 는 무조건 ObjectId 가 자동생성되며, ObjectId 는 사용하지 않을때, 조회할 필요가 없음
        projection.append("_id", 0);

        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {
            // MongoDB 조회 결과를 MelonDTO 에 저장하기 위해 변수에 저장
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));
            String addData = CmmUtil.nvl(doc.getString("addData"));

            MelonDTO rDTO = MelonDTO.builder()
                    .song(song)
                    .singer(singer)
                    .addFieldValue(addData).build();

            rList.add(rDTO);

            log.info("rDTO : " + rDTO);
        }
        return rList;
    }

    /**
     * 가수의 노래 삭제하기
     *
     * @param collectionName 저장할 컬렉션 이름
     * @param pDTO           삭제할 가수 이름
     * @return 저장 결과
     */
    @Override
    public int deleteDocument(String collectionName, MelonDTO pDTO) {
        int res = 0;

        MongoCollection<Document> col = mongodb.getCollection(collectionName);

        String singer = CmmUtil.nvl(pDTO.singer());

        log.info("pColNm : " + collectionName);
        log.info("pDTO : " + pDTO);

        // 조회할 조건(SQL 의 WHERE 역할)
        Document query = new Document();
        query.append("singer", singer);

        // MongoDB 데이터 삭제는 반드시 컬렉션을 조회하고, 조회된 ObjectId 를 기반으로 데이터를 삭제함
        // MongoDB 환경은 분산환경(Sharding) 으로 구성될수 있기 때문에 정확한 PK 에 매핑하기 위해서임
        FindIterable<Document> rs = col.find(query);

        // 람다식 활용하여 데이터 삭제하기
        // 전체 컬렉션에 있는 데이터들을 삭제하기
        rs.forEach(col::deleteOne); // col 객체에 자동으로 매칭되어 실행될 함수 정의
//        rs.forEach(doc -> col.deleteOne(doc));  // rs.forEach(col::deleteOne);  동일한 문법

        res = 1;

        return res;
    }
}
