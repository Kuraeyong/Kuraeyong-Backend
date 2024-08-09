package kuraeyong.backend.controller;

import kuraeyong.backend.service.EdgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/edges")
public class EdgeController {

    private final EdgeService edgeService;

    @GetMapping("/init-db")
    public String initEdgeInfoDB() {
        return edgeService.createEdgeInfoDB();
    }
}
