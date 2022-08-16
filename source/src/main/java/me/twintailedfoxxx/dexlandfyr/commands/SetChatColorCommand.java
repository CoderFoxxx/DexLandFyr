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
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

import java.util.Objects;
import java.util.regex.Pattern;

public class SetChatColorCommand extends DLFCommand {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public SetChatColorCommand() {
        super("defclr", "Установить цвет чата по умолчанию. Используйте данную возможность, " +
                "если у Вас есть привилегия, позволяющая писать цветные сообщения.", "defaultcolor", "setcc", "cc");
    }

    @Override
    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length != 0) {
            if (Objects.equals(args[0], "reset")) {
                conf.defaultChatColor.set("null");
                conf.reload();
                Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваш цвет чата был сброшен.");
                return;
            }

            Pattern digitColorsPattern = Pattern.compile("&[0-9a-fk-or]");

            if (digitColorsPattern.matcher(args[0].toLowerCase()).find()) {
                conf.defaultChatColor.set(args[0]);
                conf.reload();
                Message.send(DexLandFyr.MESSAGE_PREFIX + "Ваш цвет чата по умолчанию был установлен на " + Message.formatColorCodes('&', args[0] + "такой&7.)"));
            } else {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Такого цвета не существует. Используйте формат &[0-9] или &[a-f]"));
            }
        } else
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Вы не указали цвет."));
    }

    @Override
    public String[] getArgs() {
        return new String[]{"цвет&8/&ereset"};
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет одинарный цвет для чата и автоматических сообщений.\n" +
                        "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName() + " [цвет (или reset для сброса цвета)]";
    }
}
