package com.scoreboard.service;

import com.scoreboard.entity.Match;
import com.scoreboard.persistence.dao.MatchDao;
import com.scoreboard.dto.MatchResponse.FinishedMatchDto;
import com.scoreboard.dto.MatchesPageDto;
import com.scoreboard.dto.PaginationParams;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.validation.PlayerNameValidator;
import com.scoreboard.validation.result.SingleValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.LongSupplier;

@Slf4j
@RequiredArgsConstructor
public class MatchesPageService {
    private static final String LOG_LOADING_ALL_START = "Loading all matches, page {} ({} per page)";
    private static final String LOG_LOADING_FILTERED_START = "Loading matches for player {} by page {}";
    private static final String LOG_LOADING_SUCCESS = "Loading success";
    private static final String LOG_LOADING_EMPTY_PAGE = "Loading empty page with errors";

    private static final int MIN_NAME_LENGTH = 1;

    private final MatchDao matchDao;
    private final PlayerNameValidator validator;
    private final FinishedMatchMapper finishedMatchMapper;
    private final BaseTransactionalService baseTransactionalService;

    public MatchesPageDto getMatchesPage(String playerName, int pageNumber, int matchesPerPage) {
        if (StringUtils.isBlank(playerName)) {
            return getAllMatchesPage(pageNumber, matchesPerPage);
        }

        SingleValidationResult result = validator.validate(playerName, MIN_NAME_LENGTH);

        if (result.hasErrors()) {
            return createPageWithErrors(pageNumber, result.errors());
        }

        return getMatchesPageFiltered(playerName, pageNumber, matchesPerPage);
    }

    private MatchesPageDto getAllMatchesPage(int pageNumber, int matchesPerPage) {
        log.info(LOG_LOADING_ALL_START, pageNumber, matchesPerPage);
        MatchesPageDto matchesPage = buildPage(pageNumber, matchesPerPage, matchDao::countTotal, matchDao::find);
        log.info(LOG_LOADING_SUCCESS);
        return matchesPage;
    }

    private MatchesPageDto createPageWithErrors(int pageNumber, List<String> errors) {
        log.info(LOG_LOADING_EMPTY_PAGE);
        return MatchesPageDto.builder()
                .pageNumber(pageNumber)
                .matches(List.of())
                .totalPages(0)
                .errors(errors)
                .build();
    }

    private MatchesPageDto getMatchesPageFiltered(String playerName, int pageNumber, int matchesPerPage) {
        log.info(LOG_LOADING_FILTERED_START, playerName, pageNumber);
        MatchesPageDto matchesPage = buildPage(pageNumber, matchesPerPage,
                () -> matchDao.countWithPlayer(playerName),
                (offset, limit) -> matchDao.findByPlayerName(playerName, offset, limit));
        log.info(LOG_LOADING_SUCCESS);
        return matchesPage;
    }

    private MatchesPageDto buildPage(int pageNumber,
                                     int matchesPerPage,
                                     LongSupplier countSupplier,
                                     BiFunction<Integer, Integer, List<Match>> findFunction) {

        return baseTransactionalService.executeInTransaction(() -> {
            long totalMatches = countSupplier.getAsLong();
            PaginationParams pagination = calculatePagination(totalMatches, pageNumber, matchesPerPage);

            List<FinishedMatchDto> matches = pagination.totalPages() == 0 ? List.of()
                    : findFunction.apply(pagination.offset(), matchesPerPage)
                    .stream()
                    .map(finishedMatchMapper::toDto)
                    .toList();

            return MatchesPageDto.builder()
                    .pageNumber(pagination.actualPage())
                    .matches(matches)
                    .totalPages(pagination.totalPages())
                    .errors(List.of())
                    .build();
        });
    }

    private PaginationParams calculatePagination(long totalMatches, int pageNumber, int matchesPerPage) {
        int totalPages = calculateTotalPages(totalMatches, matchesPerPage);
        int actualPage = normalizePageNumber(totalPages, pageNumber);
        int offset = (actualPage - 1) * matchesPerPage;
        return new PaginationParams(totalPages, actualPage, offset);
    }

    private int calculateTotalPages(long totalMatches, int matchesPerPage) {
        if (totalMatches < 1) {
            return 0;
        }
        return (int) ((totalMatches - 1) / matchesPerPage + 1);
    }

    private int normalizePageNumber(int totalPages, int pageNumber) {
        if (totalPages == 0) {
            return 1;
        } else return Math.min(pageNumber, totalPages);
    }
}
