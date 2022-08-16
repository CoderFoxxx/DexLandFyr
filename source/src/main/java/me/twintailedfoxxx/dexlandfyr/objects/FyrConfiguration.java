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

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class FyrConfiguration {
    private final Configuration configuration;

    // Parameters section
    public Property modEnabled;
    public Property commandPrefix;
    public Property mulColorEnabled;
    public Property defaultChatColor;
    public Property mulColorScheme;
    public Property colorPeriodicity;
    public Property mulColorMirrored;
    public Property isMulClrDynamic;
    public Property totalKills;
    public Property totalBeds;

    // Categories section
    public Property killCategoryEnabled;
    public Property voidKillCategoryEnabled;
    public Property bedCategoryEnabled;
    public Property ownBedBrokenCategoryEnabled;
    public Property deathCategoryEnabled;
    public Property voidDeathCategoryEnabled;
    public Property gameStartCategoryEnabled;
    public Property gameEndCategoryEnabled;
    public Property swearRepliesCategoryEnabled;

    // Messages section
    public Property killCatMsgs;
    public Property voidKillCatMsgs;
    public Property bedCatMsgs;
    public Property ownBedCatMsgs;
    public Property deathCatMsgs;
    public Property voidDeathCatMsgs;
    public Property gameStartCatMsgs;
    public Property gameEndCatMsgs;
    public Property swearRepliesCatMsgs;

    // SoundEffects section
    public Property soundsEnabled;
    public Property gameStartSFXEnabled;
    public Property gameStartSFX;
    public Property killSFXEnabled;
    public Property killSFX;
    public Property bedDestroySFXEnabled;
    public Property bedDestroySFX;
    public Property ownBedDestroyedSFXEnabled;
    public Property ownBedDestroyedSFX;
    public Property deathSFXEnabled;
    public Property deathSFX;
    public Property gameEndSFXEnabled;
    public Property gameEndSFX;

    public FyrConfiguration(File file) {
        this.configuration = new Configuration(file);
        configuration.load();
        reload();
    }

    public void reload() {
        modEnabled = configuration.get("Parameters", "isEnabled", true, "Is mod enabled?");
        commandPrefix = configuration.get("Parameters", "commandPrefix", "f.",
                "Prefix for mod commands.");
        mulColorEnabled = configuration.get("Parameters", "mulColorEnabled", false,
                "Is message multicoloring enabled?");
        defaultChatColor = configuration.get("Parameters", "defaultChatColor", "null",
                "Default color for the chat");
        mulColorScheme = configuration.get("Parameters", "msgMulColorScheme", "af6+n",
                "These colors are used for message formatting");
        colorPeriodicity = configuration.get("Parameters", "colorPeriodicity", 2,
                "Message cut length for multiple message coloring");
        mulColorMirrored = configuration.get("Parameters", "colorMirroring", false,
                "Will colors be mirrored for more beauty?");
        isMulClrDynamic = configuration.get("Parameters", "isMulClrDynamic", true,
                "Will multicolor tool change color periodicity value for itself?");
        totalKills = configuration.get("Parameters", "totalKills", 0, "Total Kills.");
        totalBeds = configuration.get("Parameters", "totalBeds", 0, "Total beds.");

        killCategoryEnabled = configuration.get("Categories", "killCategory", true,
                "Is kill messages category enabled?");
        voidKillCategoryEnabled = configuration.get("Categories", "voidKillCategory", true,
                "Is void kill messages category enabled?");
        bedCategoryEnabled = configuration.get("Categories", "bedCategory", true,
                "Is bed messages category enabled?");
        ownBedBrokenCategoryEnabled = configuration.get("Categories", "ownBedBrokenCategory", true,
                "Is own bed broken messages category enabled?");
        deathCategoryEnabled = configuration.get("Categories", "deathCategory", true,
                "Is death messages category enabled?");
        voidDeathCategoryEnabled = configuration.get("Categories", "voidDeathCategory", true,
                "Is void death messages category enabled?");
        gameStartCategoryEnabled = configuration.get("Categories", "gameStartCategory", true,
                "Is game start messages category enabled?");
        gameEndCategoryEnabled = configuration.get("Categories", "gameEndCategory", true,
                "Is game end messages category enabled?");
        swearRepliesCategoryEnabled = configuration.get("Categories", "swearRepliesCategory", false,
                "Is swear replies messages category enabled?");

        killCatMsgs = configuration.get("Messages", "killMsgs",
                new String[]{"Я убил тебя, {player}! Я сейчас убил {ingame_kill_count} игроков, а вообще " +
                        "{total_kill_count}."}, "Messages that will be sent when player's opponent " +
                        "is killed.");
        voidKillCatMsgs = configuration.get("Messages", "voidKillMsgs",
                new String[]{"{player}, кажется, вы упали..."}, "Messages that will be sent when their " +
                        "opponent dies by void");
        bedCatMsgs = configuration.get("Messages", "bedMsgs",
                new String[]{"-кроватка, эх, стройте защиту лучше! За игру я сломал {ingame_bed_count} кроватей, " +
                        "а вообще {total_bed_count}"}, "Messages that will be sent when player breaks the bed.");
        ownBedCatMsgs = configuration.get("Messages", "ownBedMsgs",
                new String[]{"Кажется я свою кровать потерял... Ну и ладно."}, "Messages that will be sent " +
                        "when player's team loses their bed.");
        deathCatMsgs = configuration.get("Messages", "deathMsgs",
                new String[]{"Ой... Хорошо, теперь сосредоточились. Я умер {ingame_death_count} раз... Face palm."},
                "Message that will be sent when player dies.");
        voidDeathCatMsgs = configuration.get("Messages", "voidDeathMsgs",
                new String[]{"I believe I can fly... Умер я уже {ingame_death_count} раз..."},
                "Message that will be sent when player dies by void.");
        gameStartCatMsgs = configuration.get("Messages", "gameStartMsgs",
                new String[]{"gl hf!"}, "Message that will be sent when game begins");
        gameEndCatMsgs = configuration.get("Messages", "gameEndMsgs",
                new String[]{"gg!"}, "Message that will be sent when game ends.");
        swearRepliesCatMsgs = configuration.get("Messages", "swearReplies",
                new String[]{"{player}, не выражайся!"}, "Message that will be sent in reply of swear.");

        soundsEnabled = configuration.get("SoundEffects", "enabled", false,
                "Are event sounds enabled?");
        gameStartSFXEnabled = configuration.get("SoundEffects", "gameStartSFXEnabled", true,
                "Is game start event sound effect enabled?");
        gameStartSFX = configuration.get("SoundEffects", "gameStartSFX",
                "minecraft:block.note.pling/1/1", "Game start sound effect");
        killSFXEnabled = configuration.get("SoundEffects", "killSFXEnabled", true,
                "Is kill event sound effect enabled?");
        killSFX = configuration.get("SoundEffects", "killSFX",
                "minecraft:entity.experience_orb.pickup/1/2", "Kill event sound effect");
        bedDestroySFXEnabled = configuration.get("SoundEffects", "bedSFXEnabled", true);
        bedDestroySFX = configuration.get("SoundEffects", "bedSFX",
                "minecraft:entity.enderdragon.growl/1/1", "Bed destroy event sound effect");
        ownBedDestroyedSFXEnabled = configuration.get("SoundEffects", "ownBedSFXEnabled", true,
                "Is own bed destroyed event sound effect enabled?");
        ownBedDestroyedSFX = configuration.get("SoundEffects", "ownBedSFX",
                "minecraft:entity.wither.death/1/1", "Own bed destroyed event sound effect");
        deathSFXEnabled = configuration.get("SoundEffects", "deathSFXEnabled", true,
                "Is death event sound effect enabled?");
        deathSFX = configuration.get("SoundEffects", "deathSFX",
                "minecraft:entity.skeleton.death/1/1", "Death event sound effect");
        gameEndSFXEnabled = configuration.get("SoundEffects", "gameEndSFXEnabled", true,
                "Is game end event sound effect enabled?");
        gameEndSFX = configuration.get("SoundEffects", "gameEndSFX",
                "minecraft:entity.villager.yes/1/1", "Game end event sound effect");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}