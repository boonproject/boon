package com.examples;

import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import java.io.File;
import java.util.Arrays;

import static org.boon.Boon.fromJson;
import static org.boon.Boon.puts;
import static org.boon.Boon.toJson;

/**
 * Created by rhightower on 2/17/15.
 */
public class HowToParseJSONInJava {

    public static class Player {

        private final String name;
        private final String sport;
        private final int age;
        private final int id;
        private final int[] lastScores;


        public Player(String name, String sport, int age, int id, int[] lastScores) {
            this.name = name;
            this.sport = sport;
            this.age = age;
            this.id = id;
            this.lastScores = lastScores;
        }

        public final String getName() {
            return name;
        }

        public final String getSport() {
            return sport;
        }

        public final int getAge() {
            return age;
        }

        public final int getId() {
            return id;
        }

        public final int[] getLastScores() {
            return lastScores;
        }


        @Override
        public String toString() {
            return "Player{" +
                    "name='" + name + '\'' +
                    ", sport='" + sport + '\'' +
                    ", age=" + age +
                    ", id=" + id +
                    ", lastScores=" + Arrays.toString(lastScores) +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Player player = (Player) o;

            if (age != player.age) return false;
            if (id != player.id) return false;
            if (!Arrays.equals(lastScores, player.lastScores)) return false;
            if (name != null ? !name.equals(player.name) : player.name != null) return false;
            if (sport != null ? !sport.equals(player.sport) : player.sport != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (sport != null ? sport.hashCode() : 0);
            result = 31 * result + age;
            result = 31 * result + id;
            result = 31 * result + (lastScores != null ? Arrays.hashCode(lastScores) : 0);
            return result;
        }
    }

    public static void main1(String... args) {

        Player kevin = new Player("Kevin", "Cricket", 32, 221, new int[]{33, 66, 78, 21, 9, 200});
        final String json = toJson(kevin);

        puts("JSON", json);

        Player somePlayer = fromJson(json, Player.class);

        puts("They are equal", somePlayer.equals(kevin));

    }


    public static void main(String... args) throws Exception{

        Player kevin = new Player("Kevin", "Cricket", 32, 221,
                new int[]{33, 66, 78, 21, 9, 200});

        final ObjectMapper mapper = JsonFactory.create();


        final File file = File.createTempFile("json", "exmaple.json");
        mapper.writeValue(file, kevin);



        Player somePlayer = mapper.readValue(file, Player.class);

        puts("They are equal", somePlayer.equals(kevin));


    }

}
