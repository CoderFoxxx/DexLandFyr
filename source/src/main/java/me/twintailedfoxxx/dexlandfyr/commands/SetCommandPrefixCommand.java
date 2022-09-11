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
import me.twintailedfoxxx.dexlandfyr.event.ClientChatEvent;

public class SetCommandPrefixCommand extends DLFCommand {
    private final FyrConfiguration cfg = DexLandFyr.INSTANCE.cfg;

    public SetCommandPrefixCommand() {
        super("setcmdprefix", "Установить префикс для запускаемых команд.", "prefix");
    }

    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length == 0) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! " +
                    "&8" + DexLandFyr.INSTANCE.commandPrefix + "&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
        } else {
            String newCommandPrefix = args[0];
            String oldCommandPrefix = DexLandFyr.INSTANCE.commandPrefix;
            if (newCommandPrefix.length() > 0 && newCommandPrefix.length() <= 3 && !newCommandPrefix.startsWith("!") &&
                    !newCommandPrefix.startsWith("/") && !newCommandPrefix.contains(" ") && !newCommandPrefix.contains("&")) {
                DexLandFyr.INSTANCE.commandPrefix = newCommandPrefix;
                cfg.commandPrefix.set(newCommandPrefix);
                cfg.reload();
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                        "Вы установили новый префикс для команд. &e'" + oldCommandPrefix + "' &d→ &e'"
                                + newCommandPrefix + "'&7. Теперь команды будут работать, если Ваше сообщение будет " +
                                "начинаться с &e" + newCommandPrefix + "&7."));
            } else {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Префикс для " +
                        "команд должен состоять только из не более &a3&7 символов, не должен начинаться с &4'/' &7и &4'!' и" +
                        " не может содержать в себе символ аперсанта и символ пробела."));
            }
        }
    }

    @Override
    public String[] getArgs() {
        return new String[]{"новый-префикс"};
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&', "&6Эта команда позволяет Вам изменить префикс для выполнения команд. \n" +
                "\n" +
                "&9&oЧтобы поменять префикс с текущего префикса &7(" + DexLandFyr.INSTANCE.commandPrefix + ")&9&o на &ef*&9&o, команда " +
                "будет выглядеть следующим образом:\n" +
                "&7" + DexLandFyr.INSTANCE.commandPrefix + getName() + " &ef*\n" +
                "&6После выполнения этой команды, все команды мода обретут следующий вид:\n" +
                "&ef*&7addmsg&6,\n" +
                "&ef*&7setmsg&6,\n" +
                "&ef*&7removemsg&6, &7&oи т.д.\n" +
                "\n" +
                "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName() + " [новый-префикс]";
    }
}