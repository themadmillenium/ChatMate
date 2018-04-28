package dev.tmm.chatmate.gui;

import com.google.common.collect.Lists;
import dev.tmm.chatmate.ChatMate;
import dev.tmm.chatmate.compat.*;
import dev.tmm.chatmate.compat.base.CompatClass;
import dev.tmm.chatmate.compat.base.CompatMethod;
import dev.tmm.chatmate.core.ChatFilterContainer;
import dev.tmm.chatmate.core.ChatSound;
import dev.tmm.chatmate.util.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiMultiChat extends GuiNewChat {
    private static final Logger logger = LogManager.getLogger();

    private static final CompatMethod drawChatMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146230_a", "drawChat").tryMapping(CompatUtility.clInt);
    private static final CompatMethod clearChatMessagesMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146231_a", "clearChatMessages").tryMapping();
    private static final CompatMethod printChatMessageMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146227_a", "printChatMessage").tryMapping(CompatUtility.clIChatComponent);
    private static final CompatMethod printChatMessageWithOptionalDeletionMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146234_a", "printChatMessageWithOptionalDeletion").tryMapping(CompatUtility.clIChatComponent, CompatUtility.clInt);
    private static final CompatMethod refreshChatMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146245_b", "refreshChat").tryMapping();
    private static final CompatMethod getSentMessagesMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146238_c", "getSentMessages").tryMapping();
    private static final CompatMethod addToSentMessagesMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146239_a", "addToSentMessages").tryMapping(CompatUtility.clString);
    private static final CompatMethod resetScrollMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146240_d", "resetScroll").tryMapping();
    private static final CompatMethod scrollMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146229_b", "scroll").tryMapping(CompatUtility.clInt);
    private static final CompatMethod getChatComponentMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146236_a", "getChatComponent").tryMapping(CompatUtility.clInt, CompatUtility.clInt);
    private static final CompatMethod deleteChatLineMethod = new CompatMethod(new CompatClass(GuiNewChat.class), "func_146242_c", "deleteChatLine").tryMapping(CompatUtility.clInt);

    public static final KeyBinding tabLeft = new KeyBinding("Previous Tab", Keyboard.KEY_NONE, "ChatMate");
    public static final KeyBinding tabRight = new KeyBinding("Next Tab", Keyboard.KEY_NONE, "ChatMate");

    public static MethodInterceptor constructInterceptor() {
        return new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                if (method.equals(drawChatMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).drawChatCompat((Integer) args[0]);
                } else if (method.equals(clearChatMessagesMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).clearChatMessagesCompat();
                } else if (method.equals(printChatMessageMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).printChatMessageCompat(new ChatComponentInstance(args[0]));
                } else if (method.equals(printChatMessageWithOptionalDeletionMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).printChatMessageWithOptionalDeletionCompat(new ChatComponentInstance(args[0]), (Integer) args[1]);
                } else if (method.equals(refreshChatMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).refreshChatCompat();
                } else if (method.equals(getSentMessagesMethod.getMappedMethod())) {
                    return ((GuiMultiChat) o).getSentMessagesCompat();
                } else if (method.equals(addToSentMessagesMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).addToSentMessagesCompat((String) args[0]);
                } else if (method.equals(resetScrollMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).resetScrollCompat();
                } else if (method.equals(scrollMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).scrollCompat((Integer) args[0]);
                } else if (method.equals(getChatComponentMethod.getMappedMethod())) {
                    return ((GuiMultiChat) o).getChatComponentCompat((Integer) args[0], (Integer) args[1]);
                } else if (method.equals(deleteChatLineMethod.getMappedMethod())) {
                    ((GuiMultiChat) o).deleteChatLineCompat((Integer) args[0]);
                } else {
                    return methodProxy.invokeSuper(o, args);
                }

                return null;
            }
        };
    }

    private final Minecraft mc;
    private final List<String> sentMessages = Lists.newArrayList();
    private final List<RelativeMouseEventHandler> mouseHandlers;
    private int tabScroll;
    private int tabScrollLimit;
    private String tabOver;
    private int spacing = 10;

    private boolean b0P;
    private boolean b1P;
    private boolean b2P;

    private boolean overOptBtn;
    private int overOptID = -1;

    private boolean showMenu;

    private boolean mousePressed;
    private boolean draggingTab;

    private int tabLocX;

    private int dragPressX;
    private int dragCurrentX;

    private int tabUpdateCounter;
    private boolean tabChanged;

    private int lastUpdate;

    public GuiMultiChat(final Minecraft mcIn) {
        super(mcIn);
        mc = mcIn;
        mouseHandlers = new ArrayList<>();

        mouseHandlers.add(new RelativeMouseEventHandler() {
            public boolean leftBtn(int x, int y, boolean pressed) {
                draggingTab = false;

                mousePressed = false;

                if (y > getLineCount() * 9 + 1 && y <= getLineCount() * 9 + 15) {
                    if (x > 2 && x < getChatWidth() - 4) {
                        mousePressed = pressed && getTabName(x) != null;

                        if (pressed) {
                            dragPressX = x;
                            dragCurrentX = x;

                            if (getTabName(x) == null) return true;

                            if (!ChatFilterContainer.getActiveChatPageName().equals(getTabName(x)))
                                MinecraftClass.getMinecraft().getPlayer().playSound("Click", 1, 0.25f);

                            ChatFilterContainer.setActiveChatPage(getTabName(x));
                            ChatFilterContainer.getActiveChatPage().resetUnviewedMsgs();

                            int offs = tabScroll - 5;

                            for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
                                if (ChatFilterContainer.isActivePage(chatPage)) {
                                    tabLocX = offs;
                                    break;
                                }

                                offs += mc.fontRendererObj.getStringWidth(chatPage) + spacing + 1;

                                int missedChatMsgs = ChatFilterContainer.getChatPage(chatPage).getUnviewedMsgs();

                                if (missedChatMsgs > 0) {
                                    String number = "" + missedChatMsgs;
                                    offs += mc.fontRendererObj.getStringWidth(number) + 8;
                                }
                            }
                        }

                        return true;
                    }
                }

                return false;
            }

            public boolean scroll(int x, int y, int amount) {
                if (y > getLineCount() * 9 + 1 && y <= getLineCount() * 9 + 14) {
                    if (x > 2 && x < getChatWidth() - 4) {
                        tabScroll += amount;
                        tabScroll = MathHelperClass.clamp(tabScroll, Math.min(-tabScrollLimit, 0), 0);

                        return true;
                    }
                }

                return false;
            }

            public boolean position(int x, int y) {
                if (mousePressed && Math.abs(x - dragPressX) > 24) draggingTab = true;

                if (draggingTab) {
                    dragCurrentX = x;

                    if (x < 20) tabScroll++;
                    if (x > getChatWidth() - 20) tabScroll--;

                    List<String> keys = ChatFilterContainer.getChatPageEntries();

                    int activeKeyPos = keys.indexOf(ChatFilterContainer.getActiveChatPageName());

                    if (activeKeyPos > 0)
                        if (overlappingLeft(activeKeyPos - 1, tabLocX + dragCurrentX - dragPressX, 4)) {
                            ChatFilterContainer.swapKeys(activeKeyPos - 1, activeKeyPos);
                            return true;
                        }

                    if (activeKeyPos < keys.size() - 1)
                        if (overlappingRight(activeKeyPos + 1, tabLocX + dragCurrentX - dragPressX + getTabWidthRange(activeKeyPos, activeKeyPos + 1) + 2, 4)) {
                            ChatFilterContainer.swapKeys(activeKeyPos + 1, activeKeyPos);
                            return true;
                        }

                    return true;
                } else {
                    if (y > getLineCount() * 9 + 1 && y <= getLineCount() * 9 + 14) {
                        if (x > 2 && x < getChatWidth() - 4) {
                            tabOver = getTabName(x);

                            return true;
                        }
                    }
                }

                tabOver = null;

                return false;
            }
        });

        mouseHandlers.add(new RelativeMouseEventHandler() {
            public boolean leftBtn(int x, int y, boolean pressed) {
                if (!pressed) return false;
                if (y > getLineCount() * 9 + 1 && y <= getLineCount() * 9 + 14) {
                    if (x >= getChatWidth() - 2 && x < getChatWidth() + 13) {
                        MinecraftClass.getMinecraft().getPlayer().playSound("Click", 1, 0.25f);

                        showMenu = !showMenu;

                        return true;
                    }
                }

                if (x >= getChatWidth() + 15 && x <= getChatWidth() + 56) {
                    if (y > getLineCount() * 9 - 44 && y <= getLineCount() * 9 + 17) {
                        return true;
                    }
                }

                showMenu = false;

                return false;
            }

            public boolean position(int x, int y) {
                return overOptBtn = y > getLineCount() * 9 + 1 && y <= getLineCount() * 9 + 15 && x >= getChatWidth() - 2 && x <= getChatWidth() + 13;
            }
        });

        mouseHandlers.add(new RelativeMouseEventHandler() {
            public boolean leftBtn(int x, int y, boolean pressed) {
                if (!showMenu) return false;

                if (!pressed) return false;

                if (x >= getChatWidth() + 15 && x <= getChatWidth() + 55) {
                    if (y > getLineCount() * 9 + 3 && y <= getLineCount() * 9 + 15) {
                        showMenu = false;

                        GuiFilteredChat newChat = new GuiFilteredChat(mcIn, "");

                        mc.displayGuiScreen(new GuiFilterEditor("", newChat, false, false));
                        MinecraftClass.getMinecraft().getPlayer().playSound("Click", 1, 0.25f);

                        return true;
                    }

                    if (y > getLineCount() * 9 - 12 && y <= getLineCount() * 9) {
                        showMenu = false;

                        String chatName = ChatFilterContainer.getActiveChatPageName();

                        mc.displayGuiScreen(new GuiFilterEditor(chatName, ChatFilterContainer.getChatPage(chatName), true, chatName.equals("Public Chat")));
                        MinecraftClass.getMinecraft().getPlayer().playSound("Click", 1, 0.25f);

                        return true;
                    }

                    if (y > getLineCount() * 9 - 27 && y <= getLineCount() * 9 - 15) {
                        showMenu = false;

                        mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                            public void confirmClicked(boolean result, int id) {
                                if (!result) {
                                    mc.displayGuiScreen(null);
                                    return;
                                }

                                if (ChatFilterContainer.getActiveChatPageName().equals("Public Chat")) {
                                    ChatFilterContainer.getActiveChatPage().printChatMessage(new ChatComponentTextInstance(ChatColor.GOLD + "[Public Chat]: Psyche!"), true);
                                    ChatFilterContainer.getActiveChatPage().printChatMessage(new ChatComponentTextInstance(ChatColor.GOLD + "[Public Chat]: You can't get rid of me! >:D"), true);

                                    mc.displayGuiScreen(null);

                                    return;
                                }

                                String activePage = ChatFilterContainer.getActiveChatPageName();

                                boolean passedActivePage = false;

                                String newActivePage = null;

                                for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
                                    if (passedActivePage) {
                                        if (newActivePage != null) break;
                                    }

                                    if (chatPage.equals(activePage)) {
                                        passedActivePage = true;
                                        continue;
                                    }

                                    newActivePage = chatPage;
                                }

                                ChatFilterContainer.setActiveChatPage(newActivePage);
                                ChatFilterContainer.getActiveChatPage().resetUnviewedMsgs();
                                ChatFilterContainer.remove(activePage);

                                mc.displayGuiScreen(new GuiChat());

                                try {
                                    ChatMate.saveFilters();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }, "Once \u00a7l" + ChatFilterContainer.getActiveChatPageName() + "\u00a7r is removed, you will have to make a new filter to recover it.", "Are you sure you wish to remove this filter?", 0) {
                            boolean mousePressed = true;

                            protected void actionPerformed(GuiButton button) throws IOException {
                                if (mousePressed) return;
                                this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
                            }

                            protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
                                super.mouseReleased(mouseX, mouseY, mouseButton);

                                mousePressed = false;
                            }
                        });

                        MinecraftClass.getMinecraft().getPlayer().playSound("Click", 1, 0.25f);

                        return true;
                    }

                    if (y > getLineCount() * 9 - 42 && y <= getLineCount() * 9 - 30) {
                        ChatFilterContainer.getActiveChatPage().toggle();
                        MinecraftClass.getMinecraft().getPlayer().playSound("Click", 1, 0.25f);

                        try {
                            ChatMate.saveFilters();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return true;
                    }
                }

                return false;
            }

            public boolean position(int x, int y) {
                if (x >= getChatWidth() + 15 && x <= getChatWidth() + 55) {
                    for (int i = 0; i < 4; i++) {
                        if (y > getLineCount() * 9 - i * 15 + 3 && y <= getLineCount() * 9 - i * 15 + 15) {
                            overOptID = i;
                            return true;
                        }
                    }
                }

                overOptID = -1;

                return false;
            }
        });
    }

    private String getTabName(int mouseX) {
        int offs = tabScroll + 2;
        for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
            offs += mc.fontRendererObj.getStringWidth(chatPage) + spacing + 1;

            int unviewedMsgs = ChatFilterContainer.getChatPage(chatPage).getUnviewedMsgs();
            if (unviewedMsgs > 0) {
                offs += mc.fontRendererObj.getStringWidth("" + unviewedMsgs) + 8;
            }

            if (mouseX < offs) return chatPage;
        }

        return null;
    }

    private boolean overlappingLeft(int testIdx, int activeLeftMarginPos, int responseThresh) {
        int offs = tabScroll + 2;

        List<String> keys = ChatFilterContainer.getChatPageEntries();

        for (int i = 0; i < testIdx; i++) {
            offs += mc.fontRendererObj.getStringWidth(keys.get(i)) + spacing + 1;

            int unviewedMsgs = ChatFilterContainer.getChatPage(keys.get(i)).getUnviewedMsgs();
            if (unviewedMsgs > 0) {
                offs += mc.fontRendererObj.getStringWidth("" + unviewedMsgs) + 8;
            }
        }

        offs += responseThresh;

        return offs >= activeLeftMarginPos;
    }

    private boolean overlappingRight(int testIdx, int activeRightMarginPos, int responseThresh) {
        int offs = tabScroll + 2 - spacing;

        List<String> keys = ChatFilterContainer.getChatPageEntries();

        for (int i = 0; i <= testIdx; i++) {
            offs += mc.fontRendererObj.getStringWidth(keys.get(i)) + spacing + 1;

            int unviewedMsgs = ChatFilterContainer.getChatPage(keys.get(i)).getUnviewedMsgs();
            if (unviewedMsgs > 0) {
                offs += mc.fontRendererObj.getStringWidth("" + unviewedMsgs) + 8;
            }
        }

        offs -= responseThresh;

        return offs <= activeRightMarginPos;
    }

    private int getTabWidthRange(int start, int end) {
        List<String> keys = ChatFilterContainer.getChatPageEntries();

        int width = 0;

        for (int i = start; i < end; i++) {
            width += mc.fontRendererObj.getStringWidth(keys.get(i)) + spacing + 1;

            int unviewedMsgs = ChatFilterContainer.getChatPage(keys.get(i)).getUnviewedMsgs();
            if (unviewedMsgs > 0) {
                width += mc.fontRendererObj.getStringWidth("" + unviewedMsgs) + 8;
            }
        }

        return width;
    }

    public void resetTabUpdateCounter() {
        tabChanged = true;
    }

    public void drawChatCompat(int updateCounter) {
        if (mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            if (tabChanged) {
                tabUpdateCounter = updateCounter;
                tabChanged = false;
            }

            boolean flag;
            float f = mc.gameSettings.chatOpacity * 0.9F + 0.1F;

            flag = this.getChatOpen();

            float f1 = this.getChatScale();

            GlStateManager.pushMatrix();
            GlStateManager.translate(6.0F, 6.0F, 0.0F);
            GlStateManager.scale(f1, f1, 1.0F);

            if (updateCounter - tabUpdateCounter < 100) {
                double d0 = (double) (updateCounter - tabUpdateCounter) / 100.0D;
                d0 = 1.0D - d0;
                d0 = d0 * 5.0D;
                d0 = MathHelperClass.clamp(d0, 0.0D, 1.0D);
                d0 = d0 * d0;
                int l1 = (int) (255.0D * d0);

                l1 = (int) ((float) l1 * f);

                if (l1 > 3) {
                    drawRect(3, 9, mc.fontRendererObj.getStringWidth(ChatFilterContainer.getActiveChatPageName()) + 7, 22, l1 / 2 << 24);
                    GlStateManager.enableBlend();
                    mc.fontRendererObj.drawStringWithShadow(ChatFilterContainer.getActiveChatPageName(), 5, 12, 16777215 + (l1 << 24));
                    GlStateManager.disableAlpha();
                    GlStateManager.disableBlend();
                }
            }

            if (flag) {
                int scaledChatWidth = getChatWidth();
                int scaledChatHeight = getLineCount() * 9;

                tabScroll = MathHelperClass.clamp(tabScroll, Math.min(-tabScrollLimit, 0), 0);

                drawVerticalLine(-5, -scaledChatHeight - 18, 2, 0xcccccc + ((int) (0xff * f) << 24)); // Left
                drawHorizontalLine(-4, scaledChatWidth + 4, 1, 0xcccccc + ((int) (0xff * f) << 24)); // Bottom
                drawVerticalLine(scaledChatWidth + 5, -scaledChatHeight - 18, 2, 0xcccccc + ((int) (0xff * f) << 24)); // Right
                drawHorizontalLine(-4, scaledChatWidth + 4, -scaledChatHeight - 17, 0xcccccc + ((int) (0xff * f) << 24)); //Tab Top

                drawVerticalLine(scaledChatWidth - 10, -scaledChatHeight - 17, -scaledChatHeight - 1, 0xcccccc + ((int) (0xff * f) << 24));
                drawHorizontalLine(scaledChatWidth - 9, scaledChatWidth + 4, -scaledChatHeight - 2, 0xcccccc + ((int) (0xff * f) << 24));

                int shadowColor = (overOptBtn ? 0x55aaff : (showMenu ? 0x55ff55 : 0xcccccc));

                shadowColor = (shadowColor & 16579836) >> 2 | shadowColor & -16777216;

                GuiMultiChat.drawRect(scaledChatWidth - 6, -scaledChatHeight - 13, scaledChatWidth + 4, -scaledChatHeight - 11, shadowColor + ((int) (0xff * f) << 24));
                GuiMultiChat.drawRect(scaledChatWidth - 6, -scaledChatHeight - 9, scaledChatWidth + 4, -scaledChatHeight - 7, shadowColor + ((int) (0xff * f) << 24));
                GuiMultiChat.drawRect(scaledChatWidth - 6, -scaledChatHeight - 5, scaledChatWidth + 4, -scaledChatHeight - 3, shadowColor + ((int) (0xff * f) << 24));

                GuiMultiChat.drawRect(scaledChatWidth - 7, -scaledChatHeight - 14, scaledChatWidth + 3, -scaledChatHeight - 12, (overOptBtn ? 0x55aaff : (showMenu ? 0x55ff55 : 0xcccccc)) + ((int) (0xff * f) << 24));
                GuiMultiChat.drawRect(scaledChatWidth - 7, -scaledChatHeight - 10, scaledChatWidth + 3, -scaledChatHeight - 8, (overOptBtn ? 0x55aaff : (showMenu ? 0x55ff55 : 0xcccccc)) + ((int) (0xff * f) << 24));
                GuiMultiChat.drawRect(scaledChatWidth - 7, -scaledChatHeight - 6, scaledChatWidth + 3, -scaledChatHeight - 4, (overOptBtn ? 0x55aaff : (showMenu ? 0x55ff55 : 0xcccccc)) + ((int) (0xff * f) << 24));

                // End "options"

                int offs = tabScroll - 5;

                if (showMenu) {
                    drawHorizontalLine(scaledChatWidth + 6, scaledChatWidth + 47, -scaledChatHeight - 17, 0xcccccc + ((int) (0xff * f) << 24));
                    drawHorizontalLine(scaledChatWidth + 6, scaledChatWidth + 47, -scaledChatHeight - 2, 0xcccccc + ((int) (0xff * f) << 24));
                    drawHorizontalLine(scaledChatWidth + 6, scaledChatWidth + 47, -scaledChatHeight + 13, 0xcccccc + ((int) (0xff * f) << 24));
                    drawHorizontalLine(scaledChatWidth + 6, scaledChatWidth + 47, -scaledChatHeight + 28, 0xcccccc + ((int) (0xff * f) << 24));
                    drawHorizontalLine(scaledChatWidth + 6, scaledChatWidth + 47, -scaledChatHeight + 43, 0xcccccc + ((int) (0xff * f) << 24));
                    drawVerticalLine(scaledChatWidth + 48, -scaledChatHeight - 18, -scaledChatHeight + 44, 0xcccccc + ((int) (0xff * f) << 24));

                    drawRect(scaledChatWidth + 7, -scaledChatHeight - 15, scaledChatWidth + 47, -scaledChatHeight - 3, ((int) (0xff * f) / 4 << 24));
                    drawRect(scaledChatWidth + 7, -scaledChatHeight, scaledChatWidth + 47, -scaledChatHeight + 12, ((int) (0xff * f) / 4 << 24));
                    drawRect(scaledChatWidth + 7, -scaledChatHeight + 15, scaledChatWidth + 47, -scaledChatHeight + 27, ((int) (0xff * f) / 4 << 24));
                    drawRect(scaledChatWidth + 7, -scaledChatHeight + 30, scaledChatWidth + 47, -scaledChatHeight + 42, ((int) (0xff * f) / 4 << 24));

                    GlStateManager.enableBlend();
                    drawCenteredString(mc.fontRendererObj, "Add", scaledChatWidth + 27, -scaledChatHeight - 13, (overOptID == 0 ? 0x55ff55 : 0xeeeeee) + ((int) (0xff * f) << 24));
                    drawCenteredString(mc.fontRendererObj, "Edit", scaledChatWidth + 27, -scaledChatHeight + 2, (overOptID == 1 ? 0xffaa00 : 0xeeeeee) + ((int) (0xff * f) << 24));
                    drawCenteredString(mc.fontRendererObj, "Remove", scaledChatWidth + 27, -scaledChatHeight + 17, (overOptID == 2 ? 0xff5555 : 0xeeeeee) + ((int) (0xff * f) << 24));
                    drawCenteredString(mc.fontRendererObj, ChatFilterContainer.getActiveChatPage().isEnabled() ? "Disable" : "Enable", scaledChatWidth + 27, -scaledChatHeight + 32, (overOptID == 3 ? (ChatFilterContainer.getActiveChatPage().isEnabled() ? 0xff5555 : 0x55ff55) : 0xeeeeee) + ((int) (0xff * f) << 24));
                    GlStateManager.disableBlend();
                }

                if (ChatFilterContainer.getChatPageEntries().size() == 0)
                    drawHorizontalLine(-4, scaledChatWidth - 11, -scaledChatHeight - 2, 0xcccccc + ((int) (0xff * f) << 24));

                int activeEndX = 0;
                int activeStringWidth = 0;

                int numTabs = ChatFilterContainer.getChatPageEntries().size();

                int activeTab = ChatFilterContainer.getChatPageEntries().indexOf(ChatFilterContainer.getActiveChatPageName());

                for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
                    int endX = draggingTab && ChatFilterContainer.isActivePage(chatPage) ? Math.max(Math.min(tabLocX + 1 + dragCurrentX - dragPressX, getTabWidthRange(0, numTabs) - getTabWidthRange(activeTab, activeTab + 1) + tabScroll - 4), -4) : offs + 1;

                    if (ChatFilterContainer.isActivePage(chatPage)) {
                        if (Math.max(endX, -4) != -4)
                            drawHorizontalLine(-4, Math.min(endX - 1, scaledChatWidth - 12), -scaledChatHeight - 2, 0xcccccc + ((int) (0xff * f) << 24));
                    }

                    offs += mc.fontRendererObj.getStringWidth(chatPage) + spacing + 1;

                    int startX = draggingTab && ChatFilterContainer.isActivePage(chatPage) ? Math.min(Math.max(Math.min(tabLocX + dragCurrentX - dragPressX, getTabWidthRange(0, numTabs) - getTabWidthRange(activeTab, activeTab + 1) + tabScroll - 4), -4), scaledChatWidth - 11) : Math.min(Math.max(offs, -4), scaledChatWidth - 11);

                    if (ChatFilterContainer.isActivePage(chatPage)) {
                        activeEndX = endX;
                        activeStringWidth = mc.fontRendererObj.getStringWidth(chatPage);

                        if (startX != scaledChatWidth - 11)
                            drawHorizontalLine(startX, scaledChatWidth - 11, -scaledChatHeight - 2, 0xcccccc + ((int) (0xff * f) << 24));
                    } else if (tabOver == null || !tabOver.equals(chatPage)) {
                        GuiMultiChat.drawRect(Math.min(Math.max(endX, -4), scaledChatWidth - 12) + 1, -scaledChatHeight - 15, Math.min(Math.max(offs - 1, -3), scaledChatWidth - 11), -scaledChatHeight - 3, ((int) (0xff * f) / 4 << 24));
                    }

                    int missedChatMsgs = ChatFilterContainer.getChatPage(chatPage).getUnviewedMsgs();

                    if (missedChatMsgs > 0) {
                        String number = "" + missedChatMsgs;
                        int numColor = (int) (0xff * f) << 24;
                        int numStart = offs - 2;

                        offs += mc.fontRendererObj.getStringWidth(number) + 8;

                        GuiMultiChat.drawRect(Math.max(Math.min(numStart, scaledChatWidth - 12), -4) + 1, -scaledChatHeight - 15, Math.max(Math.min(offs - 2, scaledChatWidth - 12), -4) + 1, -scaledChatHeight - 3, numColor + 0x00aa00);
                    }

                    if (offs >= getChatWidth() - 12) continue;
                    if (offs <= -4) continue;

                    drawVerticalLine(offs, -scaledChatHeight - 17, -scaledChatHeight - 2, 0xcccccc + ((int) (0xff * f) << 24));
                }

                offs = tabScroll - 5;

                int activeTextOffs = 0;
                String activeClippedStr = "";

                for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
                    int textOffs = draggingTab && ChatFilterContainer.isActivePage(chatPage) ? Math.max(Math.min(tabLocX + 3 + dragCurrentX - dragPressX, getTabWidthRange(0, numTabs) - getTabWidthRange(activeTab, activeTab + 1) + tabScroll - 2), -2) : offs + 3;
                    int textEnd = draggingTab && ChatFilterContainer.isActivePage(chatPage) ? Math.max(Math.min(tabLocX + 3 + dragCurrentX - dragPressX, getTabWidthRange(0, numTabs) - getTabWidthRange(activeTab, activeTab + 1) + tabScroll - 2), -2) : offs + 3;

                    textEnd += mc.fontRendererObj.getStringWidth(chatPage) + spacing + 1;
                    offs += mc.fontRendererObj.getStringWidth(chatPage) + spacing + 1;

                    int color = (int) (0xff * f) << 24;

                    if (tabOver == null || !tabOver.equals(chatPage)) {
                        color += (ChatFilterContainer.getChatPage(chatPage).isEnabled() ? 0xeeeeee : 0xaaaaaa);
                    } else {
                        color += 0x55aaff;
                    }

                    int substrStart = 0;
                    int substrEnd = 0;

                    while (substrStart != chatPage.length() && textOffs <= -4) {
                        int charWidth = mc.fontRendererObj.getCharWidth(chatPage.charAt(substrStart));
                        textOffs += charWidth;
                        substrStart++;
                    }

                    while (substrEnd != chatPage.length() && textEnd >= getChatWidth() - 12 + spacing) {
                        textEnd -= mc.fontRendererObj.getCharWidth(chatPage.charAt(chatPage.length() - substrEnd - 1));
                        substrEnd++;
                    }

                    if (ChatFilterContainer.isActivePage(chatPage)) {
                        activeClippedStr = chatPage.substring(substrStart, chatPage.length() - substrEnd);
                        activeTextOffs = textOffs;

                        continue;
                    }

                    if (chatPage.length() - substrEnd - substrStart > 0) {
                        GlStateManager.enableBlend();
                        mc.fontRendererObj.drawStringWithShadow((ChatFilterContainer.getChatPage(chatPage).isEnabled() ? "" : ChatColor.STRIKETHROUGH) + chatPage.substring(substrStart, chatPage.length() - substrEnd), textOffs + 1, -scaledChatHeight - 13, color);
                        GlStateManager.disableBlend();
                    }

                    int missedChatMsgs = ChatFilterContainer.getChatPage(chatPage).getUnviewedMsgs();

                    if (missedChatMsgs > 0) {
                        String number = "" + missedChatMsgs;
                        int numColor = (int) (0xff * f) << 24;

                        textOffs = offs + 2;
                        textEnd = offs;

                        textEnd += mc.fontRendererObj.getStringWidth(number) + 8;
                        offs += mc.fontRendererObj.getStringWidth(number) + 8;

                        substrStart = 0;
                        substrEnd = 0;

                        while (substrStart != number.length() && textOffs <= -4) {
                            textOffs += mc.fontRendererObj.getCharWidth(number.charAt(substrStart));
                            substrStart++;
                        }

                        while (substrEnd != number.length() && textEnd >= getChatWidth() - 12 + spacing) {
                            textEnd -= mc.fontRendererObj.getCharWidth(number.charAt(number.length() - substrEnd - 1));
                            substrEnd++;
                        }

                        if (number.length() - substrEnd - substrStart > 0) {
                            GlStateManager.enableBlend();

                            mc.fontRendererObj.drawStringWithShadow(number.substring(substrStart, number.length() - substrEnd), textOffs + 1, -scaledChatHeight - 13, numColor + (updateCounter / 10 % 2 == 0 ? 0xffffff : 0xff5555));
                            GlStateManager.disableBlend();
                        }
                    }
                }

                if (ChatFilterContainer.getChatPageEntries().size() > 0) {
                    GuiMultiChat.drawRect(Math.min(Math.max(activeEndX, -4), scaledChatWidth - 12) + 1, -scaledChatHeight - 15, Math.min(Math.max(activeEndX + activeStringWidth + spacing - 1, -3), scaledChatWidth - 11), -scaledChatHeight - 3, ((int) (0xff * f) / 2 << 24));

                    GlStateManager.enableBlend();
                    mc.fontRendererObj.drawStringWithShadow((ChatFilterContainer.getActiveChatPage().isEnabled() ? "" : ChatColor.STRIKETHROUGH) + activeClippedStr, activeTextOffs + 1, -scaledChatHeight - 13, (ChatFilterContainer.getActiveChatPage().isEnabled() ? 0x55ff55 : 0xeeeeee) + ((int) (0xff * f) << 24));
                    GlStateManager.disableBlend();
                }

                tabScrollLimit = offs - tabScroll - getChatWidth() + 10;
            } else showMenu = false;

            ChatFilterContainer.drawActivePage(updateCounter - lastUpdate, flag, f);

            GlStateManager.popMatrix();
        }

        lastUpdate = updateCounter;
    }

    public void clearChatMessagesCompat() {
        GuiFilteredChat chat = ChatFilterContainer.getActiveChatPage();
        if (chat != null) {
            chat.resetUnviewedMsgs();
            chat.clearChatMessages();
        }
    }

    public void printChatMessageCompat(ChatComponentInstance chatComponent) {
        printChatMessageCompat(chatComponent, false);
    }

    public void printChatMessageCompat(ChatComponentInstance chatComponent, boolean override) {
        ChatSound sound = null;
        logger.info("[CHAT] " + chatComponent.getUnformattedText());

        boolean captured = false;

        for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
            if (ChatFilterContainer.getChatPage(chatPage).isBlacklisting()) {
                ChatSound newSound = ChatFilterContainer.getChatPage(chatPage).printChatMessage(chatComponent, override);

                if (sound == null) sound = newSound;

                if (sound != null) {
                    if (!ChatFilterContainer.isActivePage(chatPage))
                        ChatFilterContainer.getChatPage(chatPage).incrementMsg();

                    captured = true;
                    if (!override) break;
                }
            }
        }

        if (!captured || override) {
            for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
                if (ChatFilterContainer.getChatPage(chatPage).isBlacklisting()) continue;

                ChatSound newSound;

                newSound = ChatFilterContainer.getChatPage(chatPage).printChatMessage(chatComponent, override);

                if (!ChatFilterContainer.isActivePage(chatPage) && newSound != null)
                    ChatFilterContainer.getChatPage(chatPage).incrementMsg();

                if (sound == null || sound.getName().equals("None")) sound = newSound;
            }
        }

        if (sound == null) return;

        if (!override) MinecraftClass.getMinecraft().getPlayer().playSound(sound.getName(), 1, 1);
    }

    public void printChatMessageWithOptionalDeletionCompat(ChatComponentInstance chatComponent, int chatLineId) {
        printChatMessageWithOptionalDeletionCompat(chatComponent, chatLineId, false);
    }

    public void printChatMessageWithOptionalDeletionCompat(ChatComponentInstance chatComponent, int chatLineId, boolean override) {
        ChatSound sound = null;

        for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
            ChatSound newSound;

            newSound = ChatFilterContainer.getChatPage(chatPage).printChatMessageWithOptionalDeletion(chatComponent, chatLineId, override);

            if (!ChatFilterContainer.isActivePage(chatPage) && newSound != null)
                ChatFilterContainer.getChatPage(chatPage).incrementMsg();

            if (sound == null) sound = newSound;
        }

        if (sound == null) return;

        if (!override) MinecraftClass.getMinecraft().getPlayer().playSound(sound.getName(), 1, 1);
    }

    public void refreshChatCompat() {
        for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
            ChatFilterContainer.getChatPage(chatPage).refreshChat();
        }
    }

    public List<String> getSentMessagesCompat() {
        return this.sentMessages;
    }

    public void addToSentMessagesCompat(String message) {
        if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
            this.sentMessages.add(message);
        }
    }

    public void resetScrollCompat() {
        for (String chatPage : ChatFilterContainer.getChatPageEntries()) {
            ChatFilterContainer.getChatPage(chatPage).resetScroll();
        }

        tabScroll = 0;
    }

    public void scrollCompat(int amount) {
        if (this.getChatOpen()) {
            int mouseX = Mouse.getX();
            int mouseY = Mouse.getY();

            ScaledResolution scaledresolution = (ScaledResolution) CompatUtility.clScaledResolution.newInstance(CompatUtility.ctorScaledResolution, mc, mc.displayWidth, mc.displayHeight).getBaseInstance();
            int sf = scaledresolution.getScaleFactor();
            float f = this.getChatScale();

            int j = mouseX / sf - 5;
            int k = mouseY / sf - 41;
            j = MathHelperClass.floor((float) j / f) + 6;
            k = MathHelperClass.floor((float) k / f);

            boolean exit = false;

            for (RelativeMouseEventHandler h : mouseHandlers) {
                if (amount != 0) {
                    exit |= h.scroll(j, k, amount);
                }
            }

            if (exit) return;
        }

        ChatFilterContainer.getActiveChatPage().scroll(amount);
    }

    public Object getChatComponentCompat(int mouseX, int mouseY) {
        ScaledResolution scaledresolution = (ScaledResolution) CompatUtility.clScaledResolution.newInstance(CompatUtility.ctorScaledResolution, mc, mc.displayWidth, mc.displayHeight).getBaseInstance();
        int sf = scaledresolution.getScaleFactor();
        float f = this.getChatScale();

        int j = mouseX / sf - 5;
        int k = mouseY / sf - 41;
        j = MathHelperClass.floor((float) j / f) + 6;
        k = MathHelperClass.floor((float) k / f);

        boolean exit = false;

        for (RelativeMouseEventHandler h : mouseHandlers) {
            if (b0P != Mouse.isButtonDown(0)) {
                exit |= h.leftBtn(j, k, Mouse.isButtonDown(0));
            }

            if (b1P != Mouse.isButtonDown(1)) {
                exit |= h.rightBtn(j, k, Mouse.isButtonDown(1));
            }

            if (b2P != Mouse.isButtonDown(2)) {
                exit |= h.middleBtn(j, k, Mouse.isButtonDown(2));
            }

            exit |= h.position(j, k);
        }

        b0P = Mouse.isButtonDown(0);
        b1P = Mouse.isButtonDown(1);
        b2P = Mouse.isButtonDown(2);

        if (exit) return null;
        if (ChatFilterContainer.getActiveChatPage() == null) return null;

        return ChatFilterContainer.getActiveChatPage().getChatComponentCompat(mouseX, mouseY);
    }

    public void deleteChatLineCompat(int id) {
        ChatFilterContainer.getActiveChatPage().deleteChatLine(id);
    }

    public static boolean getChatOpenCompat(Minecraft mc) {
        return mc.currentScreen instanceof GuiChat;
    }

    public static int getChatWidthCompat(Minecraft mc) {
        return calculateChatboxWidth(mc.gameSettings.chatWidth);
    }

    public static int getChatHeightCompat(Minecraft mc) {
        return calculateChatboxHeight(getChatOpenCompat(mc) ? mc.gameSettings.chatHeightFocused : mc.gameSettings.chatHeightUnfocused);
    }

    public static float getChatScaleCompat(Minecraft mc) {
        return mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        int i = 320;
        int j = 40;
        return floor(scale * (float) (i - j) + (float) j);
    }

    public static int calculateChatboxHeight(float scale) {
        int i = 180;
        int j = 20;

        return floor(scale * (float) (i - j) + (float) j);
    }

    public static int floor(float value) {
        int i = (int) value;
        return value < (float) i ? i - 1 : i;
    }

    public static int getLineCountCompat(Minecraft mc) {
        return getChatHeightCompat(mc) / 9;
    }

    private class RelativeMouseEventHandler {
        public boolean leftBtn(int x, int y, boolean pressed) {
            return false;
        }

        public boolean middleBtn(int x, int y, boolean pressed) {
            return false;
        }

        public boolean rightBtn(int x, int y, boolean pressed) {
            return false;
        }

        public boolean position(int x, int y) {
            return false;
        }

        public boolean scroll(int x, int y, int amount) {
            return false;
        }
    }
}