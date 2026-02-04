package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.dto.MatchesPage;
import com.scoreboard.dto.PaginationParams;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.validation.PlayerNameValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MatchesPageService {
    private final MatchDao matchDao;
    private final PlayerNameValidator playerNameValidator;
    private final FinishedMatchMapper finishedMatchMapper;
    private final BaseTransactionalService baseTransactionalService;

    public MatchesPage getPage(String playerName, int pageNumber, int matchesPerPage) {
        if (StringUtils.isBlank(playerName)) {
            return getMatchesPage(pageNumber, matchesPerPage);
        }

        List<String> errors = playerNameValidator.validate(playerName);

        if (ObjectUtils.isNotEmpty(errors)) {
            return getEmptyPageWithErrors(playerName, pageNumber, errors);
        }

        return getMatchesPageFiltered(playerName, pageNumber, matchesPerPage);
    }

    private MatchesPage getMatchesPage(int pageNumber, int matchesPerPage) {
        log.info("Loading all matches, page {} ({} per page", pageNumber, matchesPerPage);

        return baseTransactionalService.executeInTransaction(() -> {
            long totalMatches = matchDao.countTotal();
            PaginationParams pagination = calculatePagination(totalMatches, pageNumber, matchesPerPage);

            List<FinishedMatchDto> matches = pagination.totalPages() == 0 ? List.of()
                    : matchDao.find(pagination.offset(), matchesPerPage).stream()
                    .map(finishedMatchMapper::toDto)
                    .toList();

            return MatchesPage.builder()
                    .pageNumber(pagination.actualPage())
                    .matches(matches)
                    .totalPages(pagination.totalPages())
                    .errors(List.of())
                    .build();
        });
    }

    private MatchesPage getEmptyPageWithErrors(String playerName, int pageNumber, List<String> errors) {
        log.info("Loading empty page with errors");
        return MatchesPage.builder()
                .pageNumber(pageNumber)
                .matches(List.of())
                .totalPages(0)
                .playerName(playerName)
                .errors(errors)
                .build();
    }

    private MatchesPage getMatchesPageFiltered(String playerName, int pageNumber, int matchesPerPage) {
        log.info("Loading matches for player {} by page {}", playerName, pageNumber);

        return baseTransactionalService.executeInTransaction(() -> {
            long totalMatches = matchDao.countWithPlayer(playerName);
            PaginationParams pagination = calculatePagination(totalMatches, pageNumber, matchesPerPage);

            List<FinishedMatchDto> matches = pagination.totalPages() == 0 ? List.of()
                    : matchDao.findByPlayerName(playerName, pagination.offset(), matchesPerPage).stream()
                    .map(finishedMatchMapper::toDto)
                    .toList();

            return MatchesPage.builder()
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
