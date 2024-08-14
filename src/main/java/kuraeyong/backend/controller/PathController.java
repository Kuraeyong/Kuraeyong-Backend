package kuraeyong.backend.controller;

import kuraeyong.backend.service.EdgeService;
import kuraeyong.backend.service.PathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paths")
public class PathController {

    private final PathService pathService;

    /**
     * 환승역 번호   0~268
     */
    @GetMapping("")
    public String pathSearch() {
        pathService.pathSearch(148);
        return "successfully searched";
    }
}
