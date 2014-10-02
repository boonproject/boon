package org.boon.bugs;


import org.boon.Maps;
import org.boon.datarepo.Repo;
import org.boon.datarepo.Repos;

import java.util.*;

import static org.boon.Boon.equalsOrDie;
import static org.boon.Boon.puts;
import static org.boon.criteria.ObjectFilter.eq;

/**
 * Created by Richard on 9/14/14.
 */
public class Bug224 {

//
//    static String[] jobs = new String[] {"manager", "clerk", "footballer", "artist", "teacher"};
//    static String[] colours = new String[] {"red", "blue", "green", "yellow", "orange"};
//    static String[] sports = new String[] {"athletics", "soccer", "swim", "cycle", "couch potato"};


    public static void main(String[] args) {


        puts("Create the repo");
        Repo<String, Map<String, String>> dbRepo = (Repo<String, Map<String, String>>)(Object) Repos.builder()
                .primaryKey("name")
                .lookupIndex("sport")
                .lookupIndex("job")
                .lookupIndex("colour")
                .build(String.class, Map.class);


        puts("just add some object");

        dbRepo.add(Maps.map( //Rick
                "job", "manager",
                "name", "Rick",
                "sport", "soccer"
        ));
        dbRepo.add(Maps.map( //Bob
                "job", "clerk",
                "name", "Bob",
                "sport", "soccer"
        ));
        dbRepo.add(Maps.map( //Sam
                "job", "clerk",
                "name", "Sam",
                "sport", "soccer"
        ));
        dbRepo.add(Maps.map( //Joe
                "job",  "clerk",
                "name", "Joe",
                "sport", "soccer"
        ));

        puts("Expect to be 1 manager:", dbRepo.query(eq("job", "manager")).size());
        puts("Expect to be 3 clerks:",  dbRepo.query(eq("job", "clerk")).size());

        equalsOrDie("Should be one manager", 1, dbRepo.query(eq("job", "manager")).size());
        equalsOrDie("Should be 3 clerks", 3, dbRepo.query(eq("job", "clerk")).size());

        Map<String, Object> manager = dbRepo.results(eq("job", "manager")).expectOne(Map.class).firstMap();


        manager = Maps.copy(manager); //We can't change the original object.
        // Or we will not be able to remove it from the old indexes.
        // There is an option to copy all objects on the way in. I think. Or there was one planned. :)

        //Now we modify the object.
        manager.put("job", "clerk");


        puts("The repo does not know about the change yet. We have not told it.");
        puts("Expect to be 1 manager:", dbRepo.query(eq("job", "manager")).size());
        puts("Expect to be 3 clerks:",  dbRepo.query(eq("job", "clerk")).size());

        equalsOrDie("Should be one manager", 1, dbRepo.query(eq("job", "manager")).size());
        equalsOrDie("Should be 3 clerks", 3, dbRepo.query(eq("job", "clerk")).size());




        puts("We call update so the repo can update its indexes");
        dbRepo.update( (Map) manager);
        puts("Expect to be 0 manager:", dbRepo.query(eq("job", "manager")).size());
        puts("Expect to be 4 clerks:",  dbRepo.query(eq("job", "clerk")).size());


        equalsOrDie("Should be 0 manager", 0, dbRepo.query(eq("job", "manager")).size());
        equalsOrDie("Should be 4 clerks", 4, dbRepo.query(eq("job", "clerk")).size());


        //Now let's promote Joe
        dbRepo.update("Joe", "job", "manager"); //This does an in place edit, and updates the indexes for us
        puts("Example of in place edit to promote Joe");
        puts("Expect to be 1 manager:", dbRepo.query(eq("job", "manager")).size());
        puts("Expect to be 3 clerks:",  dbRepo.query(eq("job", "clerk")).size());


        equalsOrDie("Should be 1 manager", 1, dbRepo.query(eq("job", "manager")).size());
        equalsOrDie("Should be 3 clerks", 3, dbRepo.query(eq("job", "clerk")).size());


    }



}
