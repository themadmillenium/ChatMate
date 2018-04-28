package dev.tmm.chatmate.core;

import dev.tmm.chatmate.gui.GuiFilteredChat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class ChatFilterContainer {
    private static final ArrayList<String> keys;
    private static final HashMap<String, GuiFilteredChat> chatPages;
    private static String activeChatPage;

    static {
        keys = new ArrayList<>();
        chatPages = new HashMap<>();
    }

    private ChatFilterContainer() {
    }

    public static void set(String name, GuiFilteredChat chat) {
        if (!keys.contains(name)) keys.add(name);

        chatPages.put(name, chat);
        activeChatPage = name;
    }

    public static void remove(String name) {
        keys.remove(name);
        chatPages.remove(name);
    }

    public static void clearAll() {
        keys.clear();
        chatPages.clear();
    }

    public static void drawActivePage(int updateCounter, boolean chatVisible, float opacity) {
        if (getActiveChatPage() == null) return;
        getActiveChatPage().drawChat(updateCounter, chatVisible, opacity);
    }

    public static boolean isActivePage(String testPage) {
        if (activeChatPage == null) {
            return testPage == null;
        }

        return testPage.equals(activeChatPage);
    }

    public static GuiFilteredChat getActiveChatPage() {
        return chatPages.get(activeChatPage);
    }

    public static String getActiveChatPageName() {
        return activeChatPage;
    }

    public static void setActiveChatPage(String chatPage) {
        if (chatPage == null) {
            activeChatPage = null;
            return;
        }

        if (chatPages.containsKey(chatPage)) activeChatPage = chatPage;
    }

    public static ArrayList<String> getChatPageEntries() {
        return keys;
    }

    public static void swapKeys(int i, int j) {
        Collections.swap(keys, i, j);
    }

    public static GuiFilteredChat getChatPage(String chatPage) {
        return chatPages.get(chatPage);
    }

    public static String previous() {
        return keys.get(Math.max(keys.indexOf(activeChatPage) - 1, 0));
    }

    public static String next() {
        return keys.get(Math.min(keys.indexOf(activeChatPage) + 1, keys.size() - 1));
    }
}
