package ir.tic.clouddc.api.token;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TokenService {
    boolean hasToken();

    AuthenticationToken getToken(Integer tokenId);

    List<RequestRecord> getRequestRecordHistoryList(AuthenticationToken token);

    void revokeToken();

    boolean revokePersonToken(Integer personId);

    interface ValidIdTokenProjection {
        int getId();

        String getToken();
    }

    void register();

    void postRequestRecord(HttpServletRequest request, String status);

    List<AuthenticationToken> getTokenList();
}
