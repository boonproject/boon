/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package com.examples;


import com.examples.model.test.Email;
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
import static org.boon.criteria.ObjectFilter.eq;
import static org.boon.criteria.ObjectFilter.notEq;


import com.examples.model.test.UserEmail;


public class DataRepoExamples {


    public static String EMAIL = "email";

    @Test
    public void test() {
        DataRepoExamples.main();
    }

    public static void main( String... args ) {


        boolean test = true;


        /* Create a repo builder that creates a user repo.
        *  A repo is just a new type of collection that allows queries.
        * */
        RepoBuilder repoBuilder = Repos.builder();
        repoBuilder.primaryKey( EMAIL );


        /** Create a repo of type String.class and Watcher.class */
        final Repo<String, User> userRepo = repoBuilder.build( Typ.string, user );

        final List<User> users = Lists.list(
                user( "rick.hightower@foo.com" ),
                user( "bob.jones@foo.com" ),
                user( "sam.jones@google.com" )
        );


        userRepo.addAll( users );

        /** SliceNotationExample 1: Query with eq. */

        /** Simple query. */
        List<User> results =
                userRepo.query( eq( EMAIL, "rick.hightower@foo.com" ) );

        putl( "SliceNotationExample 1: Simple Query using Equals Results", results );

        /** Same as results.get(0) */
        User rick = idx( results, 0 );

        /* Make sure we got what we wanted. */
        test |= Objects.equals( rick.getEmail(), "rick.hightower@foo.com" ) ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );


        ////// 2
        /** SliceNotationExample 2: Simple not query. */
        results =
                userRepo.query( notEq( EMAIL, "rick.hightower@foo.com" ) );

        putl( "SliceNotationExample 2: Simple Query using Not Equals Results", results );

        /** Same as results.get(0) */
        User notRick = idx( results, 0 );

        putl( notRick );

        /* Make sure we got what we wanted. */
        test |= !Objects.equals( notRick.getEmail(), "rick.hightower@foo.com" ) ||
                die( "Watcher Not Rick's email should NOT be equal " +
                        "to 'rick.hightower@foo.com' " );


        /////// 3
        /** SliceNotationExample 3: Simple query using ResultSet.firstItem. */
        rick =
                userRepo.results( eq( EMAIL, "rick.hightower@foo.com" ) ).firstItem();
        putl( "SliceNotationExample 3: Simple query using ResultSet.firstItem", rick );


                /* Make sure we got what we wanted. */
        test |= Objects.equals( rick.getEmail(), "rick.hightower@foo.com" ) ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );


        /////// 4
        /** SliceNotationExample 4: Expect only one item with expectOne().firstItem() . */
        rick = ( User )     //expectOne is not generic
                userRepo.results( eq( EMAIL, "rick.hightower@foo.com" ) )
                        .expectOne().firstItem();
        putl( "SliceNotationExample 4: Simple query using ResultSet.expectOne().firstItem", rick );


                /* Make sure we got what we wanted. */
        test |= Objects.equals( rick.getEmail(), "rick.hightower@foo.com" ) ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );


        /////// 5
        /** SliceNotationExample 5: Expect only one item with expectOne(user).firstItem() . */
        rick = userRepo.results( eq( EMAIL, "rick.hightower@foo.com" ) )
                .expectOne( user ).firstItem();
        putl( "SliceNotationExample 5: Simple query using ResultSet.expectOne(user).firstItem", rick );


                /* Make sure we got what we wanted. */
        test |= Objects.equals( rick.getEmail(), "rick.hightower@foo.com" ) ||
                die( "Rick's email not equal to 'rick.hightower@foo.com' " );


        /////// 6
        /** SliceNotationExample 6: Expect only one item with expectOne(user).firstItem() and we have many. */

        try {
            putl( "SliceNotationExample 6: Failure case, we have more than one for",
                    "query using ResultSet.expectOne(user).firstItem" );

            rick = userRepo.results( notEq( EMAIL, "rick.hightower@foo.com" ) )
                    .expectOne( user ).firstItem();
            die( "We should never get here!" );

        } catch ( DataRepoException ex ) {
            puts( "success for SliceNotationExample 6" );
        }


        runComponentClassTestForIssue();


    }

    private static void runComponentClassTestForIssue() {
        //TESTS

        putl( "EXAMPLE: Simple Composite Object query example",
                "See com.examples.model.test",
                "See Email and UserEmail classes",
                "__________________________________________"
        );


        boolean ok = true;

        RepoBuilder repoBuilder = Repos.builder();

        repoBuilder.usePropertyForAccess( true );


        putl( "The primary key is set to email" );

        repoBuilder.primaryKey( "email" );


        putl( "For ease of use you can setup nested properties ",
                "UserEmail.email property is a Email object not a string",
                "Email.email is a string." );

        //You can index component objects if you want
        repoBuilder.nestedIndex( "email", "email" );


        /** Create a repo of type String.class and Watcher.class */
        final Repo<Email, UserEmail> userRepo = repoBuilder.build(
                Email.class, UserEmail.class );


        puts( "Adding three test objects for bob, sam and joe " );
        userRepo.add( new UserEmail( "bob@bob.com" ) );
        userRepo.add( new UserEmail( "sam@bob.com" ) );
        userRepo.add( new UserEmail( "joe@bob.com" ) );


        putl( "Query using nested query Repo.eqNestedAdvanced()" );
        UserEmail bob = ( UserEmail ) userRepo.results(
                eq( "email.email", "bob@bob.com" ) )
                .expectOne().firstItem();

        ok |= bob.getEmail().getEmail().equals( "bob@bob.com" ) || die();


        putl( "Avoid the cast with using nested query Repo.eqNestedAdvanced(UserEmail.class)" );

        //NOT IN JDK7 Branch yet , but there is a generic version coming
        bob = userRepo.results( eq( "email.email", "bob@bob.com") )
                .expectOne( UserEmail.class ).firstItem();


        ok |= bob.getEmail().getEmail().equals( "bob@bob.com" ) || die();


        Email email = new Email( "bob@bob.com" );
        bob = ( UserEmail ) userRepo.results( eq( EMAIL, email ) )
                .expectOne().firstItem();

        ok |= bob.getEmail().getEmail().equals( "bob@bob.com" ) || die();

        puts( "success=", ok );


        putl( "__________________________________________",
                "__________________________________________",
                "__________________________________________" );


    }

}
