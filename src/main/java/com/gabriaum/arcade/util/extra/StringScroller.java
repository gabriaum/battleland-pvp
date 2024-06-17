package com.gabriaum.arcade.util.extra;

import net.md_5.bungee.api.ChatColor;
import java.util.ArrayList;
import java.util.List;

/**
 * StringScroller - A utility class to create scrolling text effects in Minecraft chat.
 * Credits to Gabriaum for the original implementation.
 */
public class StringScroller {
    private static final char COLOUR_CHAR = '§';
    private int position;
    private List<String> list;
    private ChatColor colour;

    public StringScroller(String message, int width, int spaceBetween) {
        colour = null;
        list = new ArrayList<>();

        if (message.length() < width)
            message = padRight(message, width);

        width -= 2;
        if (width < 1)
            width = 1;

        if (spaceBetween < 0)
            spaceBetween = 0;


        for (int i = 0; i < message.length() - width; ++i)
            list.add(message.substring(i, i + width));

        StringBuilder space = new StringBuilder();

        for (int j = 0; j < spaceBetween; ++j) {
            list.add(message.substring(message.length() - width + ((j > width) ? width : j), message.length()) + space);

            if (space.length() < width)
                space.append(" ");
        }

        for (int j = 0; j < width - spaceBetween; ++j)
            list.add(message.substring(message.length() - width + spaceBetween + j, message.length()) + space + message.substring(0, j));

        for (int j = 0; j < spaceBetween && j <= space.length(); ++j)
            list.add(space.substring(0, space.length() - j) + message.substring(0, width - ((spaceBetween > width) ? width : spaceBetween) + j));
    }

    public String next() {
        StringBuilder sb = getNext();

        if (sb.charAt(sb.length() - 1) == COLOUR_CHAR)
            sb.setCharAt(sb.length() - 1, ' ');

        if (sb.charAt(0) == COLOUR_CHAR) {
            ChatColor c = ChatColor.getByChar(sb.charAt(1));

            if (c != null) {
                colour = c;
                sb = getNext();

                if (sb.charAt(0) != ' ')
                    sb.setCharAt(0, ' ');
            }
        }

        return (colour != null ? colour : "") + sb.toString();
    }

    private StringBuilder getNext() {
        return new StringBuilder(list.get(position++ % list.size()));
    }

    private String padRight(String input, int width) {
        StringBuilder sb = new StringBuilder(input);

        while (sb.length() < width)
            sb.append(" ");

        return sb.toString();
    }
}
