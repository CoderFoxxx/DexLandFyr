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

import java.util.HashMap;
import java.util.Map;

public enum Case {
    NOMINATIVE("n"),
    GENITIVE("g"),
    DATIVE("d"),
    ACCUSATIVE("a"),
    INSTRUMENTAL("i"),
    PREPOSITIONAL("pre");
    private static final Map<String, Case> BY_PLACEHOLDER = new HashMap<>();

    static {
        for (Case c : values())
            BY_PLACEHOLDER.put(c.getPlaceholder(), c);
    }

    final String pl;

    Case(String pl) {
        this.pl = pl;
    }

    public static Case getCaseFromPlaceholder(String s) {
        return BY_PLACEHOLDER.get(s);
    }

    public String getPlaceholder() {
        return pl;
    }

    public String getDisplayName() {
        switch (this) {
            case NOMINATIVE: return "Именительный падеж";
            case GENITIVE:  return "Родительный падеж";
            case DATIVE: return "Дательный падеж";
            case ACCUSATIVE: return "Винительный падеж";
            case INSTRUMENTAL: return "Творительный падеж";
            case PREPOSITIONAL: return "Предложный падеж";
        }

        return null;
    }
}
