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

import me.twintailedfoxxx.dexlandfyr.util.Case;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameTeam
{
    private final RegisteredTeam team;
    private boolean isBroken;
    private List<String> players;

    public GameTeam(RegisteredTeam team) {
        this.team = team;
        this.isBroken = false;
    }

    public GameTeam(RegisteredTeam team, List<String> players) {
        this.team = team;
        this.isBroken = false;
        this.players = players;
    }

    public static GameTeam getPlayersTeam(String player) {
        assert Minecraft.getMinecraft().world != null;
        return new GameTeam(RegisteredTeam.fromPlayersTeam(Objects.requireNonNull(Minecraft.getMinecraft().world.
                getScoreboard().getPlayersTeam(player))));
    }

    public static GameTeam getFromTeam(Team team) {
        return new GameTeam(RegisteredTeam.fromPlayersTeam(team), new ArrayList<>(team.getMembershipCollection()));
    }

    public static GameTeam getByString(String s) {
        String[] msg = s.split(" ");
        switch (msg[6].replace("!", "")) {
            case "Красные": return new GameTeam(RegisteredTeam.RED);
            case "Синие": return new GameTeam(RegisteredTeam.BLUE);
            case "Зеленые": return new GameTeam(RegisteredTeam.GREEN);
            case "Желтые": return new GameTeam(RegisteredTeam.YELLOW);
            case "Голубые": return new GameTeam(RegisteredTeam.AQUA);
            case "Белые": return new GameTeam(RegisteredTeam.WHITE);
            case "Розовые": return new GameTeam(RegisteredTeam.PINK);
            case "Серые": return new GameTeam(RegisteredTeam.GRAY);
        }

        return null;
    }

    public String getColorCode(char codeSymbol) {
        switch (team) {
            case RED: return codeSymbol + "c";
            case BLUE: return codeSymbol + "9";
            case GREEN: return codeSymbol + "a";
            case YELLOW: return codeSymbol + "e";
            case AQUA: return codeSymbol + "b";
            case WHITE: return codeSymbol + "f";
            case PINK: return codeSymbol + "d";
            case GRAY: return codeSymbol + "7";
            case DEFAULT_TEAM: return codeSymbol + "7" + codeSymbol + "o";
        }

        return null;
    }

    public String getTeamName(Case c, boolean isPlural) {
        switch (team) {
            case RED:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Красные" : "Красный";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Красных" : "Красного";
                    case DATIVE: return (isPlural) ? "Красным" : "Красному";
                    case INSTRUMENTAL: return (isPlural) ? "Красными" : "Красным";
                    case PREPOSITIONAL: return (isPlural) ? "Красных" : "Красном";
                }
                break;
            case BLUE:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Синие" : "Синий";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Синих" : "Синего";
                    case DATIVE: return (isPlural) ? "Синим" : "Синему";
                    case INSTRUMENTAL: return (isPlural) ? "Синими" : "Синим";
                    case PREPOSITIONAL: return (isPlural) ? "Синих" : "Синем";
                }
                break;
            case GREEN:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Зелёные" : "Зелёный";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Зелёных" : "Зелёного";
                    case DATIVE: return (isPlural) ? "Зелёным" : "Зелёному";
                    case INSTRUMENTAL: return (isPlural) ? "Зелёными" : "Зелёным";
                    case PREPOSITIONAL: return (isPlural) ? "Зелёных" : "Зелёном";
                }
                break;
            case YELLOW:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Жёлтые" : "Жёлтый";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Жёлтых" : "Жёлтого";
                    case DATIVE: return (isPlural) ? "Жёлтым" : "Жёлтому";
                    case INSTRUMENTAL: return (isPlural) ? "Жёлтыми" : "Жёлтым";
                    case PREPOSITIONAL: return (isPlural) ? "Жёлтых" : "Жёлтом";
                }
                break;
            case AQUA:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Голубые" : "Голубой";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Голубых" : "Голубого";
                    case DATIVE: return (isPlural) ? "Голубым" : "Голубому";
                    case INSTRUMENTAL: return (isPlural) ? "Голубыми" : "Голубым";
                    case PREPOSITIONAL: return (isPlural) ? "Голубых" : "Голубом";
                }
                break;
            case WHITE:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Белые" : "Белый";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Белых" : "Белого";
                    case DATIVE: return (isPlural) ? "Белым" : "Белому";
                    case INSTRUMENTAL: return (isPlural) ? "Белыми" : "Белым";
                    case PREPOSITIONAL: return (isPlural) ? "Белых" : "Белом";
                }
                break;
            case PINK:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Розовые" : "Розовый";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Розовых" : "Розового";
                    case DATIVE: return (isPlural) ? "Розовым" : "Розовому";
                    case INSTRUMENTAL: return (isPlural) ? "Розовыми" : "Розовым";
                    case PREPOSITIONAL: return (isPlural) ? "Розовых" : "Розовом";
                }
                break;
            case GRAY:
                switch (c) {
                    case NOMINATIVE: return (isPlural) ? "Серые" : "Серый";
                    case GENITIVE: case ACCUSATIVE: return (isPlural) ? "Серых" : "Серого";
                    case DATIVE: return (isPlural) ? "Серым" : "Серому";
                    case INSTRUMENTAL: return (isPlural) ? "Серыми" : "Серым";
                    case PREPOSITIONAL: return (isPlural) ? "Зелёных" : "Зелёном";
                }
                break;
            case DEFAULT_TEAM: return "Команда по-умолчанию";
        }

        return null;
    }


    //TODO: Get these into the action
    public boolean isBroken() { return isBroken; }
    public void setBroken(boolean broken) { this.isBroken = broken; }
    public List<String> getPlayers() { return players; }
}