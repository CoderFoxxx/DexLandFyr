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

import net.minecraft.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public enum RegisteredTeam {
    RED("0_red"),
    BLUE("0_blue"),
    GREEN("0_green"),
    YELLOW("0_yellow"),
    AQUA("0_aqua"),
    WHITE("0_white"),
    PINK("0_light_purple"),
    GRAY("0_gray"),
    DEFAULT_TEAM("9_default_team");

    private static final Map<String, RegisteredTeam> BY_NAME = new HashMap<>();

    static {
        for (RegisteredTeam team : values())
            BY_NAME.put(team.getName(), team);
    }

    final String name;

    RegisteredTeam(String name) {
        this.name = name;
    }

    public static RegisteredTeam fromPlayersTeam(Team team) {
        return BY_NAME.get(team.getName());
    }

    public String getName() {
        return name;
    }
}