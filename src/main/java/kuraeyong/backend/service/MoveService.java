package kuraeyong.backend.service;

import kuraeyong.backend.domain.constant.DirectionType;
import kuraeyong.backend.domain.path.ActualPath;
import kuraeyong.backend.domain.path.MetroPath;
import kuraeyong.backend.domain.path.MoveInfo;
import kuraeyong.backend.domain.path.MoveInfos;
import kuraeyong.backend.domain.path.UserMoveInfo;
import kuraeyong.backend.domain.path.UserMoveInfos;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveService {

    public UserMoveInfos createUserMoveInfos(ActualPath actualPath, String stopoverStinNm, int stopoverTime) {
        // init
        MoveInfos moveInfos = actualPath.getMoveInfos();
        MetroPath compressedPath = actualPath.getCompressedPath();
        List<UserMoveInfo> userMoveInfos = new ArrayList<>();

        // create
        int firstMoveInfoIdxWithSameTrn = 1;
        MoveInfo firstMoveInfoWithSameTrn;
        for (int i = 2; i < moveInfos.size(); i++) {
            MoveInfo prev = moveInfos.get(i - 1);
            MoveInfo curr = moveInfos.get(i);

            if (curr.getTrnGroupNo() == prev.getTrnGroupNo()) {
                continue;
            }
            firstMoveInfoWithSameTrn = moveInfos.get(firstMoveInfoIdxWithSameTrn);
            userMoveInfos.add(UserMoveInfo.of(
                    prev.getLnCd(),
                    compressedPath.getStinNm(firstMoveInfoIdxWithSameTrn - 1),
                    compressedPath.getStinNm(i - 1),
                    firstMoveInfoWithSameTrn.getDptTm(),
                    prev.getArvTm(),
                    firstMoveInfoWithSameTrn.getTmnStinNm(),
                    DirectionType.get(firstMoveInfoWithSameTrn.getTrnNo())
            ));
            firstMoveInfoIdxWithSameTrn = i;    // update

            // 일반, 급행 환승인 경우
            if (curr.getTrnGroupNo() == -1 || prev.getTrnGroupNo() == -1) {
                continue;
            }
            userMoveInfos.add(UserMoveInfo.of(
                    null,
                    compressedPath.getStinNm(i - 1),
                    compressedPath.getStinNm(i - 1),
                    prev.getArvTm(),
                    prev.getArvTm(),
                    null,
                    null
            ));
        }
        // 마지막 UserMoveInfo 별도 추가
        firstMoveInfoWithSameTrn = moveInfos.get(firstMoveInfoIdxWithSameTrn);
        userMoveInfos.add(UserMoveInfo.of(
                moveInfos.get(moveInfos.size() - 1).getLnCd(),
                compressedPath.getStinNm(firstMoveInfoIdxWithSameTrn - 1),
                compressedPath.getStinNm(compressedPath.size() - 1),
                firstMoveInfoWithSameTrn.getDptTm(),
                moveInfos.get(moveInfos.size() - 1).getArvTm(),
                firstMoveInfoWithSameTrn.getTmnStinNm(),
                DirectionType.get(firstMoveInfoWithSameTrn.getTrnNo())
        ));
        return new UserMoveInfos(userMoveInfos, actualPath.getTotalTime(), actualPath.getCongestionScore(), stopoverStinNm, stopoverTime);
    }
}
