package kuraeyong.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostPathSearchRequest {
    private String orgRailOprIsttCd;
    private String orgLnCd;
    private String orgStinCd;
    private String destRailOprIsttCd;
    private String destLnCd;
    private String destStinCd;

    @Override
    public String toString() {
        return "PostPathSearchRequest{" +
                "orgRailOprIsttCd='" + orgRailOprIsttCd + '\'' +
                ", orgLnCd='" + orgLnCd + '\'' +
                ", orgStinCd='" + orgStinCd + '\'' +
                ", destRailOprIsttCd='" + destRailOprIsttCd + '\'' +
                ", destLnCd='" + destLnCd + '\'' +
                ", destStinCd='" + destStinCd + '\'' +
                '}';
    }
}
