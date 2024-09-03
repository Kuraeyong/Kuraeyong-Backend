package kuraeyong.backend.controller;

import kuraeyong.backend.domain.MetroPath;
import kuraeyong.backend.dto.MoveInfo;
import kuraeyong.backend.dto.request.PostPathSearchRequest;
import kuraeyong.backend.service.PathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paths")
public class PathController {

    private final PathService pathService;

    /**
     * 환승역 번호   0~268 (총 환승역 269개)
     */
    @PostMapping("")
    public String searchPath(@RequestBody PostPathSearchRequest postPathSearchRequest) {
        return pathService.searchPath(postPathSearchRequest.getOrgStinNm(), postPathSearchRequest.getDestStinNm(),
                postPathSearchRequest.getDateType(), postPathSearchRequest.getHour(), postPathSearchRequest.getMin());
    }
}
