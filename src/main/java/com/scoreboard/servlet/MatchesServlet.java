package com.scoreboard.servlet;

import com.scoreboard.dto.MatchesPageDto;
import com.scoreboard.service.MatchesPageService;
import com.scoreboard.validation.PlayerNameValidator;
import com.scoreboard.validation.result.SingleValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/matches")
@Slf4j
public class MatchesServlet extends BaseServlet {
    private static final String JSP_NAME = "matches";
    private static final String PLAYER_NAME = "playerName";
    private static final String PAGE_ATTR = "page";
    private static final String ERRORS_ATTR = "errors";
    private static final int DEFAULT_MATCHES_PER_PAGE = 10;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int MIN_NAME_LENGTH = 1;

    private MatchesPageService matchesPageService;
    private PlayerNameValidator validator;

    @Override
    public void init() throws ServletException {
        super.init();
        this.matchesPageService = getService(MatchesPageService.class);
        this.validator = getService(PlayerNameValidator.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String playerName = req.getParameter(PLAYER_NAME);
        int pageNumber = resolve(req.getParameter(PAGE_ATTR));

        req.setAttribute(PLAYER_NAME, playerName);

        MatchesPageDto matchesPage;

        if (StringUtils.isBlank(playerName)) {
            matchesPage = matchesPageService.getAllMatchesPage(pageNumber, DEFAULT_MATCHES_PER_PAGE);
        } else {
            SingleValidationResult result = validator.validate(playerName, MIN_NAME_LENGTH);

            if (result.hasErrors()) {
                req.setAttribute(ERRORS_ATTR, result.errors());
                matchesPage = createEmptyPage(pageNumber);
            } else {
                matchesPage = matchesPageService.getMatchesPageFiltered(playerName, pageNumber, DEFAULT_MATCHES_PER_PAGE);
            }
        }

        req.setAttribute(PAGE_ATTR, matchesPage);
        forwardTo(JSP_NAME, req, resp);
    }

    private MatchesPageDto createEmptyPage(int pageNumber) {
        return MatchesPageDto.builder()
                .matches(List.of())
                .totalPages(0)
                .pageNumber(pageNumber)
                .build();
    }

    private int resolve(String pageNumberStr) {
        if (StringUtils.isBlank(pageNumberStr)) {
            return DEFAULT_PAGE_NUMBER;
        }

        int pageNumber = Integer.parseInt(pageNumberStr);

        if (pageNumber < 0) {
            throw new NumberFormatException("Invalid page number: must be digit > 0, got '%s'".formatted(pageNumber));
        }

        return pageNumber;
    }
}