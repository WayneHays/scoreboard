package com.scoreboard.service;

import com.scoreboard.dao.MatchDao;
import com.scoreboard.dto.FinishedMatchDto;
import com.scoreboard.dto.response.MatchesPage;
import com.scoreboard.mapper.FinishedMatchMapper;
import com.scoreboard.model.entity.Match;
import com.scoreboard.validation.NameValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;

@Slf4j
public class MatchesPageService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final NameValidator nameValidator;
    private final FinishedMatchMapper finishedMatchMapper;
    private final BaseTransactionalService baseTransactionalService;

    public MatchesPageService(MatchDao matchDao,
                              FinishedMatchMapper finishedMatchMapper,
                              NameValidator nameValidator,
                              BaseTransactionalService baseTransactionalService) {
        this.matchDao = matchDao;
        this.finishedMatchMapper = finishedMatchMapper;
        this.nameValidator = nameValidator;
        this.baseTransactionalService = baseTransactionalService;
    }

    public MatchesPage getPage(String playerName, int pageNumber, int matchesPerPage) {
        if (StringUtils.isBlank(playerName)) {
            return getMatchesPage(pageNumber, matchesPerPage);
        }

        List<String> errors = nameValidator.validate(playerName);

        if (ObjectUtils.isNotEmpty(errors)) {
            return getEmptyPageWithErrors(playerName, pageNumber, errors);
        }

        return getMatchesPageFiltered(playerName, pageNumber, matchesPerPage);
    }

    private MatchesPage getMatchesPage(int pageNumber, int matchesPerPage) {
        log.info("Loading all matches, page {} ({} per page", pageNumber, matchesPerPage);
        return buildPage(
                pageNumber,
                matchesPerPage,
                null,
                matchDao::countTotal,
                offset -> matchDao.find(offset, matchesPerPage));
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
        return buildPage(
                pageNumber,
                matchesPerPage,
                playerName,
                () -> matchDao.countWithPlayer(playerName),
                offset -> matchDao.findByPlayerName(playerName, offset, matchesPerPage));
    }

    private MatchesPage buildPage(
            int pageNumber,
            int matchesPerPage,
            String playerName,
            IntSupplier getTotalCount,
            Function<Integer, List<Match>> getMatches
    ) {
        return baseTransactionalService.executeInTransaction(() -> {
            int totalPages = calculateTotalPages(getTotalCount.getAsInt(), matchesPerPage);
            int actualPageNumber = normalizePageNumber(totalPages, pageNumber);
            int offset = (actualPageNumber - 1) * matchesPerPage;

            List<FinishedMatchDto> matches = totalPages == 0 ? List.of()
                    : getMatches.apply(offset)
                    .stream()
                    .map(finishedMatchMapper::toFinishedMatch)
                    .toList();

            return MatchesPage.builder()
                    .pageNumber(actualPageNumber)
                    .matches(matches)
                    .totalPages(totalPages)
                    .playerName(playerName)
                    .errors(List.of())
                    .build();
        });
    }

    private int normalizePageNumber(int totalPages, int pageNumber) {
        if (totalPages == 0) {
            return 1;
        } else return Math.min(pageNumber, totalPages);
    }

    private int calculateTotalPages(int totalMatches, int matchesPerPage) {
        if (totalMatches < 1) {
            return 0;
        }
        return (totalMatches - 1) / matchesPerPage + 1;
    }
}
