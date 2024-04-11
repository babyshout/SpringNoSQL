package kopo.springnosql.app.persistance.mongodb.impl;

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
import org.springframework.stereotype.Component;

import java.util.*;


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

        // 조회 결과 중 출력할 컬럼들(SQL 의 SELECT 절과 FROM 절 가운데 컬럼들과 유사함
        // SELECT "여기" FROM USERINFO
        Document projection = new Document();

        projection.append("song", "$song");
        projection.append("singer", "$singer");

        // MongoDB 는 무조건 ObjectId 가 자동 생성되며, ObjectID 는 사용하지 않을때, 조회할 필요가 없음..
        // ObjectId 를 가지고 오지 않을때 사용함..
        projection.append("_id", 0);

        // MongoDB 의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find 를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        // FIXME find() 에 document 넣기
        FindIterable<Document> rs = collection.find().projection(projection);

        for (Document doc : rs) {
            String song = CmmUtil.nvl(doc.getString("song"));
            String singer = CmmUtil.nvl(doc.getString("singer"));

            log.info("song : " + song + "/ singer : " + singer);

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

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, collectionName, "collectTime");

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(collectionName);

        List<Document> list = new ArrayList<>();
        // 람다식 활용 stream 과 -> 사용
        pList.parallelStream().forEach(melon ->
                list.add(new Document(new ObjectMapper().convertValue(melon, Map.class))));

        // 레코드 리스트 단위로 한번에 저장하기
        col.insertMany(list);

        res = 1;

        return res;
    }
}
