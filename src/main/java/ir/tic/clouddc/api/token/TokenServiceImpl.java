package ir.tic.clouddc.api.token;

import ir.tic.clouddc.person.PersonService;
import ir.tic.clouddc.utils.UtilService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.text.RandomStringGenerator;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    private final RequestRecordRepository requestRecordRepository;

    private final PersonService personService;

    private static List<ValidIdTokenProjection> validTokens = new ArrayList<>();

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository, RequestRecordRepository requestRecordRepository, PersonService personService) {
        this.tokenRepository = tokenRepository;
        this.requestRecordRepository = requestRecordRepository;
        this.personService = personService;
    }

    @Override
    public boolean hasToken() {
        return tokenRepository.existsByValidAndPersonUsername(true, personService.getCurrentUsername());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'WEBSERVICE')")
    public AuthenticationToken getToken(Integer tokenId) {
        AuthenticationToken authenticationToken;
        authenticationToken = tokenRepository.getReferenceById(tokenId);
        var registerDate = authenticationToken.getRegisterDate().toLocalDate();
        var registerTime = authenticationToken.getRegisterDate().toLocalTime();
        authenticationToken.setPersianRegisterDate(UtilService.getFormattedPersianDateAndTime(registerDate, registerTime));
        authenticationToken.setPersianExpiryDate(UtilService.getFormattedPersianDate(authenticationToken.getExpiryDate()));

        return authenticationToken;
    }

    @Override
    @PreAuthorize("#token.person.username == authentication.name || hasAnyAuthority('ADMIN')")
    public List<RequestRecord> getRequestRecordHistoryList(@Param("token") AuthenticationToken token) {
        var requestList = Hibernate.unproxy(token, AuthenticationToken.class).getRequestRecords();
        if (!requestList.isEmpty()) {
            requestList.forEach(requestRecord -> {
                var date = requestRecord.getLocalDateTime().toLocalDate();
                var time = requestRecord.getLocalDateTime().toLocalTime();
                requestRecord.setPersianDateTime(UtilService.getFormattedPersianDateAndTime(date, time));
            });
        }
        return requestList;
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'WEBSERVICE')")
    public void revokeToken() {
        var username = personService.getCurrentUsername();
        tokenRepository.revokeToken(username, false, UtilService.getDATE());
        updateActiveTokenList();
    }


    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public boolean revokePersonToken(Integer personId) {
        var person = personService.getReferencedPerson(personId);
        if (tokenRepository.existsByValidAndPersonUsername(true, person.getUsername())) {
            tokenRepository.revokeToken(person.getUsername(), false, UtilService.getDATE());
            updateActiveTokenList();

            return true;
        }

        return false;
    }

    @Override
    public List<AuthenticationToken> getPersonTokenList(List<AuthenticationToken> fullList, boolean valid) {
        var currentUsername = personService.getCurrentUsername();

        return fullList
                .stream()
                .filter(authenticationToken -> authenticationToken.isValid() == valid)
                .filter(authenticationToken -> !authenticationToken.getPerson().getUsername().equals(currentUsername))
                .sorted(Comparator.comparing(authenticationToken -> authenticationToken.getPerson().getId()))
                .toList();
    }

    private void updateActiveTokenList() {
        validTokens = tokenRepository.getValidTokens();
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'WEBSERVICE')")
    public void register() {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        authenticationToken.setToken(new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .get()
                .generate(10));
        authenticationToken.setValid(true);
        authenticationToken.setExpiryDate(UtilService.getDATE().plusDays(100));
        authenticationToken.setRegisterDate(LocalDateTime.now());
        authenticationToken.setPerson(personService.getCurrentPerson());
        tokenRepository.save(authenticationToken);

        updateActiveTokenList();
    }

    @Override
    public void postRequestRecord(HttpServletRequest request, String status) {
        RequestRecord record = new RequestRecord();
        record.setAuthenticationToken(tokenRepository.getReferenceById(Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString())));
        record.setLocalDateTime(LocalDateTime.now());
        record.setRemoteAddress(request.getRemoteAddr());
        record.setSuccessful(status.equals("OK"));

        requestRecordRepository.save(record);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'WEBSERVICE')")
    public List<AuthenticationToken> getTokenList() {
        var currentUsername = personService.getCurrentUsername();
        List<AuthenticationToken> tokenList;
        if (personService.hasAdminAuthority()) {
            tokenList = tokenRepository.fetchAll();
        } else {
            tokenList = tokenRepository.getPersonTokenList(currentUsername);
        }

        if (!tokenList.isEmpty()) {
            tokenList.forEach(authenticationToken -> {
                var registerDate = authenticationToken.getRegisterDate().toLocalDate();
                var registerTime = authenticationToken.getRegisterDate().toLocalTime();
                authenticationToken.setPersianRegisterDate(UtilService.getFormattedPersianDateAndTime(registerDate, registerTime));
                authenticationToken.setPersianExpiryDate(UtilService.getFormattedPersianDate(authenticationToken.getExpiryDate()));
            });
        }

        return tokenList;
    }

    public static int isValidToken(String providedToken) {
        var validToken = validTokens
                .stream()
                .filter(token -> token.getToken().equals(providedToken))
                .findFirst();
        return validToken.map(ValidIdTokenProjection::getId).orElse(-1);
    }

    @PostConstruct
    private void setValidTokens() {
        validTokens.addAll(tokenRepository.getValidTokens());
    }
}
