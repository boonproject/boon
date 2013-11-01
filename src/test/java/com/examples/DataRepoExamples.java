package com.examples;


import com.examples.security.model.User;
import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.datarepo.DataRepoException;
import org.boon.datarepo.Repo;
import org.boon.datarepo.RepoBuilder;
import org.boon.datarepo.Repos;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

import static com.examples.security.model.User.user;
import static org.boon.Boon.putl;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.idx;
import static org.boon.criteria.CriteriaFactory.eq;
import static org.boon.criteria.CriteriaFactory.notEq;

public class DataRepoExamples {


    public static String EMAIL = "email";

    @Test public void test() {
        DataRepoExamples.main (  );
    }

    public static void main ( String... args ) {


        boolean test = true;


        /* Create a repo builder that creates a user repo.
        *  A repo is just a new type of collection that allows queries.
        * */
        RepoBuilder repoBuilder = Repos.builder ();
        repoBuilder.primaryKey ( EMAIL );


        /** Create a repo of type String.class and User.class */
        final Repo<String,User> userRepo = repoBuilder.build ( Typ.string, user );

        final List<User> users = Lists.list ( user ( "rick.hightower@foo.com" ),
                user ( "bob.jones@foo.com" ),
                user ( "sam.jones@google.com" )
        );


        userRepo.addAll ( users );

        /** Example 1: Query with eq. */

        /** Simple query. */
        List<User> results =
                userRepo.query ( eq( EMAIL, "rick.hightower@foo.com") );

        putl ( "Example 1: Simple Query using Equals Results", results );

        /** Same as results.get(0) */
        User rick = idx (results, 0);

        /* Make sure we got what we wanted. */
        test |= Objects.equals (rick.getEmail (), "rick.hightower@foo.com") ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );


        ////// 2
        /** Example 2: Simple not query. */
        results =
                userRepo.query ( notEq( EMAIL, "rick.hightower@foo.com" ) );

        putl ( "Example 2: Simple Query using Not Equals Results", results );

        /** Same as results.get(0) */
        User notRick = idx (results, 0);

        putl ( notRick );

        /* Make sure we got what we wanted. */
        test |= !Objects.equals (notRick.getEmail (), "rick.hightower@foo.com") ||
                die( "User Not Rick's email should NOT be equal " +
                        "to 'rick.hightower@foo.com' " );


        /////// 3
        /** Example 3: Simple query using ResultSet.firstItem. */
        rick =
                userRepo.results ( eq ( EMAIL, "rick.hightower@foo.com" ) ).firstItem ();
        putl ( "Example 3: Simple query using ResultSet.firstItem", rick );


                /* Make sure we got what we wanted. */
        test |= Objects.equals (rick.getEmail (), "rick.hightower@foo.com") ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );



        /////// 4
        /** Example 4: Expect only one item with expectOne().firstItem() . */
        rick =  (User)     //expectOne is not generic
                userRepo.results ( eq ( EMAIL, "rick.hightower@foo.com" ) ).expectOne ().firstItem ();
        putl ( "Example 4: Simple query using ResultSet.expectOne().firstItem", rick );


                /* Make sure we got what we wanted. */
        test |= Objects.equals (rick.getEmail (), "rick.hightower@foo.com") ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );



        /////// 5
        /** Example 5: Expect only one item with expectOne(user).firstItem() . */
        rick =  userRepo.results ( eq ( EMAIL, "rick.hightower@foo.com" ) ).expectOne (user).firstItem ();
        putl ( "Example 5: Simple query using ResultSet.expectOne(user).firstItem", rick );


                /* Make sure we got what we wanted. */
        test |= Objects.equals (rick.getEmail (), "rick.hightower@foo.com") ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );


        /////// 6
        /** Example 6: Expect only one item with expectOne(user).firstItem() and we have many. */

        try {
            putl ( "Example 6: Failure case, we have more than one for",
                    "query using ResultSet.expectOne(user).firstItem");

            rick =  userRepo.results ( notEq ( EMAIL, "rick.hightower@foo.com" ) ).expectOne (user).firstItem ();
            die("We should never get here!");

        } catch (DataRepoException ex) {
            puts ("success for Example 6");
        }


    }

}
