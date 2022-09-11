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
import me.twintailedfoxxx.dexlandfyr.util.Message;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

public class ResetSettingsCommand extends DLFCommand {
    public ResetSettingsCommand() {
        super("reset", "Сбросить настройки мода на значения по-умолчанию.", "rst");
    }

    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        boolean d = DexLandFyr.INSTANCE.configFile.delete();
        if (d) {
            DexLandFyr.INSTANCE.cfg.getConfiguration().load();
            DexLandFyr.INSTANCE.cfg.reload();
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Все настройки и сообщения были " +
                    "сброшены на значения по умолчанию."));
        }
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&', "&6Эта команда сбрасывает настройки и все сообщения со всех категорий на " +
                "значения по умолчанию.\n" +
                "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + "reset";
    }
}