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
import me.twintailedfoxxx.dexlandfyr.objects.FyrConfiguration;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

public class ToggleCommand extends DLFCommand {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public ToggleCommand() {
        super("toggle", "Включить/выключить функционал мода.", "tgl", "t");
    }

    @Override
    public String[] getArgs() {
        return new String[]{"on&8/&eoff"};
    }

    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length == 0) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! &8.&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
            return;
        } else {
            switch (args[0]) {
                case "on":
                    if (!conf.modEnabled.getBoolean()) {
                        conf.modEnabled.set(true);
                        conf.reload();
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Функционал мода уже включен");
                        return;
                    }
                    break;
                case "off":
                    if (conf.modEnabled.getBoolean()) {
                        conf.modEnabled.set(false);
                        conf.reload();
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Функционал мода уже выключен");
                        return;
                    }
                    break;
                default:
                    conf.modEnabled.set(!conf.modEnabled.getBoolean());
                    conf.reload();
                    break;
            }
        }

        Message.send(Message.formatColorCodes('&', DexLandFyr.MESSAGE_PREFIX + "Вы " + (conf.modEnabled.getBoolean() ? "&aвключили" : "&cвыключили") + " &7функционал мода."));
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет включать и отключать автоматическую отправку сообщений." +
                        "\n" +
                        "\n" +
                        "&6Чтобы &aвключить&6/&cвыключить &6автоматическую отправку сообщений, используйте:\n" +
                        "&7" + DexLandFyr.INSTANCE.commandPrefix + "toggle &aon&8/&coff\n" +
                        "\n" +
                        "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName() + " [on/off]";
    }
}
