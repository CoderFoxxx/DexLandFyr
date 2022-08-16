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

package me.twintailedfoxxx.dexlandfyr.util;

import me.twintailedfoxxx.dexlandfyr.DexLandFyr;
import me.twintailedfoxxx.dexlandfyr.exceptions.IllegalColorAmountException;
import me.twintailedfoxxx.dexlandfyr.exceptions.OutOfMaximumCharactersLimitException;
import me.twintailedfoxxx.dexlandfyr.exceptions.PeriodicityOverflowException;
import me.twintailedfoxxx.dexlandfyr.objects.FyrConfiguration;
import me.twintailedfoxxx.dexlandfyr.objects.MulticolouredString;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class XMLParser {
    public static Document parse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(new InputSource(new ByteArrayInputStream(xml.replaceFirst("\uFEFF", "").
                    getBytes(StandardCharsets.UTF_8))));
        } catch (IOException | SAXException | IllegalArgumentException | ParserConfigurationException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String getFormattedMessage(String xmlMessage) {
        FyrConfiguration conf = DexLandFyr.INSTANCE.cfg;
        Document document = XMLParser.parse(xmlMessage);
        String formattedMessage;
        assert document != null;
        if (document.getElementsByTagName("mulclr").getLength() != 0) {
            try {
                String txtContent = document.getElementsByTagName("Message").item(0).getTextContent();
                Node multicolourNode = document.getElementsByTagName("mulclr").item(0);
                String multicolouredPart = multicolourNode.getTextContent();;

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
                        1024);
                formattedMessage = txtContent.replace(multicolouredPart, multicolour.processString(multicolour.isDynamic()
                        ? 1 : (multicolourNode.getAttributes().getNamedItem("period") != null) ?
                        Integer.parseInt(multicolourNode.getAttributes().getNamedItem("period").getTextContent()) :
                        conf.colorPeriodicity.getInt()) + "&7");
            } catch (OutOfMaximumCharactersLimitException | IllegalColorAmountException |
                     PeriodicityOverflowException ex) {
                Message.send(DexLandFyr.MESSAGE_PREFIX + Message.formatColorCodes('&', "&cОшибка: &7" +
                        ex.getMessage()));
                ex.printStackTrace();
                formattedMessage = document.getElementsByTagName("Message").item(0).getTextContent();
            }
        } else formattedMessage = document.getElementsByTagName("Message").item(0).getTextContent();

        return formattedMessage.replace("§", "&") + "§7§o";
    }

    public static boolean isValid(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document d = builder.parse(is);
            return true;
        } catch (IOException | SAXException | IllegalArgumentException | ParserConfigurationException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
