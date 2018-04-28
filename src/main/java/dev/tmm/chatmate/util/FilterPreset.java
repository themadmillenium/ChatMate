package dev.tmm.chatmate.util;

import java.util.regex.Pattern;

public enum FilterPreset {
    Public_Chat,
    Private_Messages,
    Party_Chat,
    Guild_Chat,
    Key_Words,
    Custom;

    public FilterPreset next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public String getPattern(String input) {
        switch (this) {
            case Public_Chat:
                return ".*";
            case Private_Messages:
                return "^<light_purple>(?:From|To).*";
            case Party_Chat:
                return "^<blue>Party >.*";
            case Guild_Chat:
                return "^<dark_green>Guild >.*";
            case Key_Words:
                String[] keywords = input.split(" +");

                String pattern = "";

                if (keywords.length > 0) {
                    pattern += "(?:";

                    for (String k : keywords) {
                        pattern += Pattern.quote(k) + "|";
                    }

                    pattern = pattern.substring(0, pattern.length() - 1);

                    pattern += ")";
                }

                return pattern;
            case Custom:
                return input;
        }

        return null;
    }

    public String transformInput(String pattern) {
        switch (this) {
            case Public_Chat:
            case Private_Messages:
            case Party_Chat:
            case Guild_Chat:
                return "";
            case Key_Words:
                String[] keywords = pattern.substring(3, pattern.length() - 1).split("\\|");

                String text = "";

                for (String k : keywords) {
                    text += k.substring(2, k.length() - 2) + " ";
                }

                text = text.substring(0, text.length() - 1);

                return text;
            case Custom:
                return pattern;
        }

        return null;
    }
}
