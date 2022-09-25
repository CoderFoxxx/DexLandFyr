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
import me.twintailedfoxxx.dexlandfyr.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Events {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;
    private boolean gameBegan, isSolo, enteredAGame, usedLeaveCommand, bedBroken, updateChecked;
    private int killCount, deathCount, bedCount;
    private Stopwatch gameTimer;
    private GameTeam lpTeam;

    @SubscribeEvent
    public void onModCommandUsage(ClientChatEvent e) {
        if (e.getMessage().startsWith(DexLandFyr.INSTANCE.commandPrefix)) {
            String[] cmdArgs = Arrays.stream(e.getMessage().split(" ")).skip(1).toArray(String[]::new);
            if (DexLandFyr.INSTANCE.isEnabled) {
                if (Commands.execute(cmdArgs, e, Minecraft.getMinecraft().player)) {
                    assert Minecraft.getMinecraft().player != null;
                    DexLandFyr.LOGGER.debug("" + Minecraft.getMinecraft().player.getName() + " executed command.");
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
        if (event.getEntity() instanceof EntityPlayerSP) {
            if (!updateChecked) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + "Проверка на наличие обновлений...");
                try {
                    ExecutorService service = Executors.newSingleThreadExecutor();
                    Future<Pair<String[], Boolean>> future = service.submit(new VersionChecker());
                    Pair<String[], Boolean> pair = future.get();

                    if (pair.getSecond()) {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Обновлений не найдено. Удачной игры!");
                    } else {
                        String remoteVersion = pair.getFirst()[0];
                        boolean isImportant = remoteVersion.endsWith("!");
                        String[] changelog = pair.getFirst()[1].split(";");
                        if(isImportant) {
                            DexLandFyr.INSTANCE.isEnabled = false;
                            conf.modEnabled.set(false);
                            conf.reload();
                        }
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&a&lДоступно новое " +
                                "обновление! &7(&e&l" + remoteVersion.replace("!", "") + "&7)\n" +
                                "   &3Что нового?"));
                        for(String change : changelog) {
                            Message.send(Message.formatColorCodes('&', "    " + change));
                        }
                        Message.send(Message.formatColorCodes('&', (isImportant) ? "    &cЭто ВАЖНОЕ обновление, поэтому " +
                                "мод был отключён." : ""));
                        Message.send(Message.formatColorCodes('&', "&6&nНажмите здесь, чтобы скачать обновление."), MessageAction.OPEN_URL,
                                "https://raw.githubusercontent.com/CoderFoxxx/DexLandFyr/1.12.2/versions/1.12.2/DexLandFyr-"
                                        + remoteVersion.replace("!", "") + "-1.12.2.jar",
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
        String message = event.getMessage();
        if (message.startsWith("/") || message.startsWith(DexLandFyr.INSTANCE.commandPrefix)) return;

        if (!Objects.equals(conf.defaultChatColor.getString(), "null")) {
            if (!message.startsWith("!"))
                event.setMessage(conf.defaultChatColor.getString() + message + "&e");
            else {
                message = message.replace("!", "");
                event.setMessage("!" + conf.defaultChatColor.getString() + message + "&e");
            }
        }
    }

    @SubscribeEvent
    public void ignoreHandle(ClientChatReceivedEvent event) {
        String playerName = event.getMessage().getUnformattedText().split(" ")[1].replace(":", "");
        if (DexLandFyr.INSTANCE.blacklist.has(playerName)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void multicolouredChat(ClientChatEvent event) {
        if (event.getMessage().startsWith("/") || event.getMessage().startsWith(DexLandFyr.INSTANCE.commandPrefix) ||
                event.getMessage().contains("&") || !conf.modEnabled.getBoolean()) return;
        String oldMessage = event.getMessage();
        if (conf.mulColorEnabled.getBoolean()) {
            try {
                MulticolouredString multicolor = new MulticolouredString(event.getMessage(), conf.mulColorScheme.getString(),
                        conf.mulColorMirrored.getBoolean(), conf.isMulClrDynamic.getBoolean(), (gameBegan) ? 100 : 250);
                String newMessage = multicolor.processString((multicolor.isDynamic()) ? 1 : conf.colorPeriodicity.getInt());
                event.setMessage(newMessage + "&e");
            } catch (IllegalColorAmountException | OutOfMaximumCharactersLimitException |
                     PeriodicityOverflowException ex) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&eПредупреждение: &7" +
                        ex.getMessage()));
                event.setMessage(oldMessage);
            }
        }
    }

    @SubscribeEvent
    public void onGameEnter(ClientChatReceivedEvent event) {
        String message = event.getMessage().getUnformattedText();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        assert player != null;

        if (message.contains("BedWars ▸") && message.contains(player.getName() + " подключился к игре ") &&
                message.contains("/")) {
            String[] spl = message.split(" ");
            isSolo = spl[6].contains("/8");
            enteredAGame = true;
        }
    }

    @SubscribeEvent
    public void onGameStart(ClientChatReceivedEvent event) {
        ArrayList<String> gameStartMessages = new ArrayList<>(Arrays.asList(conf.gameStartCatMsgs.getStringList()));
        if (gameStartMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            String message = event.getMessage().getUnformattedText();

            if (enteredAGame && message.contains("Победит только одна, сильнейшая команда!")) {
                enteredAGame = usedLeaveCommand = false;
                gameBegan = true;
                gameTimer = Stopwatch.createStarted();
                if (conf.modEnabled.getBoolean() && conf.gameStartCategoryEnabled.getBoolean()) {
                    String rnd = getRandomString(gameStartMessages);
                    String gameStartMessage = getFormattedMessage(processPlaceholders(null,
                            null, false, rnd));
                    int maxChars = (gameStartMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(gameStartMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
                if (conf.soundsEnabled.getBoolean() && conf.gameStartSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.gameStartSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }
                killCount = deathCount = bedCount = 0;

                if(!Minecraft.getMinecraft().world.isRemote)
                    lpTeam = GameTeam.getPlayersTeam(Minecraft.getMinecraft().player.getName());
                else lpTeam = new GameTeam(RegisteredTeam.DEFAULT_TEAM);
            }
        }
    }

    @SubscribeEvent
    public void onKill(ClientChatReceivedEvent event) {
        ArrayList<String> killMessages = new ArrayList<>(Arrays.asList(conf.killCatMsgs.getStringList()));
        if (killMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            String message = event.getMessage().getUnformattedText();
            String playerName = Minecraft.getMinecraft().player.getName();

            if (gameBegan && message.contains("BedWars ▸") && message.contains("был убит игроком " + playerName)) {
                killCount += 1;
                if (conf.soundsEnabled.getBoolean() && conf.killSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.killSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }

                if (conf.modEnabled.getBoolean() && conf.killCategoryEnabled.getBoolean()) {
                    String killed = message.split(" ")[2];
                    String rnd = getRandomString(killMessages);
                    String killMessage = getFormattedMessage(processPlaceholders(null, killed,
                            false, rnd));
                    int maxChars = (killMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(killMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onVoidKill(ClientChatReceivedEvent event) {
        ArrayList<String> voidKillMessages = new ArrayList<>(Arrays.asList(conf.voidKillCatMsgs.getStringList()));
        if (voidKillMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            String message = event.getMessage().getUnformattedText();
            String playerName = Minecraft.getMinecraft().player.getName();

            if (gameBegan && message.contains("BedWars ▸") && message.contains("был скинут в бездну игроком " + playerName)) {
                killCount += 1;

                if (conf.soundsEnabled.getBoolean() && conf.killSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.killSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }

                if (conf.modEnabled.getBoolean() && conf.voidDeathCategoryEnabled.getBoolean()) {
                    String killed = message.split(" ")[2];
                    String rnd = getRandomString(voidKillMessages);
                    String killMessage = getFormattedMessage(processPlaceholders(null, killed,
                            false, rnd));
                    int maxChars = (killMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(killMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBedDestroy(ClientChatReceivedEvent event) {
        ArrayList<String> bedBreakMessages = new ArrayList<>(Arrays.asList(conf.bedCatMsgs.getStringList()));
        if (bedBreakMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            String playerName = Minecraft.getMinecraft().player.getName();
            String message = event.getMessage().getUnformattedText();
            if (message.contains("BedWars ▸") && message.contains("разрушил кровать команды") && (conf.soundsEnabled.getBoolean()
                    && conf.bedDestroySFXEnabled.getBoolean())) {
                SoundEffect sound = SoundEffect.fromString(conf.bedDestroySFX.getString());
                sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
            }

            if (gameBegan && message.contains("BedWars ▸") && message.contains(playerName + " разрушил кровать команды")) {
                bedCount += 1;
                if (conf.modEnabled.getBoolean() && conf.bedCategoryEnabled.getBoolean()) {
                    String rnd = getRandomString(bedBreakMessages);
                    String bedBreakMessage = getFormattedMessage(processPlaceholders(message,
                            null, true, rnd));
                    int maxChars = (bedBreakMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(bedBreakMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void ownBedDestroyed(ClientChatReceivedEvent event) {
        ArrayList<String> ownBedBrokenMessages = new ArrayList<>(Arrays.asList(conf.ownBedCatMsgs.getStringList()));
        if (ownBedBrokenMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            assert Minecraft.getMinecraft().world != null;
            String message = event.getMessage().getUnformattedText();

            if (gameBegan && message.contains("BedWars ▸") && message.contains("разрушил кровать команды") &&
                    message.contains(lpTeam.getTeamName(Case.NOMINATIVE, true))) {
                if (conf.soundsEnabled.getBoolean() && conf.ownBedDestroyedSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.ownBedDestroyedSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }

                bedBroken = true;
                if (conf.modEnabled.getBoolean() && conf.ownBedBrokenCategoryEnabled.getBoolean()) {
                    String rnd = getRandomString(ownBedBrokenMessages);
                    String whoBroke = message.split(" ")[3];
                    String ownBedBrokenMessage = getFormattedMessage(processPlaceholders(null,
                            whoBroke, false, rnd));
                    int maxChars = (ownBedBrokenMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(ownBedBrokenMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityByEntityDeath(ClientChatReceivedEvent event) {
        ArrayList<String> deathMessages = new ArrayList<>(Arrays.asList(conf.deathCatMsgs.getStringList()));
        if (deathMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            String message = event.getMessage().getUnformattedText();
            String playerName = Minecraft.getMinecraft().player.getName();

            if (gameBegan && message.contains("BedWars ▸") && (message.contains(playerName + " был убит игроком ") ||
                    message.contains(playerName + " был скинут в бездну игроком"))) {
                deathCount += 1;

                if (conf.soundsEnabled.getBoolean() && conf.deathSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.deathSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }

                if (bedBroken) {
                    writeStats();
                    return;
                }

                if ((conf.modEnabled.getBoolean() && conf.deathCategoryEnabled.getBoolean()) && !bedBroken) {
                    String rnd = getRandomString(deathMessages);
                    String killer = (message.contains("был скинут в бездну игроком")) ? message.split(" ")[8] :
                            message.split(" ")[6];
                    String deathMessage = getFormattedMessage(processPlaceholders(null, killer,
                            false, rnd));
                    int maxChars = (deathMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(deathMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onVoidDeath(ClientChatReceivedEvent event) {
        ArrayList<String> deathMessages = new ArrayList<>(Arrays.asList(conf.voidDeathCatMsgs.getStringList()));
        if (deathMessages.size() > 0) {
            assert Minecraft.getMinecraft().player != null;
            String message = event.getMessage().getUnformattedText();
            String playerName = Minecraft.getMinecraft().player.getName();

            if (gameBegan && message.contains("BedWars ▸") && (message.contains(playerName + " упал в бездну"))) {
                deathCount += 1;

                if (conf.soundsEnabled.getBoolean() && conf.deathSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.deathSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }

                if (bedBroken) {
                    writeStats();
                    return;
                }

                if (conf.modEnabled.getBoolean() && conf.voidDeathCategoryEnabled.getBoolean()) {
                    String rnd = getRandomString(deathMessages);
                    String deathMessage = getFormattedMessage(processPlaceholders(null,
                            null, false, rnd));
                    int maxChars = (deathMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(deathMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onChatSwear(ClientChatReceivedEvent event) {
        if (gameBegan) {
            ArrayList<String> swearReplies = new ArrayList<>(Arrays.asList(conf.swearRepliesCatMsgs.getStringList()));
            if (swearReplies.size() > 0) {
                String msg = event.getMessage().getUnformattedText();
                assert Minecraft.getMinecraft().player != null;
                for (String sw : DexLandFyr.INSTANCE.swearDictionary) {
                    if (msg.contains("Всем") && msg.toLowerCase().contains(sw)) {
                        String swearReply = getRandomString(swearReplies);
                        if (conf.modEnabled.getBoolean())
                            Minecraft.getMinecraft().player.sendChatMessage("!" + (swearReply.contains("{dc}") ?
                                    swearReply.replace("{dc}", (!Objects.equals(conf.defaultChatColor.getString(), "null")) ?
                                                    conf.defaultChatColor.getString() : "")
                                            .replace("{player}", msg.split(" ")[2])
                                    : swearReply.replace("{player}", msg.split(" ")[2])) + "&e");
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLeaveCommand(ClientChatEvent event) {
        if (event.getMessage().equalsIgnoreCase("/leave")) {
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
            assert Minecraft.getMinecraft().player != null;
            String message = event.getMessage().getUnformattedText();

            if (gameBegan && message.contains("Перезагрузка сервера через 10 секунд!") && !message.contains("→")) {
                gameBegan = false;
                usedLeaveCommand = false;
                writeStats();
                if (conf.soundsEnabled.getBoolean() && conf.gameEndSFXEnabled.getBoolean()) {
                    SoundEffect sound = SoundEffect.fromString(conf.gameEndSFX.getString());
                    sound.play(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player);
                }

                if (conf.modEnabled.getBoolean()) {
                    String rnd = getRandomString(gameEndMessages);
                    String gameEndMessage = getFormattedMessage(processPlaceholders(null,null,
                            false, rnd));
                    int maxChars = (gameEndMessage.contains("&")) ? 100 - 2 : 100;
                    Tuple<String, Boolean> t = sendable(gameEndMessage, maxChars);
                    if(t.getSecond()) {
                        Minecraft.getMinecraft().player.sendChatMessage("!" + t.getFirst() +
                                ((t.getFirst().contains("&")) ? "&e" : ""));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваше сообщение не было отправлено.");
                    }
                }
                if (gameTimer.isRunning()) gameTimer.reset();
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

    /*
    private String processPlaceholders(String playerNamePlhld, String killerNamePlhld, String str) {
        Team playerTeam = (playerNamePlhld != null) ? Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam
                (playerNamePlhld) : null;
        Team killerTeam = (killerNamePlhld != null) ? Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam
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
    */

    private String processPlaceholders(String rawMessage, String playerNamePlhld, boolean bed, String configMessage) {
        assert Minecraft.getMinecraft().world != null;
        if(!bed) {
            GameTeam playerTeam;
            if(!Minecraft.getMinecraft().world.isRemote)
                playerTeam = (playerNamePlhld != null) ? GameTeam.getPlayersTeam(playerNamePlhld
                        .replace(".", "")) : null;
            else playerTeam = new GameTeam(RegisteredTeam.DEFAULT_TEAM);

            for(String s : getTeamNamePatternMatches(configMessage)) {
                String[] params = s.substring(11).split("_");
                for(int i = 0; i < params.length; i++) params[i] = params[i].replace("{", "")
                        .replace("}", "");
                assert playerTeam != null;
                String r = playerTeam.getTeamName(Case.getCaseFromPlaceholder(params[0]), false);
                r = (Arrays.asList(params).contains("c")) ? playerTeam.getColorCode('§') + r : r;
                configMessage = configMessage.replace(s, (Arrays.asList(params).contains("l")) ? r.toLowerCase() : r);
            }

            return configMessage.replace("{player}", (playerNamePlhld != null) ? playerNamePlhld : "")
                    .replace("{ingame_kill_count}", String.valueOf(killCount))
                    .replace("{total_kill_count}", String.valueOf(conf.totalKills.getInt() + killCount))
                    .replace("{ingame_death_count}", String.valueOf(deathCount))
                    .replace("{player_team_color}", (playerTeam != null) ? playerTeam.getColorCode('§')
                            : "")
                    .replace("{dc}", (!Objects.equals(conf.defaultChatColor.getString(), "null")) ?
                            conf.defaultChatColor.getString() : "")
                    .replace("{gameTimer}", String.format("%d мин. %02d сек.", gameTimer.elapsed(TimeUnit.SECONDS)
                                    / 60,
                            gameTimer.elapsed(TimeUnit.SECONDS) % 60));
        } else {
            GameTeam brokenTeam;
            if(!Minecraft.getMinecraft().world.isRemote)
                brokenTeam = GameTeam.getByString(rawMessage);
            else brokenTeam = new GameTeam(RegisteredTeam.DEFAULT_TEAM);
            assert brokenTeam != null;

            for(String s : getTeamNamePatternMatches(configMessage)) {
                String[] params = s.substring(11).split("_");
                for(int i = 0; i < params.length; i++) params[i] = params[i].replace("{", "")
                        .replace("}", "");
                String r = brokenTeam.getTeamName(Case.getCaseFromPlaceholder(params[0]), !isSolo);
                r = (Arrays.asList(params).contains("c")) ? brokenTeam.getColorCode('§') + r : r;
                configMessage = configMessage.replace(s, (Arrays.asList(params).contains("l")) ? r.toLowerCase() : r);
            }

            return configMessage.replace("{ingame_beds_count}", String.valueOf(bedCount))
                    .replace("{total_beds_count}", String.valueOf(conf.totalBeds.getInt() + bedCount));
        }
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

    private List<String> getTeamNamePatternMatches(String s) {
        ArrayList<String> a = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\{team_name_(.*?)}").matcher(s);
        while (matcher.find()) {
            a.add(matcher.group());
        }

        return a;
    }

    private Tuple<String, Boolean> sendable(String s, int m) {
        Tuple<String, Boolean> t;
        if(s.length() < m) {
            t = new Tuple<>(s, true);
        } else {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                    "&eПредупреждение: &7Количество символов в Вашем сообщении превышает " +
                            "максимальное количество символов " + "(" + s.length() + ", лимит: 100). " +
                            "Пробую изменить сообщение и отправить его..."));
            m = 100;
            s = Message.stripColors(s, '&');
            if(s.length() < m)
                t = new Tuple<>(s, true);
            else t = new Tuple<>("", false);
        }

        return t;
    }
}