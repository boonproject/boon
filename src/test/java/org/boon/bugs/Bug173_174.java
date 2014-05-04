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

package org.boon.bugs;

import com.examples.model.test.FrequentPrimitives;
import org.boon.bugs.data.media.Image;
import org.boon.bugs.data.media.MediaContent;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;


/**
 * Created by Richard on 5/3/14.
 */
public class Bug173_174 {

    @Test
    public void test() {

        final ObjectMapper mapper =  JsonFactory.createUseProperties(true);


        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        mapper.writeValue(stream, FrequentPrimitives.getArray(2));

        puts(new String(stream.toByteArray()));
    }


    @Test
    public void test2() {

        final ObjectMapper mapper =  JsonFactory
                .createUseProperties(true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //String uri, String title, int width, int height, Size size
        mapper.writeValue(stream, new Image("/foo", "Foo", 5, 10, Image.Size.SMALL));


        puts(new String(stream.toByteArray()));
    }



    @Test
    public void test3() {

        final ObjectMapper mapper =  JsonFactory
                .createUseProperties(true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        MediaContent mediaContent = MediaContent.mediaContent();

        //String uri, String title, int width, int height, Size size
        mapper.writeValue(stream, mediaContent);


        MediaContent mediaContent2 = mapper.readValue(stream.toByteArray(), MediaContent.class);

        boolean ok = mediaContent.equals(mediaContent2) || die();
    }
}