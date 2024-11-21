package kuraeyong.backend.manager.station;

import java.util.List;

public interface StationDBInitializer {
    boolean initDB(List<List<String>> rowList);
}
