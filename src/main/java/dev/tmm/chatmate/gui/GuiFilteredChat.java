package dev.tmm.chatmate.gui;

import com.google.common.collect.Lists;
import dev.tmm.chatmate.compat.*;
import dev.tmm.chatmate.core.ChatSound;
import dev.tmm.chatmate.util.ChatColor;
import dev.tmm.chatmate.util.FilterPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class GuiFilteredChat extends GuiNewChat {
    private Minecraft mc;

    private Pattern pattern;

    private final List<ChatLineInstance> chatLines = Lists.newArrayList();
    private final List<ChatLineInstance> drawnChatLines = Lists.newArrayList();

    private ChatSound sound;

    private int scrollPos;
    private boolean isScrolled;

    private int unviewedMsgs;
    private boolean useColorCodes;
    private FilterPreset preset;

    private boolean blacklisting;
    private boolean enabled;

    private int updateCounter;

    public GuiFilteredChat(Minecraft mc, String pattern) {
        super(mc);
        this.mc = mc;

        this.pattern = Pattern.compile(ChatColor.getFormattedString(pattern), Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL | Pattern.MULTILINE);
        useColorCodes = ChatColor.hasColorCodes(pattern);

        sound = new ChatSound("None", 1, 1);

        preset = FilterPreset.Public_Chat;

        enabled = true;
    }

    public int getUnviewedMsgs() {
        return unviewedMsgs;
    }

    public void incrementMsg() {
        unviewedMsgs++;
    }

    public void resetUnviewedMsgs() {
        unviewedMsgs = 0;
    }

    public void setSound(ChatSound sound) {
        this.sound = sound;
    }

    public void drawChat(int updateIncrease, boolean flag, float opacity) {
        updateCounter += updateIncrease;

        if (mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int i = GuiMultiChat.getLineCountCompat(mc);
            int j = 0;
            int k = this.drawnChatLines.size();

            int l = MathHelperClass.ceil((float) GuiMultiChat.getChatWidthCompat(mc));

            for (int i1 = 0; i1 + this.scrollPos < this.drawnChatLines.size() && i1 < i; ++i1) {
                ChatLineInstance chatline = this.drawnChatLines.get(i1 + this.scrollPos);

                if (chatline != null) {
                    int j1 = updateCounter - chatline.getUpdatedCounter();

                    if (j1 < 200 || flag) {
                        double d0 = (double) j1 / 200.0D;
                        d0 = 1.0D - d0;
                        d0 = d0 * 10.0D;
                        d0 = MathHelperClass.clamp(d0, 0.0D, 1.0D);
                        d0 = d0 * d0;
                        int l1 = (int) (255.0D * d0);

                        if (flag) {
                            l1 = 255;
                        }

                        l1 = (int) ((float) l1 * opacity);
                        ++j;

                        if (l1 > 3) {
                            int j2 = -i1 * 9;
                            drawRect(0, j2 - 9, l + 4, j2, l1 / 2 << 24);
                            String s = chatline.getChatComponent().getFormattedText();
                            GlStateManager.enableBlend();
                            mc.fontRendererObj.drawStringWithShadow(s, 0, (float) (j2 - 8), 16777215 + (l1 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                }
            }

            if (flag && k > 0) {
                int k2 = mc.fontRendererObj.FONT_HEIGHT;
                GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                int l2 = k * k2;
                int i3 = j * k2;

                int j3 = this.scrollPos * i3 / k;
                int k1 = (int) Math.ceil((float) (i3 * i3) / l2);

                if (l2 != i3) {
                    int k3 = j3 > 0 ? 170 : 96;
                    int l3 = this.isScrolled ? 13382451 : 3355562;
                    drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                    drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                }
            }
        }
    }

    public void clearChatMessages() {
        this.drawnChatLines.clear();
        this.chatLines.clear();
    }

    public ChatSound printChatMessage(ChatComponentInstance chatComponent, boolean overrideFilter) {
        return printChatMessageWithOptionalDeletion(chatComponent, 0, overrideFilter);
    }

    public ChatSound printChatMessageWithOptionalDeletion(ChatComponentInstance chatComponent, int chatLineId, boolean overrideFilter) {
        return setChatLine(chatComponent, chatLineId, false, overrideFilter);
    }

    private ChatSound setChatLine(ChatComponentInstance chatComponent, int chatLineId, boolean displayOnly, boolean overrideFilter) {
        if (!overrideFilter && (!pattern.matcher(ChatColor.stripRedundancies(useColorCodes ? ChatColor.WHITE + chatComponent.getFormattedText() : chatComponent.getUnformattedText())).find() || !enabled))
            return null;

        if (chatLineId != 0) {
            this.deleteChatLine(chatLineId);
        }

        int i = MathHelperClass.floor((float) GuiMultiChat.getChatWidthCompat(mc));
        List<ChatComponentInstance> list = GuiUtilRenderComponentsClass.splitText(chatComponent, i, mc.fontRendererObj, false, false);
        boolean flag = GuiMultiChat.getChatOpenCompat(mc);

        for (ChatComponentInstance iTextComponent : list) {
            if (flag && this.scrollPos > 0) {
                this.isScrolled = true;
                this.scroll(1);
            }

            this.drawnChatLines.add(0, new ChatLineInstance(updateCounter, iTextComponent, chatLineId));
        }

        while (this.drawnChatLines.size() > 100) {
            this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
        }

        if (!displayOnly) {
            this.chatLines.add(0, new ChatLineInstance(updateCounter, chatComponent, chatLineId));

            while (this.chatLines.size() > 100) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }

        return sound;
    }

    public void refreshChat() {
        this.drawnChatLines.clear();
        this.resetScroll();

        for (int i = this.chatLines.size() - 1; i >= 0; --i) {
            ChatLineInstance chatline = this.chatLines.get(i);
            this.setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), true, true);
        }
    }

    public void resetScroll() {
        this.scrollPos = 0;
        this.isScrolled = false;
    }

    public void scroll(int amount) {
        this.scrollPos += amount;
        int i = this.drawnChatLines.size();

        if (this.scrollPos > i - GuiMultiChat.getLineCountCompat(mc)) {
            this.scrollPos = i - GuiMultiChat.getLineCountCompat(mc);
        }

        if (this.scrollPos <= 0) {
            this.scrollPos = 0;
            this.isScrolled = false;
        }
    }

    public Object getChatComponentCompat(int mouseX, int mouseY) {
        if (!this.getChatOpen()) {
            return null;
        } else {
            ScaledResolution scaledresolution = (ScaledResolution) CompatUtility.clScaledResolution.newInstance(CompatUtility.ctorScaledResolution, mc, mc.displayWidth, mc.displayHeight).getBaseInstance();
            int i = scaledresolution.getScaleFactor();
            float f = this.getChatScale();
            int j = mouseX / i - 7;
            int k = mouseY / i - 41;
            j = MathHelperClass.floor((float) j / f);
            k = MathHelperClass.floor((float) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(this.getLineCount(), this.drawnChatLines.size());

                if (j <= MathHelperClass.floor((float) this.getChatWidth() / this.getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
                    int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;

                    if (i1 >= 0 && i1 < this.drawnChatLines.size()) {
                        ChatLineInstance chatline = this.drawnChatLines.get(i1);
                        int j1 = 0;

                        for (Object ichatcomponent : (Iterable) chatline.getChatComponent().getBaseInstance()) {
                            if (CompatUtility.clChatComponentText.isInstance(ichatcomponent)) {
                                j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(new ChatComponentTextInstance(ichatcomponent).getChatComponentText_TextValue(), false));

                                if (j1 > j) {
                                    return ichatcomponent;
                                }
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    public void deleteChatLine(int id) {
        Iterator<ChatLineInstance> iterator = this.drawnChatLines.iterator();

        while (iterator.hasNext()) {
            ChatLineInstance chatline = iterator.next();

            if (chatline.getChatLineID() == id) {
                iterator.remove();
            }
        }

        iterator = this.chatLines.iterator();

        while (iterator.hasNext()) {
            ChatLineInstance chatline1 = iterator.next();

            if (chatline1.getChatLineID() == id) {
                iterator.remove();
                break;
            }
        }
    }

    public String getPatternStr() {
        return pattern.pattern();
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(ChatColor.getFormattedString(pattern), Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL | Pattern.MULTILINE);
        useColorCodes = ChatColor.hasColorCodes(pattern);
    }

    public ChatSound getSound() {
        return sound;
    }

    public void setPreset(FilterPreset preset) {
        this.preset = preset;
    }

    public FilterPreset getPreset() {
        return preset;
    }

    public void setBlacklisting(boolean blacklisting) {
        this.blacklisting = blacklisting;
    }

    public boolean isBlacklisting() {
        return blacklisting;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
