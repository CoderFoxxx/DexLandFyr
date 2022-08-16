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

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import net.minecraftforge.common.config.Property;

import java.util.HashMap;
import java.util.Map;

public enum MessageCategory {
    KILL("kill", "Убийства"),
    VOID_KILL("voidkill", "Брошение противника в бездну"),
    BED("bed", "Сломанная кровать"),
    OWN_BED("ownbed", "Ваша сломанная кровать"),
    GAME_END("gameend", "Конец игры"),
    DEATH("death", "Смерти"),
    VOID_DEATH("voiddeath", "Смерти в бездне"),
    GAME_START("gamestart", "Начало игры"),
    SWEAR_REPLY("swearreply", "Ответы на нецензурные слова");

    private static final Map<String, MessageCategory> BY_NAME = new HashMap<>();

    static {
        for (MessageCategory type : values())
            BY_NAME.put(type.getName(), type);
    }

    final String name;
    final String displayName;

    MessageCategory(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public static MessageCategory getByName(String name) {
        return BY_NAME.get(name);
    }

    public Property getConfigMessagesProperty() {
        FyrConfiguration cfg = DexLandFyr.INSTANCE.cfg;
        switch (this) {
            case KILL: return cfg.killCatMsgs;
            case VOID_KILL: return cfg.voidKillCatMsgs;
            case BED: return cfg.bedCatMsgs;
            case OWN_BED: return cfg.ownBedCatMsgs;
            case GAME_START: return cfg.gameStartCatMsgs;
            case DEATH: return cfg.deathCatMsgs;
            case VOID_DEATH: return cfg.voidDeathCatMsgs;
            case GAME_END: return cfg.gameEndCatMsgs;
            case SWEAR_REPLY: return cfg.swearRepliesCatMsgs;
        }

        return null;
    }

    public Property getToggleProperty() {
        FyrConfiguration cfg = DexLandFyr.INSTANCE.cfg;
        switch (this) {
            case KILL: return cfg.killCategoryEnabled;
            case VOID_KILL: return cfg.voidKillCategoryEnabled;
            case BED: return cfg.bedCategoryEnabled;
            case OWN_BED: return cfg.ownBedBrokenCategoryEnabled;
            case GAME_START: return cfg.gameStartCategoryEnabled;
            case DEATH: return cfg.deathCategoryEnabled;
            case VOID_DEATH: return cfg.voidDeathCategoryEnabled;
            case GAME_END: return cfg.gameEndCategoryEnabled;
            case SWEAR_REPLY: return cfg.swearRepliesCategoryEnabled;
        }

        return null;
    }

    public String getName() {
        return name;
    }
    public String getDisplayName() {
        return displayName;
    }
}