package org.boon.bugs;


//Bug report from https://github.com/kimptoc/boon-tester

import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;

import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.criteria.ObjectFilter.and;
import static org.boon.criteria.ObjectFilter.eq;

/**
 * Created by Richard on 9/8/14.
 */
public class Bug216 {

    static final int MAX_N = 30_000;

    static final int ITER = 10_000;

    static List<Map<String, String>> database = new ArrayList<>();

    static String[] jobs = new String[] {"manager", "clerk", "footballer", "artist", "teacher"};
    static String[] colours = new String[] {"red", "blue", "green", "yellow", "orange"};


    public static void main(String[] args) {

        for (int i=0; i<MAX_N; i++){
            Map<String, String> entry = new HashMap<>();
            entry.put("name","Mr "+i);
            entry.put("colour",colours[((int) (Math.random() * colours.length))]);
            entry.put("job",jobs[((int) (Math.random() * jobs.length))]);
            database.add(entry);
        }



        long start = System.currentTimeMillis();

        long count=0;

        for (int i=0; i<ITER; i++) {
            count+=findColour();
            count+=findJob();
            count+=findJobColour("clerk","blue");

        }

        long elapsed = System.currentTimeMillis() - start;

        puts("warmup elapsed", elapsed, "found", count);



        start = System.currentTimeMillis();

        count=0;

        for (int i=0; i<ITER; i++) {
            count+=findColour();
            count+=findJob();
            count+=findJobColour("clerk","blue");

        }

        elapsed = System.currentTimeMillis() - start;

        puts("After warmup JDK example from kimptoc", elapsed, "found", count);


        Repo<String, Map<String, String>> dbRepo =  (Repo<String, Map<String, String>>)(Object) Repos.builder()
                .primaryKey("name")
                .lookupIndex("colour")
                .lookupIndex("job")
                .lookupIndex("sport")
                .build(String.class, Map.class);

        dbRepo.addAll(database);


        count=0;

        for (int i=0; i<ITER; i++) {
            count+=findColourBoon(dbRepo, "red").size();
            count+=findJobBoon(dbRepo, "artist").size();
            count+=findJobSportBoon(dbRepo, "manager", "cycle").size();

            count+=findJobColourBoon(dbRepo, "clerk","blue").size();
        }

        puts("Warming up boon... Warmup round", count);


        count=0;

        start = System.currentTimeMillis();
        for (int i=0; i<ITER; i++) {

            count+=findColourBoon(dbRepo, "red").size();
            count+=findJobBoon(dbRepo, "artist").size();
            count+=findJobSportBoon(dbRepo, "manager", "cycle").size();

            count+=findJobColourBoon(dbRepo, "clerk","blue").size();
        }

        elapsed = System.currentTimeMillis() - start;


        puts("Boon BABY! elapsed", elapsed, "found", count);

    }

    private static int findJob() {
        int count = 0;
        for (Map entry : database) {
            if (entry.get("job").equals("artist")) {
                count++;
            }
        }
        return count;
    }

    private static int findColour() {
        int count = 0;

        for (Map entry : database) {
            if (entry.get("colour").equals("red")) {
                count++;

            }
        }
        return count;

    }

    private static int findJobColour(String job, String colour) {
        int count = 0;
        for (Map entry : database) {
            if (entry.get("colour").equals(colour) && entry.get("job").equals(job)) {
                count++;
            }
        }
        return count;
    }



    private static List<Map<String, String>>  findJobBoon(Repo<String, Map<String, String>> dbRepo, String job) {
        return dbRepo.query(eq("job", job));

    }

    private static List<Map<String, String>> findColourBoon(Repo<String, Map<String, String>> dbRepo, String colour) {
         return dbRepo.query(eq("colour", colour));
    }

    private static Collection<Map<String, String>> findJobColourBoon(Repo<String, Map<String, String>> dbRepo, String colour, String job) {
        return dbRepo.results(eq("colour", colour)).filter(eq("job", job));
    }
    private static List<Map<String, String>> findJobSportBoon(Repo<String, Map<String, String>> dbRepo, String job, String sport) {
        return dbRepo.query(and( eq("sport", sport), eq("job", job)));
    }
}
