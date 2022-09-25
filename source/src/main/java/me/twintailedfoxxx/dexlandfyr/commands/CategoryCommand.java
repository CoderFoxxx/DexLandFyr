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
import me.twintailedfoxxx.dexlandfyr.objects.*;
import me.twintailedfoxxx.dexlandfyr.util.Case;
import me.twintailedfoxxx.dexlandfyr.util.Message;
import me.twintailedfoxxx.dexlandfyr.util.XMLParser;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.ClientChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryCommand extends DLFCommand
{
    private final FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;

    public CategoryCommand() {
        super("category", "Команда для работы с автоматическими сообщениями", "cat", "c");
    }

    public void execute(EntityPlayerSP player, ClientChatEvent event, String[] args) {
        if(args.length == 0) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Категории:");
            for(MessageCategory category : MessageCategory.values()) {
                assert category.getToggleProperty() != null;
                assert category.getConfigMessagesProperty() != null;
                Message.send(Message.formatColorCodes('&', "  &6▪ &e'" + category.getDisplayName() + "' &8| " +
                        (category.getToggleProperty().getBoolean() ? "&a●" : "&c●") + " &8| &fСообщений: &a" +
                        category.getConfigMessagesProperty().getStringList().length), MessageAction.SUGGEST_COMMAND, "",
                        Message.formatColorCodes('&', "&eСообщения категории:\n") +
                                "\n" + messageListToString(category));
            }
        } else {
            if(args[0].equalsIgnoreCase("disablemessages")) {
                conf.killCategoryEnabled.set(false);
                conf.voidKillCategoryEnabled.set(false);
                conf.bedCategoryEnabled.set(false);
                conf.ownBedBrokenCategoryEnabled.set(false);
                conf.gameStartCategoryEnabled.set(false);
                conf.gameEndCategoryEnabled.set(false);
                conf.deathCategoryEnabled.set(false);
                conf.voidDeathCategoryEnabled.set(false);
                conf.swearRepliesCategoryEnabled.set(false);
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &aвключили&7 возможность " +
                        "отправлять автоматические сообщения."));
                return;
            } else if (args[0].equalsIgnoreCase("enablemessages")) {
                conf.killCategoryEnabled.set(true);
                conf.voidKillCategoryEnabled.set(true);
                conf.bedCategoryEnabled.set(true);
                conf.ownBedBrokenCategoryEnabled.set(true);
                conf.gameStartCategoryEnabled.set(true);
                conf.gameEndCategoryEnabled.set(true);
                conf.deathCategoryEnabled.set(true);
                conf.voidDeathCategoryEnabled.set(true);
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &cотключили&7 возможность " +
                        "отправлять автоматические сообщения."));
                return;
            }

            MessageCategory category = MessageCategory.getByName(args[0].toLowerCase());
            assert category.getConfigMessagesProperty() != null;
            assert category.getToggleProperty() != null;
            if(args.length == 1) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Детали о категории &3'"
                        + category.getDisplayName() + "'&7:"));
                Message.send(Message.formatColorCodes('&', "    &fБудут ли отправляться сообщения с этой кате" +
                        "гории? " + ((category.getToggleProperty().getBoolean()) ? "&aда" : "&cнет")));
                Message.send(Message.formatColorCodes('&', "    &fСообщения &7("
                        + category.getConfigMessagesProperty().getStringList().length + ")&f:"));
                outputMessageList(category);
            } else {
                switch (args[1]) {
                    case "addmsg":
                    case "am":
                    case "addmessage":
                    case "a":
                        StringBuilder builder;
                        if(args.length >= 3) {
                            builder = new StringBuilder();
                            for(int i = 2; i < args.length; i++) builder.append(args[i]).append(" ");
                            addMessage(builder.toString(), category);
                        } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                                "&cОшибка: &7Укажите сообщение."));
                        break;
                    case "messages":
                    case "msgs":
                    case "msglist":
                    case "messagelist":
                    case "listmsgs":
                    case "listmessages":
                    case "list":
                    case "lm":
                    case "ml":
                    case "l":
                        Message.send(DexLandFyr.MESSAGE_PREFIX + "Сообщения из категории '" + category.getDisplayName() + "':");
                        Message.send(Message.formatColorCodes('&', "&7      № | Сообщение"));
                        Message.send(Message.formatColorCodes('&', "&6&m-----------------------------------------------"));
                        outputMessageList(category);
                        break;
                    case "remmsg":
                    case "removemsg":
                    case "removemessage":
                    case "rmmsg":
                    case "rm":
                    case "r":
                        int id;
                        if(args.length >= 3) {
                            id = Integer.parseInt(args[2]);
                            removeMessage(id, category);
                        } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                                "&cОшибка: &7Укажите номер сообщения, которого Вы хотите удалить."));
                        break;
                    case "setmsg":
                    case "setmessage":
                    case "replacemessage":
                    case "replacemsg":
                    case "rplmessage":
                    case "rplmsg":
                    case "rpl":
                        if(args.length >= 4) {
                            builder = new StringBuilder();
                            id = Integer.parseInt(args[2]);
                            for(int i = 3; i < args.length; i++) builder.append(args[i]).append(" ");
                            setMessage(id, builder.toString(), category);
                        } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&',
                                "&cОшибка: &7Неверное использование подкоманды! Использование: " +
                                        DexLandFyr.INSTANCE.commandPrefix + "&ecategory &b" + args[0] + " &dsetmsg " +
                                        "&a<№ сообщения> &6<новое сообщение>"));
                        break;
                    case "on":
                    case "enable":
                        if(!category.getToggleProperty().getBoolean()) {
                            category.getToggleProperty().set(true);
                            conf.reload();
                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &aвключили " +
                                    "&7категорию &e'" + category.getDisplayName() + "'&7."));
                        } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Категория &e'" +
                                category.getDisplayName() + "' &7уже &aвключена&7."));
                        break;
                    case "off":
                    case "disable":
                        if(category.getToggleProperty().getBoolean()) {
                            category.getToggleProperty().set(false);
                            conf.reload();
                            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы &cвыключили " +
                                    "&7категорию &e'" + category.getDisplayName() + "'&7."));
                        } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Категория &e'" +
                                category.getDisplayName() + "' &7уже &cвыключена&7."));
                        break;
                    case "clear":
                    case "clr":
                        clearMessages(category);
                        break;
                    default:
                        Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: " +
                                "&7Неизвестная подкоманда."));
                        break;
                }
            }
        }
    }

    @Override
    public String[] getArgs() {
        return new String[] { "enablemessages&8/&edisablemessages&8/&eкатегория", "&7(&eaddmsg&8/&eaddmessage&8/&eam&7)&6/&7(&esetmsg&8/&esetmessage&8/&ereplacemessage&8" +
                "/&ereplacemsg&8/&erplmessage&8/&erplmsg&8/&erpl&7)&6/&7(&eremmsg&8/&eremovemsg&8/&ermmsg&8/&erm&7)&6/" +
                "&7(&emessages&8/&emsgs&8/&emsglist&8/&emessagelist&8/&elistmsgs&8/&elistmessages&8/&elist&8/&elm&8/&eml" +
                "&8/&el&7)&6/&7(&eon&8/&eoff&7)&6/&7(&eclear&8/&eclr&7)", "параметр" };
    }

    @Override
    public String getHoverText() {
        return Message.formatColorCodes('&',
                "&6Эта команда позволяет Вам работать с автоматическими сообщениями.\n" +
                        "\n" +
                        "&2У сообщений есть &a9&2 категорий:\n" +
                        "&3Убийства &b&o(kill)&7, &3смерть противника в бездне &b&o(voidkill)&7, &3сломанная Вами кровать " +
                        "&b&o(bed)&7, &3сломанная кровать Вашей команды &b&o(ownbed)&7, &3смерть &b&o(death)&7, &3смерть " +
                        "в бездне (без взаимодействия других игроков на Вас) &b&o(voiddeath)&7, &3начало игры &b&o(gamestart)&7, " +
                        "&3конец игры &b&o(gameend)&3и ответы на нецензурные слова &b&o(swearreply)&3.\n" +
                        "\n" +
                        "&6Доступные функции:\n" +
                        "   &e● &fДобавить сообщение в категорию &7(&8" + DexLandFyr.INSTANCE.commandPrefix + "&ecategory " +
                        "&b<категория> &daddmsg/am/addmessage/a &6<сообщение>&7)\n" +
                        "   &e● &fЗаменить уже существующее сообщение в указанной категории &7(&8"
                        + DexLandFyr.INSTANCE.commandPrefix + "&ecategory &b<категория> &dsetmsg&5/&dsetmessage&5/" +
                        "&dreplacemessage&5/&dreplacemsg&5/&drplmessage&5/&drplmsg&5/&drpl &a<№ сообщения в списке> " +
                        "&6<новое-сообщение>&7)\n" +
                        "   &e● &fУдалить сообщение с категории &7(&8" + DexLandFyr.INSTANCE.commandPrefix +
                        "&ecategory &b<категория> &dremmsg&5/&dremovemsg&5/&drmmsg&5/&drm&5/&dr &a<№ сообщения в списке>&7)\n" +
                        "   &e● &fПоказать список сообщений в указанной категории &7(&8" +
                        DexLandFyr.INSTANCE.commandPrefix + "&ecategory &b<категория> &dmessages&5/&dmsgs&5/&dmsglist&5/" +
                        "&dmessagelist&5/&dlistmsgs&5/&dlistmessages&5/&dlist&5/&dlm&5/&dml&5/&dl&7)\n" +
                        "   &e§e &aВключить&f/&cвыключить&f отправку сообщений с указанной категории &7(&8" +
                        DexLandFyr.INSTANCE.commandPrefix + "&ecategory &b<категория> &aon (или enable)&7/&coff (или disable)&7)\n" +
                        "   &e● &fУдалить &4&lВСЕ &fсообщения с категории &7(&8" + DexLandFyr.INSTANCE.commandPrefix +
                        "&ecategory &b<категория> &dclear/clr&7)\n" +
                        "\n" +
                        "&6Примеры выполнения команд:\n" +
                        "&9&oКоманда для добавления сообщения &7&o'Это не твой уровень, [имя игрока, которого Вы убили]'&9&o в категорию &3'Убийства'&9&o " +
                        "будет выглядеть следующим образом:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&ecategory &bkill &daddmsg &6Это не твой уровень, {player}\n\n" +
                        "&9&oЗамена сообщения осущесвтляется следующим образом:\n" +
                        "   &61. &f&oСначала Вам необходимо узнать, под каким номером находится сообщение, которое Вы хотите " +
                        "заменить. Чтобы узнать этот номер, используйте команду &8" + DexLandFyr.INSTANCE.commandPrefix +
                        "&ecategory &b<категория> &dmessages &fили Вы можете запустить команду &8f.category &fи навести " +
                        "на нужную категорию сообщений для вывода списка сообщений&f;\n" +
                        "   &62. &f&oПосле того, как Вы узнали номер сообщения, выполните команду &8" +
                        DexLandFyr.INSTANCE.commandPrefix + "&ecategory &b<категория> &dsetmsg &a<№ сообщения> &6<новое-сообщение>.\n" +
                        "&9&oВывод с &8&o" + DexLandFyr.INSTANCE.commandPrefix + "&e&ocategory &b&obed &d&omessages:\n" +
                        "&7&o...\n" +
                        "&74. &7&oВидимо вам совершенно не жалко терять свою кровать... Я это понял по её защите.\n" +
                        "&7&o...\n" +
                        "&9&oКоманда для замены сообщения в категории &3'Сломанная кровать' под &a№4 &9&oна сообщение " +
                        "&7&o'Я сломал [количество сломанных кроватей за игру] кроватей!' &9&oбудет выглядеть следующим образом:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&ecategory &bbed &dsetmsg &a4 &6Я сломал {ingame_beds_count} кроватей!\n\n" +
                        "&9&oУдаление сообщения:\n" +
                        "   &f&oУзнайте номер сообщения при помощи команды &8" + DexLandFyr.INSTANCE.commandPrefix +
                        "&ecategory &b<категория> &dmessages &f&oи затем выполните команду:\n" +
                        "&8" + DexLandFyr.INSTANCE.commandPrefix + "&ecategory &b<категория> &dremmsg &a<№ сообщения>\n" +
                        "\n" +
                        "&6&oСообщения поддерживают все виды форматирования чата &7(только с привилегии &e&l[GOLD]&7)&6.\n" +
                        "&6&oСообщения поддерживают тег &9<mulclr>&6&o. Узнайте больше, наведя на команду &8" +
                        DexLandFyr.INSTANCE.commandPrefix + "&emulclr &6&oв помощи.\n" +
                        "&6&oСообщения поддерживают переменные. Посмотрите список доступных переменных в &8" +
                        DexLandFyr.INSTANCE.commandPrefix + "&ehelp &6&oв самом низу.\n" +
                        "\n" +
                        "&6&nНажмите, чтобы написать эту команду в чате!");
    }

    @Override
    public String getCommandSuggestText() {
        return DexLandFyr.INSTANCE.commandPrefix + "category <категория> <действие> <параметр>";
    }

    private void addMessage(String message, MessageCategory category) {
        try {
            assert category.getConfigMessagesProperty() != null;
            List<String> confList = new ArrayList<>(Arrays.asList(category.getConfigMessagesProperty().getStringList()));
            String xmlMessage = "<Message>" + message.replace("&", "§") + "</Message>";

            if (XMLParser.isValid(xmlMessage)) {
                if (!confList.contains(xmlMessage)) {
                    confList.add(xmlMessage);
                    category.getConfigMessagesProperty().set(confList.toArray(new String[0]));
                    conf.reload();
                    Message.send(DexLandFyr.MESSAGE_PREFIX + "Сообщение '" + Message.formatColorCodes('&',
                            XMLParser.getFormattedMessage(xmlMessage) + "'" + " &7было успешно добавлено в категорию " +
                                    category.getDisplayName() + "."));
                } else
                    Message.send(DexLandFyr.MESSAGE_PREFIX + "Такое сообщение в категории '" + category.getDisplayName() +
                            "' уже существует.");
            } else Message.send(DexLandFyr.MESSAGE_PREFIX + "Неверный формат сообщения.");
        } catch (Exception ex) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время выполнения команды. Проверьте введенные " +
                    "данные и попробуйте снова.");
            ex.printStackTrace();
        }
    }

    private void setMessage(int id, String newMessage, MessageCategory category) {
        try {
            assert category.getConfigMessagesProperty() != null;
            List<String> confList = new ArrayList<>(Arrays.asList(category.getConfigMessagesProperty().getStringList()));
            String oldMessage = confList.get(id - 1);
            newMessage = "<Message>" + newMessage.replace("&", "§") + "</Message>";
            if (XMLParser.isValid(newMessage)) {
                confList.set(id - 1, newMessage);
                category.getConfigMessagesProperty().set(confList.toArray(new String[0]));
                conf.reload();
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Сообщение в категории '" +
                        category.getDisplayName() + "' под идентификатором &e" + id + " &7было изменено. &6'&d&o" +
                        XMLParser.getFormattedMessage(oldMessage) + "&6' → &6'&d&o" + XMLParser.getFormattedMessage(newMessage) +
                        "&6'&7."));
            } else Message.send(DexLandFyr.MESSAGE_PREFIX + "Неверный формат сообщения.");
        } catch (Exception ex) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время выполнения команды. Проверьте введенные " +
                    "данные и попробуйте снова.");
            ex.printStackTrace();
        }
    }

    private void outputMessageList(MessageCategory category) {
        try {
            for(int i = 0; i < Objects.requireNonNull(category.getConfigMessagesProperty()).getStringList().length; i++) {
                Message.send("      " + Message.formatColorCodes('&', "&7" + (i + 1) + ". &7&o" +
                        XMLParser.getFormattedMessage(processPlaceholders(category.getConfigMessagesProperty()
                                .getStringList()[i]))));
            }
        } catch (Exception ex) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время выполнения команды. Проверьте введенные " +
                    "данные и попробуйте снова.");
            ex.printStackTrace();
        }
    }

    private String messageListToString(MessageCategory category) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < Objects.requireNonNull(category.getConfigMessagesProperty()).getStringList().length; i++) {
            builder.append(Message.formatColorCodes('&', "&7" + (i + 1) + ". &7&o" +
                            XMLParser.getFormattedMessage(processPlaceholders(category.getConfigMessagesProperty()
                                    .getStringList()[i]))))
                    .append("\n");
        }

        return builder.toString();
    }

    private void removeMessage(int id, MessageCategory category) {
        try {
            assert category.getConfigMessagesProperty() != null;
            List<String> confList = new ArrayList<>(Arrays.asList(category.getConfigMessagesProperty().getStringList()));
            confList.remove(id - 1);
            category.getConfigMessagesProperty().set(confList.toArray(new String[0]));
            conf.reload();
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Сообщение под идентификатором " + Message.formatColorCodes('&', "&6" + id + " &7было успешно удалено с категории '" + category.getDisplayName() + "'."));
        } catch (Exception ex) {
            Message.send(DexLandFyr.MESSAGE_PREFIX + "Произошла ошибка во время выполнения команды. Проверьте введенные " +
                    "данные и попробуйте снова.");
            ex.printStackTrace();
        }
    }

    private void clearMessages(MessageCategory category) {
        assert category.getConfigMessagesProperty() != null;
        List<String> confList = new ArrayList<>(Arrays.asList(category.getConfigMessagesProperty().getStringList()));
        if(confList.size() > 0) {
            confList.clear();
            category.getConfigMessagesProperty().set(confList.toArray(new String[0]));
            Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Вы очистили сообщения с категории " +
                    "&e'" + category.getName() + "'&7."));
        } else Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "Список сообщений категории " +
                "&e'" + category.getName() + "'&7 уже пуст."));
    }

    private String processPlaceholders(String s) {
        for (String m : getTeamNamePatternMatches(s)) {
            String[] params = m.substring(11).split("_");
            for (int i = 0; i < params.length; i++)
                params[i] = params[i].replace("{", "")
                        .replace("}", "");
            StringBuilder b = new StringBuilder(Message.formatColorCodes('&', "&3&o[Имя команды: "));
            b.append(Message.formatColorCodes('&', "&e&o" + Case.getCaseFromPlaceholder(params[0]).getDisplayName()));
            if (Arrays.asList(params).contains("l"))
                b.append(Message.formatColorCodes('&', "&3&o; начало с маленькой буквы"));
            if (Arrays.asList(params).contains("c"))
                b.append(Message.formatColorCodes('&', "&3&o; цветное"));
            b.append("]");

            s = s.replace(m, b.toString());
        }

        return s.replace("{dc}", conf.defaultChatColor.getString())
                .replace("{player}", "[имя игрока, которого Вы убили]")
                .replace("{ingame_kill_count}", "[счётчик убийств за игру]")
                .replace("{total_kill_count}", "[счётчик убийств за все время]")
                .replace("{ingame_beds_count}", "[счётчик кроватей за игру]")
                .replace("{total_beds_count}", "[счётчик кроватей за всё время]")
                .replace("{ingame_death_count}", "[счётчик смертей за игру]")
                .replace("{player_team_color}", "[цвет команды игрока, которого Вы убили]")
                .replace("{killer}",  "[имя игрока, который убил Вас]")
                .replace("{killer_team_color}", "[цвет команды игрока, который убил Вас]")
                .replace("{gameTimer}", "[время игры]");
    }

    private List<String> getTeamNamePatternMatches(String s) {
        ArrayList<String> a = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\{team_name_(.*?)}").matcher(s);
        while (matcher.find()) {
            a.add(matcher.group());
        }

        return a;
    }
}