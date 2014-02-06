package org.boon.primitive;


import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static junit.framework.Assert.assertTrue;
import static org.boon.Exceptions.die;
import static org.boon.primitive.Chr.chars;
import static org.junit.Assert.assertEquals;

public class CharBufTest {




    @Test
    public void unicodeAndControl() {
        String str =  CharBuf.create(0).addJsonEscapedString("\u0001").toString();

        boolean ok = str.equals( "\"\\u0001\"") || die(str);
        str =  CharBuf.create(0).addJsonEscapedString("\u00ff").toString();
        ok = str.equals( "\"\\u00ff\"") || die(str);

        str =  CharBuf.create(0).addJsonEscapedString("\u0fff").toString();
        ok = str.equals( "\"\\u0fff\"") || die(str);

        str =  CharBuf.create(0).addJsonEscapedString("\uffff").toString();
        ok = str.equals( "\"\\uffff\"") || die(str);

        str =  CharBuf.create(0).addJsonEscapedString("\uefef").toString();
        ok = str.equals( "\"\\uefef\"") || die(str);

        str =  CharBuf.create(0).addJsonEscapedString(" \b ").toString();
        ok = str.equals( "\" \\b \"" ) || die(str);

        str =  CharBuf.create(0).addJsonEscapedString(" \r ").toString();
        ok = str.equals( "\" \\r \"" ) || die(str);

        str =  CharBuf.create(0).addJsonEscapedString(" \n ").toString();
        ok = str.equals( "\" \\n \"" ) || die(str);

        str =  CharBuf.create(0).addJsonEscapedString(" \n ").toString();
        ok = str.equals( "\" \\n \"" ) || die(str);


        str =  CharBuf.create(0).addJsonEscapedString(" \f ").toString();
        ok = str.equals( "\" \\f \"" ) || die(str);

        str =  CharBuf.create(0).addJsonEscapedString(" \" Hi mom \" ").toString();
        ok = str.equals( "\" \\\" Hi mom \\\" \"" ) || die(str);


        str =  CharBuf.create(0).addJsonEscapedString(" \\ ").toString();
        ok = str.equals( "\" \\\\ \"" ) || die(str);

    }

    @Test
    public void testFrenchChars() {
          String str = "Éé, Èè, Êê, Ëë, Àà, Ââ, Ææ, Ôô, Œœ, Ùù, Ûû, Üü, Ÿÿ";
          byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
          CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
          boolean ok = str.equals( buf.toString() ) || die( buf.toString() );
    }

    @Test
    public void testJapaneseCharacters() {
        String str = "色 c";//olour; 白 white; 黒 black; 赤 red; 紅 crimson; 青 blue; 黄 yellow; 緑 green;";
        byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
        CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
        boolean ok = str.equals( buf.toString() ) || die( buf.toString() );
    }

    @Test
    public void testJapaneseCharacters2() {
        String str = "丁 | 七 | 九 | 了 | 二 | 人 | 入 | 八 | 刀 | 力 | 十 | 又 | 乃";
        byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
        CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
        boolean ok = str.equals( buf.toString() ) || die( buf.toString() );
    }

