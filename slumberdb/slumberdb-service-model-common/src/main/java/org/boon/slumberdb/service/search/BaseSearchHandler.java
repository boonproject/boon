package org.boon.slumberdb.service.search;

import org.boon.slumberdb.service.protocol.requests.SearchRequest;
import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;
import org.boon.slumberdb.KeyValueStore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author JD
 */
public class BaseSearchHandler implements SearchHandler {
    public final static String TOTAL_RESULTS_KEY = "total";

    @Override
    public Map<String, String> handle(KeyValueStore<String, String> store, SearchRequest request) {
        Map<String, String> results = new LinkedHashMap<>();

        int count = 0;
        int total = 0;
        SearchCriterion criteria = request.criteria().get(0);
        final KeyValueIterable<String, String> keyValueIterable = store.loadAll();
        Pattern pattern = Pattern.compile(criteria.getCriterion());

        try {

            for (Entry<String, String> entry : keyValueIterable) {
                boolean keyMatch = match(entry.key(), criteria, pattern);
                boolean valueMatch = match(entry.value(), criteria, pattern);

                if (keyMatch || valueMatch) {
                    if (count >= request.offset()
                            && results.size() < request.limit()) {
                        results.put(entry.key(), entry.value());
                    }
                    total++;
                }
                count++;
            }
            results.put(TOTAL_RESULTS_KEY, Integer.toString(total));
        } finally {
            keyValueIterable.close();
        }
        return results;
    }

    private boolean match(String match, SearchCriterion criteria, Pattern pattern) {
        String criterion = criteria.getCriterion();
        boolean isMatch = false;
        if (criterion != null && !criterion.equals("")) {
            Matcher matcher = pattern.matcher(match);

            if (matcher.matches()) {
                isMatch = true;
            }
        }

        return isMatch;
    }
}
