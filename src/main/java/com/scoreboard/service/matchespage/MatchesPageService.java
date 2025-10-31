package com.scoreboard.service.matchespage;

import com.scoreboard.config.properties.Config;
import com.scoreboard.constant.AppDefaults;
import com.scoreboard.constant.ConfigKeys;
import com.scoreboard.dao.MatchDao;
import com.scoreboard.dto.request.MatchesPageContext;
import com.scoreboard.dto.response.MatchesPage;
import com.scoreboard.mapper.MatchesPageMapper;
import com.scoreboard.model.entity.Match;
import com.scoreboard.service.BaseTransactionalService;
import com.scoreboard.validation.PaginationValidator;

import java.util.Collections;
import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public class MatchesPageService extends BaseTransactionalService {
    private final MatchDao matchDao;
    private final MatchesPageMapper mapper;
    private final int matchesPerPage;

    public MatchesPageService(MatchDao matchDao, Config config) {
        this.matchDao = matchDao;
        this.mapper = new MatchesPageMapper();
        this.matchesPerPage = config.getInt(
                ConfigKeys.MATCHES_PER_PAGE,
                AppDefaults.DEFAULT_MATCHES_PER_PAGE
        );
        PaginationValidator.validatePageSize(matchesPerPage);
    }

    public MatchesPage getPage(int pageNumber) {
        PaginationValidator.validatePageNumber(pageNumber);

        return buildPage(
                pageNumber,
                null,
                matchDao::getTotalCountOfMatches,
                () -> matchDao.find(pageNumber, matchesPerPage)
        );
    }

    public MatchesPage getPageByPlayerName(String playerName, int pageNumber) {
        PaginationValidator.validatePageNumber(pageNumber);

        return buildPage(
                pageNumber,
                playerName.trim(),
                () -> matchDao.getTotalCountOfMatchesByPlayerName(playerName),
                () -> matchDao.findByPlayerName(playerName, pageNumber, matchesPerPage)
        );
    }

    private MatchesPage buildPage(
            int pageNumber,
            String filter,
            LongSupplier getTotalCount,
            Supplier<List<Match>> getMatches
    ) {
        return executeInTransaction(() -> {
            long totalMatches = getTotalCount.getAsLong();
            long totalPages = calculateTotalPages(totalMatches, matchesPerPage);

            PaginationValidator.validatePageNumberAgainstTotal(pageNumber, totalPages);

            List<Match> matches = totalPages == 0
                    ? Collections.emptyList()
                    : getMatches.get();

            MatchesPageContext pageContext = new MatchesPageContext(
                    pageNumber, matches, totalPages, filter);
            return mapper.map(pageContext);
        }, "Failed to build matches page");
    }

    private long calculateTotalPages(long totalMatches, int matchesPerPage) {
        if (totalMatches < 1) {
            return 0;
        }
        return (totalMatches - 1) / matchesPerPage + 1;
    }
}
