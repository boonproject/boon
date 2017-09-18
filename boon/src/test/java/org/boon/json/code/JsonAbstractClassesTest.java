package org.boon.json.code;

import org.boon.json.JsonFactory;
import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializer;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by piyush.goyal on 8/17/17.
 */
public class JsonAbstractClassesTest {



    private ObjectMapper mapper;

    @Test
    public void testAbstracTClassesWSimpleMapper() {
        mapper = JsonFactory.create(new JsonParserFactory().useAnnotations(),
                new JsonSerializerFactory().useAnnotations());
        testAbstracTClasses();
    }
    @Test
    public void testAbstracTClassesWComplexMapper() {
        mapper = JsonFactory.create(new JsonParserFactory().setRespectIgnore(false).useAnnotations(),
                new JsonSerializerFactory().useAnnotations());
        testAbstracTClasses();
    }

    public void testAbstracTClasses() {


        JsonClassC c = new JsonClassC();
        c.setPiyush("Tosheer");

        JsonClassD d = new JsonClassD();
        d.setPiyush("VIvek");

        Map<String, InterfaceB> map = new HashMap<>();
        map.put("c", c);
        map.put("d", d);

        List<InterfaceB> list = new ArrayList<>();
        list.add(c);
        list.add(d);

        JsonClassA jsonClassA = new JsonClassA();

        jsonClassA.setTextValue("aa");
        jsonClassA.setXx(map);
        jsonClassA.setYy(list);

        JsonSerializer serializer = new JsonSerializerFactory().setOutputType(true).create();

        String s = serializer.serialize(jsonClassA).toString();
        System.out.println(s);

        //String json = "{\"class\":\"com.akqa.some.code.JsonClassA\",\"xx\":{\"c\":{\"class\":\"com.akqa.some.code.JsonClassC\",\"piyush\":\"Tosheer\"},\"d\":{\"class\":\"com.akqa.some.code.JsonClassD\",\"piyush\":\"VIvek\"}},\"textValue\":\"aa\"}";
        String json = s;
        JsonClassA jsonClassA1 = mapper.fromJson(json, JsonClassA.class);


        assertEquals("aa", jsonClassA1.getTextValue());
        Map<String, InterfaceB> xx = jsonClassA1.getXx();
        InterfaceB c1 = xx.get("c");
        InterfaceB d1 = xx.get("d");
        assertEquals("Tosheer", c1.getPiyush());
        assertEquals("VIvek", d1.getPiyush());
        assertEquals(c.getClass(), c1.getClass());
        assertEquals(d.getClass(), d1.getClass());
        List<InterfaceB> list1= jsonClassA1.getYy();
        c1 = list1.get(0);
        d1 = list1.get(1);
        assertEquals("Tosheer", c1.getPiyush());
        assertEquals("VIvek", d1.getPiyush());
        assertEquals(c.getClass(), c1.getClass());
        assertEquals(d.getClass(), d1.getClass());
    }
}
