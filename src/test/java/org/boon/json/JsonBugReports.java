package org.boon.json;


import org.junit.Test;

import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.json.JsonFactory.fromJson;
import static org.boon.json.JsonFactory.toJson;

public class JsonBugReports {

    @Test
    public void testForIssue47() {
        Map<String, Object> map = (Map<String, Object>) fromJson("{\"empty\":\"\",\"docId\":111,\"serviceName\":\"cafe\"}");
        puts (map);
        puts (toJson(map));
    }

}
