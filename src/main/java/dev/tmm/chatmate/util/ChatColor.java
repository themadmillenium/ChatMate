package dev.tmm.chatmate.util;

import dev.tmm.chatmate.compat.ChatColorEnum;
import dev.tmm.chatmate.compat.ChatComponentInstance;
import dev.tmm.chatmate.compat.ChatComponentTextInstance;
import dev.tmm.chatmate.compat.StyleInstance;

import java.util.HashMap;
import java.util.Map;

public enum ChatColor {
    BLACK('0', "black"),
    DARK_BLUE('1', "dark_blue"),
    DARK_GREEN('2', "dark_green"),
    DARK_AQUA('3', "dark_aqua"),
    DARK_RED('4', "dark_red"),
    DARK_PURPLE('5', "dark_purple"),
    GOLD('6', "gold"),
    GRAY('7', "gray"),
    DARK_GRAY('8', "dark_gray"),
    BLUE('9', "blue"),
    GREEN('a', "green"),
    AQUA('b', "aqua"),
    RED('c', "red"),
    LIGHT_PURPLE('d', "light_purple"),
    YELLOW('e', "yellow"),
    WHITE('f', "white"),
    OBFUSCATED('k', "obfuscated"),
    BOLD('l', "bold"),
    STRIKETHROUGH('m', "strikethrough"),
    UNDERLINE('n', "underline"),
    ITALIC('o', "italic"),
    RESET('r', "reset");

    private static final Map<Character, ChatColor> lookup = new HashMap<>();

    static {
        for (ChatColor c : values()) {
            lookup.put(c.formattingCode, c);
        }
    }

    private char formattingCode;
    private String symbol;

    ChatColor(char formattingCode, String symbol) {
        this.formattingCode = formattingCode;
        this.symbol = symbol;
    }

    public String toString() {
        return "\u00a7" + this.formattingCode;
    }

    public String getSymbol() {
        return "<" + symbol + ">";
    }

    public static boolean hasColorCodes(String text) {
        for (ChatColor c : values()) {
            if (text.contains(c.getSymbol())) return true;
            if (text.contains(c.toString())) return true;
        }

        return false;
    }

    public static String getSymbolizedString(String formattedString) {
        String symbolizedString = formattedString;

        for (ChatColor c : values()) {
            symbolizedString = symbolizedString.replaceAll(c.toString(), c.getSymbol());
        }

        return symbolizedString;
    }

    public static String getFormattedString(String symbolizedString) {
        String formattedString = symbolizedString;

        for (ChatColor c : values()) {
            formattedString = formattedString.replaceAll(c.getSymbol(), c.toString());
        }

        return formattedString;
    }

    public static String stripRedundancies(String redundantString) {
        StringBuilder out = new StringBuilder(redundantString);

        char lastChar = '\u00a7';
        boolean affectedTextExists = false;

        boolean changeLastChar = true;

        for (int i = out.length() - 1; i >= 0; i--) {
            if (out.charAt(i) == '\u00a7') {
                switch (lastChar) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'r':
                        if (!affectedTextExists) {
                            out.replace(i, i + 2, "");
                            changeLastChar = false;
                        }

                        affectedTextExists = false;

                        break;
                }
            } else {
                if (lastChar != '\u00a7') affectedTextExists = true;
            }

            if (changeLastChar) lastChar = out.charAt(i);
            else lastChar = '\u00a7';

            changeLastChar = true;
        }

        return out.toString();
    }

    public static ChatComponentInstance getAdjustedStr(String formattedString) {
        formattedString = stripRedundancies(formattedString);

        formattedString += ChatColor.RESET;

        ChatComponentInstance out = null;
        StyleInstance style = new StyleInstance();

        char val;

        for (int i = 0; i < formattedString.length(); i++) {
            if (formattedString.charAt(i) == '\u00a7') {
                switch (val = formattedString.charAt(++i)) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case 'a':
                    case 'b':
                    case 'c':
                    case 'd':
                    case 'e':
                    case 'f':
                        style.setBold(false);
                        style.setItalic(false);
                        style.setObfuscated(false);
                        style.setStrikethrough(false);
                        style.setUnderlined(false);
                        style.setColor(ChatColorEnum.valueOf(fromCode(val).name()));

                        break;
                    case 'k':
                        style.setObfuscated(true);
                        break;
                    case 'l':
                        style.setBold(true);
                        break;
                    case 'm':
                        style.setStrikethrough(true);
                        break;
                    case 'n':
                        style.setUnderlined(true);
                        break;
                    case 'o':
                        style.setItalic(true);
                        break;
                    case 'r':
                        style = new StyleInstance();
                        break;
                }
            } else {
                if (out == null) {
                    out = new ChatComponentTextInstance(formattedString.substring(i, i = formattedString.indexOf('\u00a7', i + 1))).setStyle(style);
                } else {
                    out.appendSibling(new ChatComponentTextInstance(formattedString.substring(i, i = formattedString.indexOf('\u00a7', i))).setStyle(style));
                }

                i--;
            }
        }

        return out;
    }

    public static ChatColor fromCode(char code) {
        return lookup.get(code);
    }
}