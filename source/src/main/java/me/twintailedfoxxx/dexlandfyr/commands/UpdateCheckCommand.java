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
import me.twintailedfoxxx.dexlandfyr.objects.MessageAction;
import me.twintailedfoxxx.dexlandfyr.objects.Pair;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import me.twintailedfoxxx.dexlandfyr.util.VersionChecker;
import net.minecraft.client.entity.EntityPlayerSP;
import me.twintailedfoxxx.dexlandfyr.event.ClientChatEvent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UpdateCheckCommand extends DLFCommand {
    public UpdateCheckCommand() {
        super("updchk", "Проверить мод на доступные обновления", "update-check", "u");
    }

    @Override
    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        try {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                    "Ваша версия мода: &a" + DexLandFyr.VERSION));
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Проверка на наличие обновлений...");
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<Pair<String, Boolean>> task = service.submit(new VersionChecker());
            Pair<String, Boolean> pair = task.get();
            if (pair.getSecond()) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + "Обновлений не найдено. У Вас самая последняя версия мода.");
            } else {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&aДоступно обновление! " +
                                "&7(&e" + pair.getFirst() + "&7). &6&nНажмите на это сообщение, " +
                                "чтобы скачать последнюю версию мода."), MessageAction.OPEN_URL,
                        DexLandFyr.UPDATE_URL + "DexLandFyr-" + pair.getFirst() + "-1.12.2.jar",
                        Message.formatColorCodes('&', "&6Нажмите, чтобы открыть ссылку."));
            }
        } catch (InterruptedException | ExecutionException ex) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время проверки обновлений.");
        }
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&', "&6Эта команда проверяет наличие обновлений на данный мод.\n" +
                "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName();
    }
}
