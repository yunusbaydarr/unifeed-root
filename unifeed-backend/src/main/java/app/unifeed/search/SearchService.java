package app.unifeed.search;

import app.unifeed.common.RequestLimits;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class SearchService {
    private final SearchRepository repository;
    SearchService(SearchRepository repository) { this.repository = repository; }
    SearchController.SearchResult search(String query, int limit) {
        String normalized = query.trim();
        if (normalized.isEmpty()) return new SearchController.SearchResult(List.of(), List.of(), List.of());
        int safeLimit = RequestLimits.between(limit, 25);
        return new SearchController.SearchResult(repository.users(normalized, safeLimit), repository.clubs(normalized, safeLimit), repository.events(normalized, safeLimit));
    }
}
