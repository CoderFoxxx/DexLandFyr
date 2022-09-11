/*
 * MIT License
 *
 * Copyright (c) 2022 Nick "CoderFoxxx", "TwinTailedFoxxx" Moonshine
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.twintailedfoxxx.dexlandfyr.util;

import me.twintailedfoxxx.dexlandfyr.objects.MessageAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;

import java.util.Objects;

public class Message {
    private final static int CENTER_PX = 154;

    public static void send(String message) {
        assert Minecraft.getMinecraft().thePlayer != null;
        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(Objects.requireNonNull
                (ChatComponentText.Serializer.jsonToComponent("{\"text\": \"" + message + "\"}")));
    }

    public static void send(String message, MessageAction action, String value, String hoverString) {
        EntityPlayerSP lPlayer = Minecraft.getMinecraft().thePlayer;
        assert lPlayer != null;
        switch (action) {
            case OPEN_URL:
                lPlayer.addChatComponentMessage(Objects.requireNonNull(ChatComponentText.Serializer
                        .jsonToComponent(
                                "{" +
                                        "\"text\": \"" + message + "\"," +
                                        "\"clickEvent\": {" +
                                        "\"action\": \"open_url\"," +
                                        "\"value\": \"" + value + "\"" +
                                        "}," +
                                        "\"hoverEvent\": {" +
                                        "\"action\": \"show_text\"," +
                                        "\"value\": \"" + hoverString + "\"" +
                                        "}" +
                                        "}")));
                break;
            case SUGGEST_COMMAND:
                lPlayer.addChatComponentMessage(Objects.requireNonNull(ChatComponentText.Serializer
                        .jsonToComponent(
                                "{" +
                                        "\"text\": \"" + message + "\"," +
                                        "\"clickEvent\": {" +
                                        "\"action\": \"suggest_command\"," +
                                        "\"value\": \"" + value + "\"" +
                                        "}," +
                                        "\"hoverEvent\": {" +
                                        "\"action\": \"show_text\"," +
                                        "\"value\": \"" + hoverString + "\"" +
                                        "}" +
                                        "}")));
                break;
        }
    }

    public static String centeredString(String msg) {
        msg = formatColorCodes('&', msg);
        int msgPxSize = 0;
        boolean isBold = false;
        boolean prevCode = false;

        for (char c : msg.toCharArray()) {
            if (c == '§')
                prevCode = true;
            else if (prevCode) {
                prevCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo defFontInf = DefaultFontInfo.getDefaultFontInfo(c);
                msgPxSize += isBold ? defFontInf.getBoldLength() : defFontInf.getLength();
                msgPxSize += 1;
            }
        }

        int halvedMsgPxSize = msgPxSize / 2;
        int toCompensate = CENTER_PX - halvedMsgPxSize;
        int spaceLen = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder result = new StringBuilder();
        while (compensated < toCompensate) {
            result.append(" ");
            compensated += spaceLen;
        }

        return result + msg;
    }

    public static String formatColorCodes(char c, String s) {
        return s.replace(c, '§');
    }

    public static String stripColors(String cs, char symbol) {
        return cs.replaceAll(symbol + "[0-9a-fk-or]", "");
    }
}