    @Test
    public void testJapaneseCharacters12() {
        String str = "偉 | 傍 | 傘 | 備 | 割 | 創 | 勝 | 募 | 博 | 善 | 喚 | 喜 | 喪 | 喫 | 営 | 堅 | 堕 | 堤 | 堪 |" +
                " 報 | 場 | 塀 | 塁 | 塔 | 塚 | 奥 | 婿 | 媒 | 富 | 寒 | 尊 | 尋 | 就 | 属 | 帽 | 幅 | 幾 | 廃 | 廊 |" +
                " 弾 | 御 | 復 | 循 | 悲 | 惑 | 惰 | 愉 | 慌 | 扉 | 掌 | 提 | 揚 | 換 | 握 | 揮 | 援 | 揺 | 搭 | 敢 |" +
                " 散 | 敬 | 晩 | 普 | 景 | 晴 | 晶 | 暁 | 暑 | 替 | 最 | 朝 | 期 | 棋 | 棒 | 棚 | 棟 | 森 | 棺 | 植 |" +
                " 検 | 業 | 極 | 欺 | 款 | 歯 | 殖 | 減 | 渡 | 渦 | 温 | 測 | 港 | 湖 | 湯 | 湾 | 湿 | 満 | 滋 | 無 |" +
                " 焦 | 然 | 焼 | 煮 | 猶 | 琴 | 番 | 畳 | 疎 | 痘 | 痛 | 痢 | 登 | 着 | 短 | 硝 | 硫 | 硬 | 程 | 税 |" +
                " 童 | 筆 | 等 | 筋 | 筒 | 答 | 策 | 紫 | 結 | 絞 | 絡 | 給 | 統 | 絵 | 絶 | 脹 | 腐 | 腕 | 落 | 葉 |" +
                " 葬 | 蛮 | 衆 | 裁 | 裂 | 装 | 裕 | 補 | 覚 | 訴 | 診 | 証 | 詐 | 詔 | 評 | 詞 | 詠 | 象 | 貫 | 貯 |" +
                " 貴 | 買 | 貸 | 費 | 貿 | 賀 | 超 | 越 | 距 | 軸 | 軽 | 遂 | 遅 | 遇 | 遊 | 運 | 遍 | 過 | 道 | 達 |" +
                " 酢 | 量 | 鈍 | 開 | 閑 | 間 | 陽 | 隅 | 隊 | 階 | 随 | 雄 | 集 | 雇 | 雰 | 雲 | 項 | 順 | 飯 | 飲 |" +
                " 智 | 須 | 萩 | 敦 | 媛 | 嵐 | 椎 | 翔 | 喬 | 巽 | 湧 | 斐 | 葵 | 禄 | 欽 | 惣 | 稀 | 渥 | 凱 | 絢 |" +
                " 遥 | 瑛 | 皓 | 竣 | 琳 | 椋";
        byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
        CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
        outputs( buf.toString() );
        boolean ok = str.equals( buf.toString() ) || die( buf.toString() );
    }

    @Test
    public void testJapaneseCharacters29() {
        String str = "鷹 | 麟";
        byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
        CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
        outputs( buf.toString() );
        boolean ok = str.equals( buf.toString() ) || die( buf.toString() );
    }

    @Test
    public void testChineseCharacters() {
        String str = "鳥 | 語 | 罐 | 佛"; //Bird speaks through tin can to prophet
        byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
        CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
        outputs( buf.toString() );
        boolean ok = str.equals( buf.toString() ) || die( buf.toString() );
    }

    private void outputs( String string ) {
    }


    @Test
    public void testUnicode() {

        StringBuilder builder = new StringBuilder( 64_000 );

        for (char cc = 32; cc < (char)1_114_112 ; cc++) {

            if (Character.isLetter( cc )) {
                builder.append(cc);
            }
        }

        String str = builder.toString();
        byte[] bytes = str.getBytes( StandardCharsets.UTF_8 );
        CharBuf buf = CharBuf.createFromUTF8Bytes( bytes );
        boolean ok = str.equals( buf.toString() ) || die( buf.toString() );

    }

    @Test
    public void testMe() {
        CharBuf buf = new CharBuf();
        buf.add( chars( "0123456789\n" ) );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456END\n" );


        String out = buf.toString();
        assertEquals( 66, out.length() );
        assertTrue( out.endsWith( "END\n" ) );

    }

    @Test
    public void testExact() {
        CharBuf buf = CharBuf.createExact( 66 );
        buf.add( chars( "0123456789\n" ) );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456789\n" );
        buf.add( "0123456END\n" );


        String out = buf.toString();
        assertEquals( 66, out.length() );
        assertTrue( out.endsWith( "END\n" ) );

    }

}
