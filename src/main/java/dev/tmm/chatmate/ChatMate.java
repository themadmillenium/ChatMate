package dev.tmm.chatmate;

import dev.tmm.chatmate.compat.ChatComponentInstance;
import dev.tmm.chatmate.compat.CompatUtility;
import dev.tmm.chatmate.compat.base.CompatClass;
import dev.tmm.chatmate.compat.base.CompatField;
import dev.tmm.chatmate.compat.base.CompatMethod;
import dev.tmm.chatmate.core.ChatFilterContainer;
import dev.tmm.chatmate.core.ChatSound;
import dev.tmm.chatmate.gui.GuiFilteredChat;
import dev.tmm.chatmate.gui.GuiMultiChat;
import dev.tmm.chatmate.util.ChatColor;
import dev.tmm.chatmate.util.FileUtility;
import dev.tmm.chatmate.util.FilterPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sf.cglib.proxy.Enhancer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Mod(modid = ChatMate.MOD_ID, name = ChatMate.MOD_NAME, version = ChatMate.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8,1.11]")

@SideOnly(Side.CLIENT)
public class ChatMate {
    public static final String MOD_ID = "chatmate";
    public static final String MOD_NAME = "ChatMate";
    public static final String VERSION = "1.0";

    private static final Logger logger = LogManager.getLogger();

    public static Minecraft mc;

    private static String CHATMATE_DIR;
    private static String USER_DIR;

    private static String serverIP;

    private static GuiMultiChat chat;

    private static Pattern hypixelIPPattern = Pattern.compile("(\\w+\\.hypixel\\.net|209\\.222\\.115\\.14|hypixel..+)", 2);

    private static boolean updateAvailable;

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        mc = Minecraft.getMinecraft();
        CHATMATE_DIR = "mods" + File.separator + "chatmate" + File.separator;

        String playerID = ChatMate.mc.getSession().getProfile().getId().toString();
        USER_DIR = CHATMATE_DIR + playerID + File.separator;

        FileUtility.getDirectory(USER_DIR);

        CompatUtility.loadSounds(mc);

        ChatMate core = new ChatMate();

        ClientRegistry.registerKeyBinding(GuiMultiChat.tabLeft);
        ClientRegistry.registerKeyBinding(GuiMultiChat.tabRight);

        MinecraftForge.EVENT_BUS.register(core);
        FMLCommonHandler.instance().bus().register(core);

        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(GuiMultiChat.class);
        enhancer.setCallback(GuiMultiChat.constructInterceptor());

