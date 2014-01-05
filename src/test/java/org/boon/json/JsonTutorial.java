package org.boon.json;

import org.boon.Lists;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;

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

    public static void main (String... args) throws Exception {

        MyBean myBean = new MyBean();
        File dst = File.createTempFile("emp", ".json");

        ObjectMapper mapper =  ObjectMapperFactory.create();

        puts ("json string", mapper.writeValueAsString( myBean ));

        mapper.writeValue( dst, myBean ); // where 'dst' can be File, OutputStream or Writer


        File src = dst;
        MyBean value = mapper.readValue(src, MyBean.class); // 'src' can be File, InputStream, Reader, String



        //MyBean value = mapper.readValue(src, MyBean.class); // 'src' can be File, InputStream, Reader, String

        puts ("mybean", value);



        Object root = mapper.readValue(src, Object.class);
        Map<String,Object> rootAsMap =  mapper.readValue(src, Map.class);

        puts ("root", root);
        puts ("rootAsMap", rootAsMap);



        MyBean myBean1 = new MyBean(); myBean1.name = "Diana";
        MyBean myBean2 = new MyBean(); myBean2.name = "Rick";

        dst = File.createTempFile("empList", ".json");

        final List<MyBean> list = Lists.list( myBean1, myBean2 );

        puts ("json string", mapper.writeValueAsString( list ));

        mapper.writeValue( dst, list );

        src = dst;

        List<MyBean> beans = mapper.readValue(src, List.class, MyBean.class);

        puts ("mybeans", beans);





    }
}
