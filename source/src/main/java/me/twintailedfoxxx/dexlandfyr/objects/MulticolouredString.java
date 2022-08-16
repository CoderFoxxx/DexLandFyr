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

package me.twintailedfoxxx.dexlandfyr.objects;

import com.google.common.base.Splitter;
import me.twintailedfoxxx.dexlandfyr.exceptions.IllegalColorAmountException;
import me.twintailedfoxxx.dexlandfyr.exceptions.OutOfMaximumCharactersLimitException;
import me.twintailedfoxxx.dexlandfyr.exceptions.PeriodicityOverflowException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MulticolouredString {
    private final String processedString;
    private final String scheme;
    private final boolean mirrored;
    private final boolean isDynamic;
    private final int maxChars;

    public MulticolouredString(String processedString, String scheme, boolean mirrored, boolean isDynamic,
                               int maxChars) {
        this.processedString = processedString;
        this.scheme = scheme;
        this.mirrored = mirrored;
        this.isDynamic = isDynamic;
        this.maxChars = maxChars;
    }

    public String processString(int periodicity) throws OutOfMaximumCharactersLimitException, IllegalColorAmountException,
            PeriodicityOverflowException {
        String[] splitScheme = scheme.split("\\+");

        char[] colorsString = splitScheme[0].toCharArray();
        ArrayList<Character> colors = new ArrayList<>();
        for (char c : colorsString) colors.add(c);

        StringBuilder formatsStr = new StringBuilder();
        char[] formats = splitScheme[1].toCharArray();
        if (formats.length > 0)
            for (char c : formats) formatsStr.append("&").append(c);

        if (colors.size() >= 2) {
            String[] sectionedMessage;
            StringBuilder newMessage;

            if (!isDynamic) {
                sectionedMessage = (processedString.startsWith("!")) ? iterable2StringArray(Splitter.fixedLength(periodicity)
                        .split(processedString.substring(1))) :
                        iterable2StringArray(Splitter.fixedLength(periodicity).split(processedString));

                if (mirrored)
                    colors.addAll(reverseList(new ArrayList<>(colors)));

                int k = 0;
                for (int i = 0; i < sectionedMessage.length; i++) {
                    if (k == colors.size()) k = 0;
                    sectionedMessage[i] = "&" + colors.get(k).toString() + formatsStr +
                            sectionedMessage[i];
                    k += 1;
                }

                newMessage = new StringBuilder();
                for (String str : sectionedMessage) newMessage.append(str);

                if (newMessage.length() <= maxChars) return (processedString.startsWith("!")) ? "!" + newMessage :
                        newMessage.toString();
                else throw new OutOfMaximumCharactersLimitException("Длина отформатированной строки достигла максимально " +
                        "возможного количества символов. Использую сообщение без форматирования...");
            } else {
                if (periodicity > maxChars)
                    throw new PeriodicityOverflowException("Частота изменения цветов достигла лимита. " +
                            "Использую сообщение без форматирования...");
                sectionedMessage = (processedString.startsWith("!")) ? iterable2StringArray(Splitter.fixedLength(periodicity)
                        .split(processedString.substring(1))) :
                        iterable2StringArray(Splitter.fixedLength(periodicity).split(processedString));
                if (mirrored) colors.addAll(reverseList(new ArrayList<>(colors)));

                int k = 0;
                for (int i = 0; i < sectionedMessage.length; i++) {
                    if (k == colors.size()) k = 0;
                    sectionedMessage[i] = "&" + colors.get(k).toString() + formatsStr + sectionedMessage[i];
                    k += 1;
                }
                newMessage = new StringBuilder();
                for (String s : sectionedMessage) newMessage.append(s);

                String r = (processedString.startsWith("!")) ? "!" + newMessage : newMessage.toString();
                if (r.length() >= maxChars) return processString(periodicity + 1);
                else return r;
            }
        } else
            throw new IllegalColorAmountException("В схеме находится менее двух цветов. Пожалуйста, добавьте " +
                    "хотя бы ещё один цвет. Использую сообщение без форматирования...");
    }

    public boolean isDynamic() {
        return isDynamic;
    }

    private static List<Character> reverseList(List<Character> arr) {
        List<Character> l = new ArrayList<>(arr);
        Collections.reverse(l);
        return l;
    }

    private static String[] iterable2StringArray(Iterable<String> i) {
        ArrayList<String> a = new ArrayList<>();
        i.forEach(a::add);
        return a.toArray(new String[0]);
    }
}