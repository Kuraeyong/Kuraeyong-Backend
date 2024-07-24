package kuraeyong.backend.dao;

import kuraeyong.backend.dto.GetListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@Repository
public class StationDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StationDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public GetListResponse getLineNameListByStationName(String stationName) {
        log.info("[StationDao.getLineNameListByStationName]");

        String sql = "select line from station where name=:name";
        Map<String, Object> param = Map.of("name", stationName);

        List<String> list = jdbcTemplate.query(sql, param, (rs, count) -> rs.getString("line"));

        return new GetListResponse(list);
    }
}
