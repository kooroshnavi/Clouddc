package ir.tic.clouddc.rpc.token;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TokenService {
    boolean hasToken();

    AuthenticationToken getToken(Integer tokenId);

    List<RequestRecord> getRequestRecordHistoryList(AuthenticationToken token);

    void revokeToken();

    interface ValidIdTokenProjection {
        int getId();

        String getToken();
    }

    void register();

    void postRequestRecord(HttpServletRequest request, String status);

    List<AuthenticationToken> getTokenList();
}
