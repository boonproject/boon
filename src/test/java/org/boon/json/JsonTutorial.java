package org.boon.json;

import org.boon.IO;
import org.boon.Lists;
import org.boon.core.Dates;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.json.annotations.JsonIgnore;
import org.boon.json.annotations.JsonInclude;
import org.boon.json.annotations.JsonViews;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by rick on 1/4/14.
 */
public class JsonTutorial {


    public static class MyBean {
        String name = "Rick";

        @Override
        public String toString() {
            return "MyBean{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }


    public static class User {


        @JsonViews( ignoreWithViews = {"public"},
                includeWithViews = {"internal"})
        private String empId = "555-55-5555";

        @JsonIgnore
        private String ssn = "555-55-5555";

        @JsonInclude
        private String status = null;

        public enum Gender {MALE, FEMALE}



        public static class Name {
            private String first, last;

            public Name( String _first, String _last ) {
                this.first = _first;
                this.last = _last;
            }

            public String getFirst() {
                return first;
            }

            public String getLast() {
                return last;
            }

            public void setFirst( String s ) {
                first = s;
            }

            public void setLast( String s ) {
                last = s;
            }

            @Override
            public String toString() {
                return "Name{" +
                        "first='" + first + '\'' +
                        ", last='" + last + '\'' +
                        '}';
            }
        }

        private Gender gender;
        private Name name;
        private boolean verified;
        private Date birthDate;

        public Name getName() {
            return name;
        }

        public boolean isVerified() {
            return verified;
        }

        public Gender getGender() {
            return gender;
        }

        public void setName( Name n ) {
            name = n;
        }

        public void setVerified( boolean b ) {
            verified = b;
        }

        public void setGender( Gender g ) {
            gender = g;
        }

        public Date getBirthDate() {
            return birthDate;
        }

        public void setBirthDate( Date birthDate ) {
            this.birthDate = birthDate;
        }

        @Override
        public String toString() {
            return "User{" +
                    "gender=" + gender +
                    ", name=" + name +
                    ", verified=" + verified +
                    ", birthDate=" + birthDate +
                    '}';
        }
    }


    static User user = new User();

    static {
        user.setGender( User.Gender.MALE );
        user.setName( new User.Name( "Richard", "Hightower" ) );
        user.setVerified( true );
        user.setBirthDate( Dates.getUSDate( 5, 25, 1980 ) );
    }

    public static void part1() throws Exception {


        MyBean myBean = new MyBean();
        File dst = File.createTempFile( "emp", ".json" );

        ObjectMapper mapper = ObjectMapperFactory.create();

        puts( "json string", mapper.writeValueAsString( myBean ) );

        mapper.writeValue( dst, myBean ); // where 'dst' can be File, OutputStream or Writer


        File src = dst;
        MyBean value = mapper.readValue( src, MyBean.class ); // 'src' can be File, InputStream, Reader, String

        puts( "mybean", value );


        Object root = mapper.readValue( src, Object.class );
        Map<String, Object> rootAsMap = mapper.readValue( src, Map.class );

        puts( "root", root );
        puts( "rootAsMap", rootAsMap );


        MyBean myBean1 = new MyBean();
        myBean1.name = "Diana";
        MyBean myBean2 = new MyBean();
        myBean2.name = "Rick";

        dst = File.createTempFile( "empList", ".json" );

        final List<MyBean> list = Lists.list( myBean1, myBean2 );

        puts( "json string", mapper.writeValueAsString( list ) );

        mapper.writeValue( dst, list );

        src = dst;

        List<MyBean> beans = mapper.readValue( src, List.class, MyBean.class );

        puts( "mybeans", beans );


    }

    public static void part2() throws Exception {

        ObjectMapper mapper = ObjectMapperFactory.create();


        puts( mapper.writeValueAsString( user ) );


        //Now to write and then read this as a file.

        File file = File.createTempFile( "user", ".json" );

        mapper.writeValue( file, user );

        User userFromFile = mapper.readValue( file, User.class );

        puts( "userFromFile", userFromFile );


        Path path = Paths.get( file.toString() );
        InputStream inputStream = Files.newInputStream( path );

        User userFromInput = mapper.readValue( inputStream, User.class );
        puts( "userFromInput", userFromInput );


        Reader reader = Files.newBufferedReader( path, StandardCharsets.UTF_8 );
        User userFromReader = mapper.readValue( reader, User.class );

        puts( "userFromReader", userFromReader );


    }

    public static void part3() throws Exception {
        part3_1();
        part3_2();
    }

    public static void part3_1() throws Exception {

        ObjectMapper mapper = ObjectMapperFactory.create();
        puts( mapper.writeValueAsString( user ) );

        User user2 = mapper.readValue( mapper.writeValueAsString( user ), User.class );

        puts( user2 );
    }


    public static void part3_2() throws Exception {

        ObjectMapper mapper = ObjectMapperFactory.createUseJSONDates();
        puts( mapper.writeValueAsString( user ) );

        User user2 = mapper.readValue( mapper.writeValueAsString( user ), User.class );

        puts( user2 );

    }

    public static void part5() throws Exception {

        puts ("\n\n\n", "\npart5");

        ObjectMapper mapper = ObjectMapperFactory.createUseJSONDates();


        final User diana = BeanUtils.copy( user );
        final User rick = BeanUtils.copy( user );
        diana.getName().setFirst( "Diana" );
        rick.getName().setFirst( "Rick" );
        diana.setBirthDate( Dates.getUSDate( 8, 21, 1984 ) );


        File file = File.createTempFile( "userList", ".json" );

        List<User> users = Lists.list( diana, rick );

        mapper.writeValue( file, users  );


        List<User> userList = mapper.readValue( file, List.class, User.class  );


        puts (userList);

        puts ( mapper.writeValueAsString( userList ) );


    }

    public static void part4() throws Exception {

        ObjectMapper mapper = ObjectMapperFactory.createUseJSONDates();


        puts( mapper.writeValueAsString( user ) );


        //Now to write and then read this as a file.

        File file = File.createTempFile( "user", ".json" );

        mapper.writeValue( file, user );

        Object userFromFile = mapper.readValue( file, Object.class );

        puts( "userFromFile", "type", userFromFile.getClass(), "value", userFromFile );

        Map<String, Object> map = (Map<String, Object>) mapper.readValue( file, Map.class );

        puts( "userFromFile", "type", map.getClass(), "value", map );


        puts( "userFromFile.name", "type", map.get("name").getClass(),
                "value", map.get("name") );


        puts( "userFromFile.birthDate", "type", map.get("birthDate").getClass(),
                "value", map.get("birthDate") );


        puts( "userFromFile.gender", "type", map.get("gender").getClass(),
                "value", map.get("gender") );


        User userFromMap =
               MapObjectConversion.fromMap(
                       map, User.class);

        puts ( userFromMap );

    }


    public static void part6() throws Exception {

        puts ("\n\n\n", "\npart6");

        ObjectMapper mapper = ObjectMapperFactory.createUseJSONDates();


        final User diana = BeanUtils.copy( user );
        final User rick = BeanUtils.copy( user );
        diana.getName().setFirst( "Diana" );
        diana.setGender( User.Gender.FEMALE );
        rick.getName().setFirst( "Rick" );
        diana.setBirthDate( Dates.getUSDate( 8, 21, 1984 ) );


        File file = File.createTempFile( "userList", ".json" );

        List<User> users = Lists.list( diana, rick );

        mapper.writeValue( file, users  );


        List<User> userList = mapper.readValue( file, List.class, User.class  );


        puts (userList);

        puts ( mapper.writeValueAsString( userList ) );


    }

    public static void part7() throws Exception {

        puts ("\n\n\n", "\npart7");

        ObjectMapper mapper = ObjectMapperFactory.createUseAnnotations( true );


        /* Create two users. */
        final User diana = BeanUtils.copy( user );
        final User rick = BeanUtils.copy( user );
        diana.getName().setFirst( "Diana" );
        diana.setGender( User.Gender.FEMALE );
        rick.getName().setFirst( "Rick" );
        diana.setBirthDate( Dates.getUSDate( 8, 21, 1984 ) );

        File file = File.createTempFile( "userList", ".json" );
        List<User> users = Lists.list( diana, rick );


        /* Inspect the JSON of the users from the file. */
        puts ("users", mapper.writeValueAsString( users ) );


        /* Write users out to file. */
        mapper.writeValue( file, users  );

        /* Reader Users back from file. */
        List<User> userList = mapper.readValue( file, List.class, User.class  );


        puts ("userListBeansReadFromFile", userList);

        /* Inspect the JSON of the users from the file. */
        puts ("usersFromFileAsJSON", mapper.writeValueAsString( userList ) );


    }


    public static void part8 () throws Exception {

        puts ("\n\n\n\npart8");

        ObjectMapper mapper = ObjectMapperFactory.create();


        String intStr = "123456";

        int someNumber = mapper.parser().parseInt( intStr );

        boolean ok = someNumber == 123456 || die( "" + someNumber );


        String jsonArray = "[0,1,2,3,4,5,6,7,8]";

        int [] intArray = mapper.parser().parseIntArray( jsonArray );

        ok |= Arrays.equals( new int[]{1,2,3,4,5,6,7,8}, intArray );


        String jsonMap = "{\"race\":true, \"speedup\": false, \"name\": \"bob\"}";

        Map <String, Object> map  = mapper.parser().parseMap( jsonMap );

        ok |= ( map.get("race") == Boolean.TRUE  && map.get("name").equals( "bob" ) )  || die(map.toString());

        puts("ok?", ok);


    }



    public static void part9() throws Exception {

        puts ("\n\n\n", "\npart9");


        JsonParserFactory jsonParserFactory = new JsonParserFactory()
                .useFieldsFirst().useFieldsOnly().usePropertiesFirst().usePropertyOnly() //one of these
                .plistStyle() //allow parsing of ASCII PList style files
                .lax() //allow loose parsing of JSON like JSON Smart
                .strict() //opposite of lax
                .setCharset( StandardCharsets.UTF_8 ) //Set the standard charset, defaults to UTF_8
                .setChop( true ) //chops up buffer overlay buffer (more discussion of this later)
                .setLazyChop( true ) //similar to chop but only does it after map.get
                ;

        JsonSerializerFactory jsonSerializerFactory = new JsonSerializerFactory()
                .useFieldsFirst().useFieldsOnly().usePropertiesFirst().usePropertyOnly() //one of these
                //.addPropertySerializer(  )  customize property output
                //.addTypeSerializer(  )      customize type output
                .useJsonFormatForDates() //use json dates
                //.addFilter(  )   add a property filter to exclude properties
                .includeEmpty().includeNulls().includeDefaultValues() //override defaults
                .handleComplexBackReference() //uses identity map to track complex back reference and avoid them
                .setHandleSimpleBackReference( true ) //looks for simple back reference for parent
                .setCacheInstances( true ) //turns on caching for immutable objects
                ;



        final User diana = BeanUtils.copy( user );
        final User rick = BeanUtils.copy( user );
        diana.getName().setFirst( "Diana" );
        diana.setGender( User.Gender.FEMALE );
        rick.getName().setFirst( "Rick" );
        diana.setBirthDate( Dates.getUSDate( 8, 21, 1984 ) );
        List<User> users = Lists.list( diana, rick );

        //You can use parser and serializer directly.
        final JsonParser jsonParser = jsonParserFactory.create();
        final JsonSerializer jsonSerializer = jsonSerializerFactory.create();

        File file = File.createTempFile( "userList", ".json" );
        String jsonString = jsonSerializer.serialize( users ).toString();
        IO.write( IO.path( file.toString()), jsonString);
        List<User> users2 = jsonParser.parseListFromFile( User.class, file.toString() );
  
        //Or you can pass them to the ObjectMapper interface you know and love, just pass the factories to it.
        ObjectMapper mapper = ObjectMapperFactory.create(jsonParserFactory, jsonSerializerFactory);


        mapper.writeValue( file, users  );
        List<User> userList = mapper.readValue( file, List.class, User.class  );
        puts (userList);
        puts ( mapper.writeValueAsString( userList ) );


    }



    public static void part10() throws Exception {


        final User rick = BeanUtils.copy( user );
        rick.getName().setFirst( "Rick" );

        boolean ok = true;


        JsonSerializer serializer = new JsonSerializerFactory().useAnnotations().setView( "public" ).create();
        String str = serializer.serialize( rick ).toString();

        puts (str);
        ok |= !str.contains( "\"empId\":" ) || die(str);


        serializer = new JsonSerializerFactory().useAnnotations().setView( "internal" ).create();
        str = serializer.serialize( rick ).toString();
        ok |= str.contains( "\"empId\":" ) || die(str);

        puts (str);


        serializer = new JsonSerializerFactory().useAnnotations().create();
        str = serializer.serialize( rick ).toString();
        ok |= str.contains( "\"empId\":" ) || die(str);

        puts (str);


    }


    public static void main( String... args ) throws Exception {

        part1();
        part2();
        part3();
        part4();
        part5();
        part6();
        part7();
        part8();
        part9();
        part10();


    }


    @Test
    public void test() throws Exception {
        JsonTutorial.main(  );
    }
}