        chat = (GuiMultiChat) enhancer.create(new Class[]{Minecraft.class}, new Object[]{ChatMate.mc});
    }

    private static CompatMethod getTypeMethod = new CompatMethod(new CompatClass(ClientChatReceivedEvent.class), "getType").tryMapping();
    private static CompatMethod getMessageMethod = new CompatMethod(new CompatClass(ClientChatReceivedEvent.class), "getMessage").tryMapping();
    private static CompatField typeField = new CompatField(new CompatClass(ClientChatReceivedEvent.class), "type");
    private static CompatField messageField = new CompatField(new CompatClass(ClientChatReceivedEvent.class), "message");

    @SubscribeEvent
    public void chatEvent(ClientChatReceivedEvent event) {
        if (getTypeMethod.mappingExists()) {
            if (getTypeMethod.call(event) == 0) {
                chat.printChatMessageCompat(new ChatComponentInstance(getMessageMethod.call(event)));
                event.setCanceled(true);
            }
        } else {
            if (typeField.get(event) == 0) {
                chat.printChatMessageCompat(new ChatComponentInstance(messageField.get(event)));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void playerLogIn(FMLNetworkEvent.ClientConnectedToServerEvent event) throws IOException {
        ReflectionHelper.setPrivateValue((Class) GuiIngame.class, ChatMate.mc.ingameGUI, chat, "field_73840_e", "persistantChatGUI");

        try {
            HttpURLConnection versionDL = (HttpURLConnection) new URL("https://drive.google.com/uc?export=download&id=0Byag2yNCG7Knby1SQXA4dzl5NXM").openConnection();
            versionDL.setConnectTimeout(10000);
            versionDL.setReadTimeout(10000);

            InputStream versionStream = versionDL.getInputStream();

            int contentSize = versionDL.getContentLength();
            byte[] content = new byte[contentSize];

            byte[] chunk = new byte[1024];
            int readIdx = 0;
            int readSize;

            while ((readSize = versionStream.read(chunk, 0, 1024)) >= 0) {
                System.arraycopy(chunk, 0, content, readIdx, readSize);
                readIdx += readSize;
            }

            // !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~

            String stableHash = new String(content).split(" ")[1];
            updateAvailable = !stableHash.equals("#");
        } catch (SocketTimeoutException e) {
            logger.warn("Socket timed out! Check your internet connection!");
        }

        ChatFilterContainer.clearAll();

        ServerData data = FMLClientHandler.instance().getClient().getCurrentServerData();

        if (data == null) {
            if (!ChatMate.mc.isSingleplayer()) return;

            serverIP = ChatMate.mc.getIntegratedServer().getWorldName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".local";
        } else {
            serverIP = data.serverIP;
        }

        ChatFilterContainer.set("Public Chat", new GuiFilteredChat(ChatMate.mc, ".*"));

        String[] filters = FileUtility.loadFromFile(USER_DIR + serverIP + ".cmdata");

        for (String serializedFilter : filters) {
            if (!serializedFilter.contains("\u0005")) continue;

            String[] filterProps = serializedFilter.split("\u0005");

            GuiFilteredChat filter;

            if (filterProps.length != 6) { // filterProps.length != 5 &&
                logger.info(filterProps[0] + " was found to be broken, and was not loaded!");
                continue;
            }

            if (filterProps[0].length() == 0 || filterProps[1].length() == 0 || filterProps[4].length() == 0) {
                logger.info(filterProps[0] + " was found to be broken, and was not loaded!");
                continue;
            }

            FilterPreset preset = FilterPreset.valueOf(filterProps[1]);

            try {
                switch (preset) {
                    case Custom:
                    case Key_Words:
                        filter = new GuiFilteredChat(ChatMate.mc, ChatColor.getSymbolizedString(preset.getPattern(filterProps[2])));
                        break;
                    default:
                        filter = new GuiFilteredChat(ChatMate.mc, ChatColor.getSymbolizedString(preset.getPattern("")));
                        break;
                }
            } catch (PatternSyntaxException e) {
                logger.info(filterProps[0] + " was found to be broken, and was not loaded!");
                continue;
            }

            filter.setPreset(preset);

            ChatSound sound = null;

            if (filterProps[3].length() > 0)
                sound = new ChatSound(filterProps[3], 1, 1);

            if (sound != null) filter.setSound(sound);

            filter.setBlacklisting(Boolean.valueOf(filterProps[4]));

            //if (filterProps.length != 5) { //TODO Remove code block
                if (!Boolean.valueOf(filterProps[5])) filter.toggle();
            //}

            ChatFilterContainer.set(filterProps[0], filter);
        }

        ChatFilterContainer.setActiveChatPage("Public Chat");

        if (updateAvailable) {
            new Timer().schedule(new TimerTask() {
                public void run() {
                    ChatFilterContainer.getActiveChatPage().printChatMessage(ChatColor.getAdjustedStr(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "A newer version of ChatMate is available!\nRun the installer again to update!\nAlternatively, use the updated link on the forums!"), true);
                }
            }, 5000);
        }
    }

    public static void saveFilters() throws IOException {
        if (serverIP == null) return;

        List<String> filters = ChatFilterContainer.getChatPageEntries();

        String[] formattedFilters = new String[filters.size()];

        int i = 0;

        for (String filter : filters) {
            GuiFilteredChat chat = ChatFilterContainer.getChatPage(filter);
            ChatSound sound = chat.getSound();

            if (sound.getName().equals("None"))
                formattedFilters[i] = filter + "\u0005" + chat.getPreset() + "\u0005" + chat.getPreset().transformInput(chat.getPatternStr()) + "\u0005\u0005" + chat.isBlacklisting() + "\u0005" + chat.isEnabled();
            else
                formattedFilters[i] = filter + "\u0005" + chat.getPreset() + "\u0005" + chat.getPreset().transformInput(chat.getPatternStr()) + "\u0005" + sound.getName() + "\u0005" + chat.isBlacklisting() + "\u0005" + chat.isEnabled();

            i++;
        }

        FileUtility.saveToFile(USER_DIR + serverIP + ".cmdata", formattedFilters);
    }

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event) {
        if(GuiMultiChat.tabLeft.isPressed()) {
            ChatFilterContainer.setActiveChatPage(ChatFilterContainer.previous());
        } else if(GuiMultiChat.tabRight.isPressed()) {
            ChatFilterContainer.setActiveChatPage(ChatFilterContainer.next());
        } else return;

        chat.resetTabUpdateCounter();

        ChatFilterContainer.getActiveChatPage().resetUnviewedMsgs();
    }

    public static boolean onHypixel() {
        return serverIP != null && hypixelIPPattern.matcher(serverIP).matches();
    }
}
