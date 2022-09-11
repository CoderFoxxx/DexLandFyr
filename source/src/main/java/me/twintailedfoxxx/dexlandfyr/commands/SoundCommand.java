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

package me.twintailedfoxxx.dexlandfyr.commands;

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import me.twintailedfoxxx.dexlandfyr.objects.DLFCommand;
import me.twintailedfoxxx.dexlandfyr.objects.MessageCategory;
import me.twintailedfoxxx.dexlandfyr.objects.FyrConfiguration;
import me.twintailedfoxxx.dexlandfyr.objects.SoundEffect;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

import java.util.regex.Pattern;

public class SoundCommand extends DLFCommand {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public SoundCommand() {
        super("sound", "Настройка звуков при событиях.", "snd");
    }

    @Override
    public String[] getArgs() {
        return new String[]{"on&8/&eoff&8/&eсобытие", "on&8/&eoff&8/&eset", "minecraft:звук", "громкость", "высота"};
    }

    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length == 0) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Звуки:");
            Message.send(Message.formatColorCodes('&', "    &e• &fПри убийстве &8- " + ((conf.killSFXEnabled.getBoolean()) ? "&aвкл" : "&cвыкл") + " &6(" + SoundEffect.fromString(conf.killSFX.getString()) + "&6)"));
            Message.send(Message.formatColorCodes('&', "    &e• &fПри ломании кровати &8- " + ((conf.bedDestroySFXEnabled.getBoolean()) ? "&aвкл" : "&cвыкл") + " &6(" + SoundEffect.fromString(conf.bedDestroySFX.getString()) + "&6)"));
            Message.send(Message.formatColorCodes('&', "    &e• &fПри потере собственной кровати &8- " + ((conf.ownBedDestroyedSFXEnabled.getBoolean()) ? "&aвкл" : "&cвыкл") + " &6(" + SoundEffect.fromString(conf.ownBedDestroyedSFX.getString()) + "&6)"));
            Message.send(Message.formatColorCodes('&', "    &e• &fПри смерти &8- " + ((conf.deathSFXEnabled.getBoolean()) ? "&aвкл" : "&cвыкл") + " &6(" + SoundEffect.fromString(conf.deathSFX.getString()) + "&6)"));
            Message.send(Message.formatColorCodes('&', "    &e• &fПри начале игры &8- " + ((conf.gameStartSFXEnabled.getBoolean()) ? "&aвкл" : "&cвыкл") + " &6(" + SoundEffect.fromString(conf.gameStartSFX.getString()) + "&6)"));
            Message.send(Message.formatColorCodes('&', "    &e• &fПри завершении игры &8- " + ((conf.gameEndSFXEnabled.getBoolean()) ? "&aвкл" : "&cвыкл") + " &6(" + SoundEffect.fromString(conf.gameEndSFX.getString()) + "&6)"));
        } else {
            switch (args[0].toLowerCase()) {
                case "on":
                    conf.soundsEnabled.set(true);
                    conf.reload();
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &aвключили &7звуковые эффекты."));
                    break;
                case "off":
                    conf.soundsEnabled.set(false);
                    conf.reload();
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &cотключили &7звуковые эффекты."));
                    break;
                default:
                    if (args.length > 1) {
                        MessageCategory cat = MessageCategory.getByName(args[0].toLowerCase());
                        switch (args[1].toLowerCase()) {
                            case "on":
                                switch (cat) {
                                    case KILL:
                                        conf.killSFXEnabled.set(true);
                                        break;
                                    case BED:
                                        conf.bedDestroySFXEnabled.set(true);
                                        break;
                                    case OWN_BED:
                                        conf.ownBedDestroyedSFXEnabled.set(true);
                                        break;
                                    case DEATH:
                                        conf.deathSFXEnabled.set(true);
                                        break;
                                    case GAME_START:
                                        conf.gameStartSFXEnabled.set(true);
                                        break;
                                    case GAME_END:
                                        conf.gameEndSFXEnabled.set(true);
                                        break;
                                    default:
                                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Игрового события &e" + args[0] + "&7 не существует." + "&7."));
                                        return;
                                }

                                conf.reload();
                                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &aвключили &7звуковой эффект для события &e" + args[0] + "&7."));
                                break;
                            case "off":
                                switch (cat) {
                                    case KILL:
                                        conf.killSFXEnabled.set(false);
                                        break;
                                    case BED:
                                        conf.bedDestroySFXEnabled.set(false);
                                        break;
                                    case OWN_BED:
                                        conf.ownBedDestroyedSFXEnabled.set(false);
                                        break;
                                    case DEATH:
                                        conf.deathSFXEnabled.set(false);
                                        break;
                                    case GAME_START:
                                        conf.gameStartSFXEnabled.set(false);
                                        break;
                                    case GAME_END:
                                        conf.gameEndSFXEnabled.set(false);
                                        break;
                                    default:
                                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Игрового события &e" + args[0] + "&7 не существует." + "&7."));
                                        return;
                                }

                                conf.reload();
                                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &cотключили &7звуковой эффект для события &e" + args[0] + "&7."));
                                break;
                            case "set":
                                if (args.length > 3) {
                                    try {
                                        Pattern pattern = Pattern.compile("minecraft:\\w");
                                        if (pattern.matcher(args[2]).find()) {
                                            float volume = Float.parseFloat(args[3]);
                                            float pitch = Float.parseFloat(args[4]);

                                            switch (cat) {
                                                case KILL:
                                                    conf.killSFX.set(args[2] + "/" + volume + "/" + pitch);
                                                    break;
                                                case BED:
                                                    conf.bedDestroySFX.set(args[2] + "/" + volume + "/" + pitch);
                                                    break;
                                                case OWN_BED:
                                                    conf.ownBedDestroyedSFX.set(args[2] + "/" + volume + "/" + pitch);
                                                    break;
                                                case DEATH:
                                                    conf.deathSFX.set(args[2] + "/" + volume + "/" + pitch);
                                                    break;
                                                case GAME_START:
                                                    conf.gameStartSFX.set(args[2] + "/" + volume + "/" + pitch);
                                                    break;
                                                case GAME_END:
                                                    conf.gameEndSFX.set(args[2] + "/" + volume + "/" + pitch);
                                                    break;
                                                default:
                                                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Игрового события &e" + args[0] + "&7 не существует." + "&7."));
                                                    return;
                                            }

                                            conf.reload();
                                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы установили новый звуковой эффект &e'" + args[2] + "'&7 для события &6'" + args[0] + "'&7 с громкостью &a" + volume + "&7 и высотой &a" + pitch + "&7."));
                                        } else {
                                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Неправильный формат звука."));
                                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&c&lPROTIP! &7Список звуков доступен по этой ссылке: &6&nhttps://pastebin.com/vBPjnzHP"));
                                        }
                                    } catch (Exception ex) {
                                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время выполнения команды. Проверьте введенные данные и попробуйте снова.");
                                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&c&lPROTIP! &7Список звуков доступен по этой ссылке: &6&nhttps://pastebin.com/vBPjnzHP"));
                                        ex.printStackTrace();
                                    }
                                } else
                                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! &8.&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
                                break;
                        }
                    } else
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! &8.&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
                    break;
            }
        }
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет настраивать и переключать состояние звуковых эффектов при игровых событиях.\n" +
                        "&3Игровыми событиями являются &eубийство &o(kill)&3, &eсмерть &o(death)&3, &eразрушение кровати &o(bed)&3, &eпотеря кровати &o(ownbed)&3, " +
                        "&eначало игры &o(gamestart) &3и &eконец игры &o(gameend)&3.\n" +
                        "\n" +
                        "&6Использование подкоманд:\n" +
                        "&6Чтобы &aвключить&6/&cвыключить &6озвучку игровых событий, используйте:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "sound &aon&6/&coff\n" +
                        "\n" +
                        "&6Чтобы &aвключить&6/&cвыключить &6озвучку определенного события, используйте:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "sound &d[&eсобытие&d] &aon&6/&coff\n" +
                        "&fПример: &9&oКоманда, &cвыключающая&9&o озвучку события &e'Разрушенная кровать'&9&o, будет выглядеть следующим образом:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "sound &ebed &coff\n" +
                        "\n" +
                        "&6Чтобы установить другой звук для события, используйте:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "sound &d[&eсобытие&d] &eset &d[&bminecraft:звук&d] &d[&aгромкость&d] &d[&aвысота&d]\n" +
                        "&fПример: &9&oКоманда &eустанавливающая &9&oзвук &bminecraft:entity.villager.death &9&oсобытию &e&odeath &9&oс громкостью и высотой, равную &a1&9&o, будет выглядеть следующим образом:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "sound &e&odeath &eset &bminecraft:entity.villager.death &a1 1" +
                        "\n" +
                        "&c&lPROTIP! &7Список звуков Minecraft доступен по ссылке: &6&nhttps://pastebin.com/vBPjnzHP\n" +
                        "&6&nНажмите, чтобы написать эту команду в чат!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + "sound [on/off/событие] [on/off/set] [minecraft:звук] [громкость] [высота]";
    }
}