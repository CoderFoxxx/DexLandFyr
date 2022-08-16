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
import me.twintailedfoxxx.dexlandfyr.objects.MulticolouredString;
import me.twintailedfoxxx.dexlandfyr.util.Commands;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import me.twintailedfoxxx.dexlandfyr.util.XMLParser;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

import java.util.Objects;
import java.util.regex.Pattern;

public class MultipleColorMessageCommand extends DLFCommand {
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public MultipleColorMessageCommand() {
        super("mulclr", "Установка нескольких цветов в сообщении.", "mclr", "multicolor");
    }

    @Override
    public String[] getArgs() {
        return new String[]{"on&8/&eoff&8/&eset&8/&eperiod", "параметр"};
    }

    @Override
    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if (args.length < 1) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! " + DexLandFyr.INSTANCE.commandPrefix + "&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
            return;
        }

        Pattern digitColorsPattern = Pattern.compile("&[0-9a-fk-or]");

        if ((Objects.equals(args[0], "add") || Objects.equals(args[0], "remove")) && args.length == 2 &&
                !digitColorsPattern.matcher(args[1].toLowerCase()).find()) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Вы указали недействительный цвет."));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "on":
                if (!conf.mulColorEnabled.getBoolean()) {
                    conf.mulColorEnabled.set(true);
                    conf.reload();
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &aвключили &7разноцветные сообщения"));
                } else
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Разноцветные сообщения &aуже &7включены."));
                break;
            case "off":
                if (conf.mulColorEnabled.getBoolean()) {
                    conf.mulColorEnabled.set(false);
                    conf.reload();
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &свыключили &7разноцветные сообщения"));
                } else
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Разноцветные сообщения &aуже &7выключены."));
                break;
            case "set":
                String chatFormatString = args[1].toLowerCase();
                Pattern pattern;
                if (chatFormatString.contains("+")) pattern = Pattern.compile("[0-9a-f]\\+[k-o]");
                else pattern = Pattern.compile("[0-9a-f]");
                if (pattern.matcher(chatFormatString).find()) {
                    conf.mulColorScheme.set(chatFormatString);
                    conf.reload();
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы установили " +
                            "последовательность форматирования: &6" + chatFormatString + "&7."));
                } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                        "&cОшибка: &7Вы неверно указали последовательность форматирования. Формат: цвета+формат-текста"));
                break;
            case "period":
                if (args.length < 2) {
                    Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Неверное использование команды! " + DexLandFyr.INSTANCE.commandPrefix + "&7&o" + getName()) + " " + Commands.argsToString(getArgs()));
                    return;
                }

                try {
                    if (Integer.parseInt(args[1]) > 0) {
                        conf.colorPeriodicity.set(Integer.parseInt(args[1]));
                        conf.reload();
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы установили частоту " +
                                "смены цвета: &6" + args[1]));
                    } else {
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Длина" +
                                " обрезки сообщения должна быть больше нуля."));
                    }
                } catch (Exception ex) {
                    Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время выполнения команды. Проверьте введенные данные и попробуйте снова.");
                    ex.printStackTrace();
                }
                break;
            case "mirroring":
                switch (args[1].toLowerCase()) {
                    case "on":
                        conf.mulColorMirrored.set(true);
                        conf.reload();
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &aвключили&7 зеркальность цветов."));
                        break;
                    case "off":
                        conf.mulColorMirrored.set(false);
                        conf.reload();
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &cотключили&7 зеркальность цветов."));
                        break;
                    default:
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7Неизвестная подкоманда."));
                        break;
                }
                break;
        }
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет настраивать разноцветные сообщения. " +
                        "\n" +
                        "&6Чтобы &aвключить&6/&cвыключить &6использование разноцветных сообщений, используйте команду:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&emulclr &aon&e/&coff\n" +
                        "\n" +
                        "&6Чтобы установить палитру цветов, используйте команду:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&emulclr &bset &6<палитра>\n" +
                        "&fПалитра цветов выглядит следующим образом: &o6ea2+n&f. Символы до плюса - цвета, после - форматирование.\n" +
                        "&fВ этом случае будут использоваться цвета: &6золотой&f, &eжёлтый&f, &aсветло-зелёный&f, &2тёмно-зелёный &f" +
                        "и вся строка будет &nподчёркнутой&f. " + XMLParser.getFormattedMessage("<Message><mulclr schema='6ea2+n' " +
                        "mirrored='false' dynamic='false' period='1'>Так будет выглядеть обработанная строка с данной схемой цветов.</mulclr></Message>") +
                        "\n" +
                        "&6Чтобы изменить частоту смены цветов, используйте команду:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&emulclr &bperiod &a<целочисленное положительное значение>\n" +
                        "&7&oЧем меньше значение частоты смены цветов, тем чаще меняется цвет.\n" +
                        "\n" +
                        "&6Вы можете использовать отзеркаленные цвета в сообщении. Чтобы включить зеркальность, используйте команду:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&emulclr &bmirroring &eon&e/&coff\n" +
                        "\n" +
                        "&6О теге &9<mulclr>\n" +
                        "&fТег &9<mulclr> &fиспользуется только в автоматических сообщениях. Он используется для того, " +
                        "чтобы выделить определенную часть текста сообщения разными цветами.\n" +
                        "Пример:\n" +
                        "&f&oВы добавляете сообщение в категорию &3&o'Убийства'&f&o:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&ecategory &bkill &daddmsg &eЯ убил тебя, " +
                        "&9<mulclr &6schema=&7'f4c6+o' &6mirror=&7'true' &6dynamic=&7'true'&9>&f{player}&9</mulclr>&e!\n" +
                        "&f&oПосле убийства игрока, будет выведено сообщение: " +
                        XMLParser.getFormattedMessage("<Message>§eЯ убил тебя, <mulclr schema='f4c6+o' " +
                                "mirror='true' dynamic='true'>[имя игрока, которого Вы убили]</mulclr>§e!</Message>") + "\n" +
                        "&9Параметры:\n" +
                        "   &f● &6schema &7- &dсхема цветов, которая будет использоваться в сообщении\n" +
                        "   &f● &6mirror &7- &dпараметр, отвечающий за зеркальность цветов.\n" +
                        "       &7Значение &atrue &7- &7зеркальность цветов будет использоваться в выделенной части сообщения\n" +
                        "       &7Значение &cfalse&7- &7зеркальность цветов &cне будет&7 использоваться в выделенной части сообщения\n" +
                        "   &f● &6dynamic &7- &dпараметр, отвечающий за динамическую частоту смены цвета.\n" +
                        "       &7Значение &atrue &7- динамическая частота смены цвета &aбудет &7использоваться в выделенной части сообщения\n" +
                        "       &7Значение &cfalse &7- динамисеская частота смены цвета &cне будет &7иcпользоваться в выделенной части сообщения\n" +
                        "   &f● &6period &7- &dчастота смены цветов. Принимаются только целочисленные положительные значения.\n" +
                        "&7&oИспользование параметров может быть не обязательно, так как будут использоваться значения, " +
                        "указанные в конфигурации мода (в этом случае часть сообщения будет выглядеть так: &9<mulclr>&f{player}&9</mulclr>&7&o). " +
                        "Использовать параметры будет необходимо в том случае, если Вы желаете установить " +
                        "другую схему цветов, при этом не меняя значения схемы цветов в конфигурации, или, значения, которые установлены в " +
                        "конфигурации не форматируют желаемую часть сообщения из-за длины обработанного сообщения.\n" +
                        "\n" +
                        "&6Тег &9<mulclr> &6можно использовать и в функции замены сообщения &8" + DexLandFyr.INSTANCE.
                        commandPrefix + "&ecategory &b<категория> &dsetmsg&6.\n" +
                        "\n" +
                        "&c&lВНИМАНИЕ: &7Эта функция будет работать, если у Вас привиления &e&l[GOLD] &7 и выше!\n" +
                        "\n" +
                        "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + getName() + " [действие] [цвет (если Вы добавляете или удаляете цвет)]";
    }
}
