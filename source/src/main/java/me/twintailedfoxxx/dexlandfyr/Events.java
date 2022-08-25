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

package me.twintailedfoxxx.dexlandfyr;

import com.google.common.base.Stopwatch;
import me.twintailedfoxxx.dexlandfyr.exceptions.IllegalColorAmountException;
import me.twintailedfoxxx.dexlandfyr.exceptions.OutOfMaximumCharactersLimitException;
import me.twintailedfoxxx.dexlandfyr.exceptions.PeriodicityOverflowException;
import me.twintailedfoxxx.dexlandfyr.objects.*;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import me.twintailedfoxxx.dexlandfyr.util.VersionChecker;
import me.twintailedfoxxx.dexlandfyr.util.XMLParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Team;
import me.twintailedfoxxx.dexlandfyr.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Events {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;
    private boolean gameBegan, enteredAGame, usedLeaveCommand, bedBroken, updateChecked;
    private int killCount, deathCount, bedCount;
    private Stopwatch gameTimer;

    @SubscribeEvent
    public void onModCommandUsage(ClientChatEvent e) {
        if (e.message.startsWith(DexLandFyr.INSTANCE.commandPrefix)) {
            String[] cmdArgs = Arrays.stream(e.message.split(" ")).skip(1).toArray(String[]::new);
            if (DexLandFyr.INSTANCE.isEnabled) {
                if (Commands.execute(cmdArgs, e, Minecraft.getMinecraft().thePlayer)) {
                    assert Minecraft.getMinecraft().thePlayer != null;
                    DexLandFyr.logger.debug("" + Minecraft.getMinecraft().thePlayer.getName() + " executed command.");
                } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неизвестная команда. " +
                        "Введите &e" + DexLandFyr.INSTANCE.commandPrefix + "help&7, чтобы узнать список команд."));
            } else
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы не можете выполнять команды мода," +
                        " поскольку мод был отключён."));
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onServerJoin(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityPlayerSP) {
            if (!updateChecked) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + "Проверка на наличие обновлений...");
                try {
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    Future<Pair<String, Boolean>> future = service.submit(new VersionChecker());
                    Pair<String, Boolean> pair = future.get();

                    if (pair.getSecond()) {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Обновлений не найдено. Удачной игры!");
                    } else {
                        DexLandFyr.INSTANCE.isEnabled = false;
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&aДоступно обновление! " +
                                        "&7(&e" + pair.getFirst() + "&7). &6&nНажмите на это сообщение, " +
                                        "чтобы скачать последнюю версию мода."), MessageAction.OPEN_URL,
                                "https://github.com/CoderFoxxx/DexLandFyr/releases/download/1.8.9-RELEASE/DexLandFyr-" +
                                        pair.getFirst() + "-1.8.9.jar",
                                Message.formatColorCodes('&', "&6Нажмите, чтобы открыть ссылку."));
                    }
                    updateChecked = true;
                    service.shutdown();
                } catch (Exception ex) {
                    Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время проверки обновлений. " +
                            "Возможно, это связано с тем, что сервер не отвечает. Повторите попытку позднее.");
                    ex.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public void colouredChat(ClientChatEvent event) {
        if (!conf.modEnabled.getBoolean() || conf.mulColorEnabled.getBoolean()) return;
        String message = event.message;
        if (message.startsWith("/") || message.startsWith(DexLandFyr.INSTANCE.commandPrefix)) return;

        if (!Objects.equals(conf.defaultChatColor.getString(), "null")) {
            if (!message.startsWith("!"))
                event.message = conf.defaultChatColor.getString() + message + "&e";
            else {
                message = message.replace("!", "");
                event.message = ("!" + conf.defaultChatColor.getString() + message + "&e");
            }
        }
    }

    @SubscribeEvent
    public void multicolouredChat(ClientChatEvent event) {
        if (event.message.startsWith("/") || event.message.startsWith(DexLandFyr.INSTANCE.commandPrefix) ||
                event.message.contains("&") || !conf.modEnabled.getBoolean()) return;
        // TODO: Improve multicolouring tool
        if (conf.mulColorEnabled.getBoolean() && !event.message.contains("!")) {
            try {
                MulticolouredString multicolor = new MulticolouredString(event.message, conf.mulColorScheme.getString(),
                        conf.mulColorMirrored.getBoolean(), conf.isMulClrDynamic.getBoolean(), 100 - 2);
                String newMessage = multicolor.processString((multicolor.isDynamic()) ? 1 : conf.colorPeriodicity.getInt());
                Minecraft.getMinecraft().thePlayer.sendChatMessage(newMessage + "&e");
            } catch (IllegalColorAmountException | OutOfMaximumCharactersLimitException |
                     PeriodicityOverflowException ex) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&eПредупреждение: &7" +
                        ex.getMessage()));
            }
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void ignoreHandle(ClientChatReceivedEvent event) {
        String playerName = event.message.getUnformattedText().split(" ")[1].replace(":", "");
        if (DexLandFyr.INSTANCE.blacklist.has(playerName)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onGameEnter(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        assert player != null;

        if (message.contains("BedWars ▸") && message.contains(player.getName() + " подключился к игре ") &&
                message.contains("/")) enteredAGame = true;
    }

    @SubscribeEvent
    public void onGameStart(ClientChatReceivedEvent event) {
        ArrayList<String> gameStartMessages = new ArrayList<>(Arrays.asList(conf.gameStartCatMsgs.getStringList()));
        if (gameStartMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String message = event.message.getUnformattedText();

            if (enteredAGame && message.contains("Победит только одна, сильнейшая команда!")) {
                enteredAGame = usedLeaveCommand = false;
                gameBegan = true;
                gameTimer = Stopwatch.createStarted();
                if (conf.modEnabled.getBoolean() && conf.gameStartCategoryEnabled.getBoolean()) {
                    String gameStartMessage = getFormattedMessage(processPlaceholders(null, null,
                            getRandomString(gameStartMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + gameStartMessage +
                            ((gameStartMessage.contains("&")) ? "&r&e" : ""));
                }
                if (conf.soundsEnabled.getBoolean() && conf.gameStartSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.gameStartSFX.getString());
                    sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                }
                killCount = deathCount = bedCount = 0;
            }
        }
    }

    @SubscribeEvent
    public void onKill(ClientChatReceivedEvent event) {
        ArrayList<String> killMessages = new ArrayList<>(Arrays.asList(conf.killCatMsgs.getStringList()));
        if (killMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String message = event.message.getUnformattedText();
            String playerName = Minecraft.getMinecraft().thePlayer.getName();

            if (gameBegan && message.contains("BedWars ▸") && message.contains("был убит игроком " + playerName)) {
                killCount += 1;
                if (conf.soundsEnabled.getBoolean() && conf.killSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.killSFX.getString());
                    sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                }

                if (conf.modEnabled.getBoolean() && conf.killCategoryEnabled.getBoolean()) {
                    String killed = message.split(" ")[2];
                    String killMessage = getFormattedMessage(processPlaceholders(killed, playerName,
                            getRandomString(killMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + killMessage +
                            ((killMessage.contains("&")) ? "&r&e" : ""));
                }
            }
        }
    }

    @SubscribeEvent
    public void onVoidKill(ClientChatReceivedEvent event) {
        ArrayList<String> voidKillMessages = new ArrayList<>(Arrays.asList(conf.voidKillCatMsgs.getStringList()));
        if (voidKillMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String message = event.message.getUnformattedText();
            String playerName = Minecraft.getMinecraft().thePlayer.getName();

            if (gameBegan && message.contains("BedWars ▸") && message.contains("был скинут в бездну игроком " + playerName)) {
                killCount += 1;

                if (conf.soundsEnabled.getBoolean() && conf.killSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.killSFX.getString());
                    sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                }

                if (conf.modEnabled.getBoolean() && conf.voidKillCategoryEnabled.getBoolean()) {
                    String killed = message.split(" ")[2];
                    String killMessage = getFormattedMessage(processPlaceholders(killed, playerName,
                            getRandomString(voidKillMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + killMessage +
                            ((killMessage.contains("&")) ? "&r&e" : ""));
                }
            }
        }
    }

    @SubscribeEvent
    public void onBedDestroy(ClientChatReceivedEvent event) {
        ArrayList<String> bedBreakMessages = new ArrayList<>(Arrays.asList(conf.bedCatMsgs.getStringList()));
        if (bedBreakMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String playerName = Minecraft.getMinecraft().thePlayer.getName();
            String message = event.message.getUnformattedText();
            if (message.contains("BedWars ▸") && message.contains("разрушил кровать команды") && (conf.soundsEnabled.getBoolean()
                    && conf.bedDestroySFXEnabled.getBoolean())) {
                SoundEffect sound = SoundEffect.fromString(conf.bedDestroySFX.getString());
                sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
            }

            if (gameBegan && message.contains("BedWars ▸") && message.contains(playerName + " разрушил кровать команды")) {
                bedCount += 1;
                if (conf.modEnabled.getBoolean() && conf.bedCategoryEnabled.getBoolean()) {
                    String bedBreakMessage = getFormattedMessage(processPlaceholders(playerName, null,
                            getRandomString(bedBreakMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + bedBreakMessage +
                            ((bedBreakMessage.contains("&")) ? "&r&e" : ""));
                }
            }
        }
    }

    @SubscribeEvent
    public void livingUpdate(LivingEvent.LivingUpdateEvent event) {
        if(!bedBroken && gameBegan && event.entityLiving instanceof EntityPlayerSP &&
                (event.entityLiving.isPotionActive(Potion.blindness))) {
            ArrayList<String> ownBedBrokenMessages = new ArrayList<>(Arrays.asList(conf.ownBedCatMsgs.getStringList()));
            if (conf.soundsEnabled.getBoolean() && conf.ownBedDestroyedSFXEnabled.getBoolean()) {
                SoundEffect sound = SoundEffect.fromString(conf.ownBedDestroyedSFX.getString());
                sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
            }

            bedBroken = true;
            if (conf.modEnabled.getBoolean() && conf.ownBedBrokenCategoryEnabled.getBoolean()) {
                String ownBedBrokenMessage = getFormattedMessage(processPlaceholders(null, null,
                        getRandomString(ownBedBrokenMessages)));
                Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + ownBedBrokenMessage +
                        ((ownBedBrokenMessage.contains("&")) ? "&r&e" : ""));
            }
        }
    }

    @SubscribeEvent
    public void onEntityByEntityDeath(ClientChatReceivedEvent event) {
        ArrayList<String> deathMessages = new ArrayList<>(Arrays.asList(conf.deathCatMsgs.getStringList()));
        if (deathMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String message = event.message.getUnformattedText();
            String playerName = Minecraft.getMinecraft().thePlayer.getName();

            if (gameBegan && message.contains("BedWars ▸") && (message.contains(playerName + " был убит игроком ") ||
                    message.contains(playerName + " был скинут в бездну игроком"))) {
                deathCount += 1;

                if (conf.soundsEnabled.getBoolean() && conf.deathSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.deathSFX.getString());
                    sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                }

                if (bedBroken) writeStats();
                if (!bedBroken && conf.modEnabled.getBoolean() && conf.deathCategoryEnabled.getBoolean()) {
                    String killer = (message.contains("был скинут в бездну игроком")) ? message.split(" ")[8] :
                            message.split(" ")[6];
                    String deathMessage = getFormattedMessage(processPlaceholders(null, killer,
                            getRandomString(deathMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + deathMessage +
                            ((deathMessage.contains("&")) ? "&r&e" : ""));
                }
            }
        }
    }

    @SubscribeEvent
    public void onVoidDeath(ClientChatReceivedEvent event) {
        ArrayList<String> deathMessages = new ArrayList<>(Arrays.asList(conf.voidDeathCatMsgs.getStringList()));
        if (deathMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String message = event.message.getUnformattedText();
            String playerName = Minecraft.getMinecraft().thePlayer.getName();

            if (gameBegan && message.contains("BedWars ▸") && (message.contains(playerName + " упал в бездну"))) {
                deathCount += 1;

                if (conf.soundsEnabled.getBoolean() && conf.deathSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.deathSFX.getString());
                    sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                }

                if (bedBroken) writeStats();
                if (conf.modEnabled.getBoolean() && conf.voidDeathCategoryEnabled.getBoolean()) {
                    String deathMessage = getFormattedMessage(processPlaceholders(playerName, null,
                            getRandomString(deathMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + deathMessage +
                            ((deathMessage.contains("&")) ? "&r&e" : ""));
                }
            }
        }
    }

    @SubscribeEvent
    public void onChatSwear(ClientChatReceivedEvent event) {
        if (gameBegan) {
            ArrayList<String> swearReplies = new ArrayList<>(Arrays.asList(conf.swearRepliesCatMsgs.getStringList()));
            if (swearReplies.size() > 0) {
                String msg = event.message.getUnformattedText();
                assert Minecraft.getMinecraft().thePlayer != null;
                for (String sw : DexLandFyr.INSTANCE.swearDictionary) {
                    if (msg.contains("Всем") && msg.toLowerCase().contains(sw)) {
                        String swearReply = getRandomString(swearReplies);
                        if (conf.modEnabled.getBoolean() && conf.swearRepliesCategoryEnabled.getBoolean())
                            Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + (swearReply.contains("{dc}") ?
                                    swearReply.replace("{dc}", (!Objects.equals(conf.defaultChatColor.getString(), "null")) ?
                                                    conf.defaultChatColor.getString() : "")
                                            .replace("{player}", msg.split(" ")[2])
                                    : swearReply.replace("{player}", msg.split(" ")[2])) + "&r&e");
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLeaveCommand(ClientChatEvent event) {
        if (event.message.equalsIgnoreCase("/leave")) {
            usedLeaveCommand = true;
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                    "&eПредупреждение: &7Вы вышли из игры при помощи команды /leave. Ваше количество убийств, " +
                            "кроватей, достигнутое в покинутой Вами игре, ещё не было обнулено. При заходе на новую игру " +
                            "счётчики обнулятся."));
        }
    }

    @SubscribeEvent
    public void onGameEnd(ClientChatReceivedEvent event) {
        ArrayList<String> gameEndMessages = new ArrayList<>(Arrays.asList(conf.gameEndCatMsgs.getStringList()));
        if (gameEndMessages.size() > 0) {
            assert Minecraft.getMinecraft().thePlayer != null;
            String message = event.message.getUnformattedText();

            if (gameBegan && message.contains("Перезагрузка сервера через 10 секунд!") && !message.contains("→")) {
                gameBegan = false;
                if (conf.soundsEnabled.getBoolean() && conf.gameEndSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.gameEndSFX.getString());
                    sound.play(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer);
                }

                if (conf.modEnabled.getBoolean() && conf.gameEndCategoryEnabled.getBoolean()) {
                    String gameEndMessage = getFormattedMessage(processPlaceholders(null, null,
                            getRandomString(gameEndMessages)));
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("!" + gameEndMessage + "&e");
                }
                if (gameBegan && (message.contains("bw-lobby") || message.contains("bw-")) && !message.contains("[")) {
                    if (gameTimer.isRunning()) gameTimer.reset();
                    writeStats();
                }
                usedLeaveCommand = false;
            }
        }
    }

    private String getFormattedMessage(String xmlMessage) {
        Document document = XMLParser.parse(xmlMessage);
        String formattedMessage;
        assert document != null;
        if (document.getElementsByTagName("mulclr").getLength() != 0) {
            try {
                String txtContent = document.getElementsByTagName("Message").item(0).getTextContent();
                Node multicolourNode = document.getElementsByTagName("mulclr").item(0);
                String multicolouredPart = multicolourNode.getTextContent();

                MulticolouredString multicolour = new MulticolouredString(multicolouredPart,
                        (multicolourNode.getAttributes().getNamedItem("schema") != null) ?
                                multicolourNode.getAttributes().getNamedItem("schema").getTextContent() :
                                conf.mulColorScheme.getString(),
                        (multicolourNode.getAttributes().getNamedItem("mirrored") != null) ?
                                Boolean.parseBoolean(multicolourNode.getAttributes().getNamedItem("mirrored")
                                        .getTextContent()) : conf.mulColorMirrored.getBoolean(),
                        (multicolourNode.getAttributes().getNamedItem("dynamic") != null) ?
                                Boolean.parseBoolean(multicolourNode.getAttributes().getNamedItem("dynamic")
                                        .getTextContent()) : conf.isMulClrDynamic.getBoolean(),
                        Math.abs(((gameBegan) ? 100 : 250) - txtContent.length()));
                formattedMessage = txtContent.replace(multicolouredPart, multicolour.processString(multicolour.isDynamic()
                        ? 1 : (multicolourNode.getAttributes().getNamedItem("period") != null) ?
                        Integer.parseInt(multicolourNode.getAttributes().getNamedItem("period").getTextContent()) :
                        conf.colorPeriodicity.getInt()));
                if((gameBegan && formattedMessage.length() >= 100) || (formattedMessage.length() >= 250))
                    formattedMessage = document.getElementsByTagName("Message").item(0).getTextContent();
            } catch (OutOfMaximumCharactersLimitException | IllegalColorAmountException | PeriodicityOverflowException ex) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7" +
                        ex.getMessage()));
                ex.printStackTrace();
                formattedMessage = document.getElementsByTagName("Message").item(0).getTextContent();
            }
        } else formattedMessage = document.getElementsByTagName("Message").item(0).getTextContent();

        return formattedMessage.replace("§", "&");
    }

    private String processPlaceholders(String playerNamePlhld, String killerNamePlhld, String str) {
        Team playerTeam = (playerNamePlhld != null) ? Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam
                (playerNamePlhld) : null;
        Team killerTeam = (killerNamePlhld != null) ? Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam
                (killerNamePlhld) : null;

        return str.replace("{player}", (playerNamePlhld != null) ? playerNamePlhld : "")
                .replace("{ingame_kill_count}", String.valueOf(killCount))
                .replace("{total_kill_count}", String.valueOf(conf.totalKills.getInt() + killCount))
                .replace("{ingame_beds_count}", String.valueOf(bedCount))
                .replace("{total_beds_count}", String.valueOf(conf.totalBeds.getInt() + bedCount))
                .replace("{ingame_death_count}", String.valueOf(deathCount))
                .replace("{player_team_color}", (playerTeam != null) ? TeamColor.transformTeamNameIntoColorCode
                        (TeamColor.fromTeam(playerTeam)) : "")
                .replace("{killer}", (killerNamePlhld != null) ? killerNamePlhld : "")
                .replace("{killer_team_name}", (killerTeam != null) ? TeamColor.transformTeamNameIntoColorCode
                        (TeamColor.fromTeam(killerTeam)) : "")
                .replace("{dc}", (!Objects.equals(conf.defaultChatColor.getString(), "null")) ?
                        conf.defaultChatColor.getString() : "")
                .replace("{gameTimer}", String.format("%d мин. %02d сек.", gameTimer.elapsed(TimeUnit.SECONDS) / 60,
                        gameTimer.elapsed(TimeUnit.SECONDS) % 60));
    }

    private String getRandomString(ArrayList<String> a) {
        return a.get(new Random().nextInt(a.size()));
    }

    private void writeStats() {
        conf.totalKills.set(killCount + conf.totalKills.getInt());
        conf.totalBeds.set(bedCount + conf.totalBeds.getInt());

        if (!usedLeaveCommand) {
            killCount = deathCount = bedCount = 0;
        }
    }
}