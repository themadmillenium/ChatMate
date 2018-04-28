package dev.tmm.chatmate.gui;

import dev.tmm.chatmate.ChatMate;
import dev.tmm.chatmate.core.ChatFilterContainer;
import dev.tmm.chatmate.util.ChatColor;
import dev.tmm.chatmate.util.FilterPreset;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class GuiFilterEditor extends GuiScreen {
    private static final String blacklistOff = "Blacklist: OFF";
    private static final String blacklistOn = "Blacklist: ON";

    private GuiTextField nameField;
    private GuiTextField filterInputField;

    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    private GuiButton blacklistBtn;

    private GuiButton presetBtn;

    private FilterPreset preset;

    private GuiFilteredChat chat;

    private String nameStr;
    private String filterInputStr;

    private boolean modifying;
    private boolean lock;

    private boolean nameTaken;

    private boolean validPattern;
    private String errorStr;
    private int errorIdx;

    private boolean guiInitialized;

    private boolean mousePressed;

    public GuiFilterEditor(String name, GuiFilteredChat chat, boolean modifying, boolean lock) {
        this.nameStr = name;
        this.modifying = modifying;
        this.lock = lock;

        this.chat = chat;

        preset = chat.getPreset();
        this.filterInputStr = ChatColor.getSymbolizedString(preset.transformInput(chat.getPatternStr()));

        validPattern = true;
        errorIdx = -1;

        mousePressed = true;
        guiInitialized = false;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.clear();

        buttonList.add(cancelBtn = new GuiButton(2, this.width / 2 - 140, this.height / 4 + 146, 48, 20, "Cancel"));
        buttonList.add(blacklistBtn = new GuiButton(3, this.width / 2 - 88, this.height / 4 + 146, 81, 20, blacklistOff));
        buttonList.add(new GuiButton(4, this.width / 2 - 3, this.height / 4 + 146, 81, 20, "Select Sound"));
        buttonList.add(doneBtn = new GuiButton(5, this.width / 2 + 82, this.height / 4 + 146, 48, 20, "Done"));

        buttonList.add(presetBtn = new GuiButton(6, this.width / 2 - 50, 100, 100, 20, preset.toString().replace('_', ' ')));

        presetBtn.enabled = !lock;

        nameField = new GuiTextField(0, mc.fontRendererObj, this.width / 2 - 75, 42, 150, 20);
        nameField.setMaxStringLength(30);
        nameField.setFocused(!modifying);
        nameField.setText(nameStr);

        nameField.setEnabled(!modifying);

        filterInputField = new GuiTextField(1, mc.fontRendererObj, this.width / 2 - 100, 149, 200, 20);
        filterInputField.setMaxStringLength(32767);

        filterInputField.setText(filterInputStr);

        blacklistBtn.displayString = chat.isBlacklisting() ? blacklistOn : blacklistOff;

        doneBtn.enabled = permitDone();

        if (guiInitialized) return;
        if (preset == FilterPreset.Key_Words) {
            filterInputField.setText(filterInputStr);
        } else if (preset == FilterPreset.Custom) {
            filterInputField.setText(filterInputStr);
        }

        filterInputField.setEnabled(!lock);
        filterInputField.setFocused(modifying && !lock);

        doneBtn.enabled = permitDone();

        guiInitialized = true;
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateScreen() {
        nameField.updateCursorCounter();
        filterInputField.updateCursorCounter();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 2:
                mc.displayGuiScreen(new GuiChat());
                break;
            case 3:
                if (button.displayString == blacklistOff) {
                    button.displayString = blacklistOn;
                } else {
                    button.displayString = blacklistOff;
                }

                chat.setBlacklisting(blacklistBtn.displayString == blacklistOn);

                break;
            case 4:
                mc.displayGuiScreen(new SoundList(this, chat));
                break;
            case 5:
                if (button.enabled) {
                    chat.setPattern(preset.getPattern(filterInputStr));

                    chat.setPreset(preset);

                    ChatFilterContainer.set(nameField.getText(), chat);
                    mc.displayGuiScreen(new GuiChat());

                    ChatMate.saveFilters();
                }

                break;
            case 6:
                filterInputStr = "";
                filterInputField.setText("");

                presetBtn.displayString = (preset = preset.next()).toString().replace('_', ' ');

                doneBtn.enabled = permitDone();

                break;
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mousePressed) return;

        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
        filterInputField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);

        mousePressed = false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        nameField.textboxKeyTyped(typedChar, keyCode);
        filterInputField.textboxKeyTyped(typedChar, keyCode);
        doneBtn.enabled = permitDone();

        nameStr = nameField.getText();
        filterInputStr = filterInputField.getText();

        if (keyCode == 15) {
            if (isShiftKeyDown()) {
                if (!nameField.isFocused()) {
                    nameField.setFocused(true);
                    filterInputField.setFocused(false);
                }
            } else {
                if (!filterInputField.isFocused()) {
                    nameField.setFocused(false);
                    filterInputField.setFocused(true);
                }
            }
        }

        if (preset == FilterPreset.Custom) try {
            validPattern = true;
            errorIdx = -1;

            Pattern.compile(filterInputField.getText(), Pattern.UNICODE_CHARACTER_CLASS | Pattern.DOTALL | Pattern.MULTILINE);
        } catch (PatternSyntaxException e) {
            doneBtn.enabled = false;
            validPattern = false;

            errorStr = e.getDescription();
            errorIdx = e.getIndex();
        }

        if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 1) {
                actionPerformed(cancelBtn);
            }
        } else {
            actionPerformed(doneBtn);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(mc.fontRendererObj, (modifying ? "Change" : "Create") + " Chat Filter", this.width / 2, 10, 0xffffff);

        drawCenteredString(mc.fontRendererObj, "Filter Name", this.width / 2, 29, 0xffffff);
        if (nameTaken)
            drawCenteredString(mc.fontRendererObj, "This name has already been taken!", this.width / 2, 66, 0xff5555);

        drawCenteredString(mc.fontRendererObj, "Preset", this.width / 2, 88, 0xffffff);

        if (!ChatMate.onHypixel() && preset != FilterPreset.Custom && preset != FilterPreset.Public_Chat && preset != FilterPreset.Key_Words) {
            drawCenteredString(mc.fontRendererObj, "Warning: You are not on Hypixel!", this.width / 2, 136, 0xffaa00);
            drawCenteredString(mc.fontRendererObj, "The preset you have selected may not work!", this.width / 2, 149, 0xffaa00);
        }

        if (preset == FilterPreset.Custom) {
            drawCenteredString(mc.fontRendererObj, "RegEx Pattern", this.width / 2, 136, 0xffffff);
            if (validPattern) drawString(mc.fontRendererObj, "No problems!", this.width / 2 - 100, 173, 0x55ff55);
            else {
                drawString(mc.fontRendererObj, "Error in pattern: " + errorStr, this.width / 2 - 100, 173, 0xff5555);

                if (errorIdx >= 0) {
                    if (errorIdx >= filterInputField.getText().length())
                        drawString(mc.fontRendererObj, filterInputField.getText(), this.width / 2 - 100, 186, 0xff5555);
                    else
                        drawString(mc.fontRendererObj, new StringBuilder(filterInputField.getText()).replace(errorIdx, errorIdx + 1, "\u00a76" + filterInputField.getText().charAt(errorIdx) + "\u00a7c").toString(), this.width / 2 - 100, 186, 0xff5555);
                }
            }

            filterInputField.drawTextBox();
        } else if (preset == FilterPreset.Key_Words) {
            drawCenteredString(mc.fontRendererObj, "Key Words (space separated)", this.width / 2, 136, 0xffffff);

            filterInputField.drawTextBox();
        }

        nameField.drawTextBox();

        if (blacklistBtn.isMouseOver()) {
            String[] hoverText = new String[4];

            hoverText[0] = ChatColor.DARK_AQUA + "Blacklist: " + (blacklistBtn.displayString == blacklistOff ? ChatColor.RED + "OFF" : ChatColor.GREEN + "ON");

            hoverText[1] = ChatColor.YELLOW + (blacklistBtn.displayString == blacklistOff ? "The filter will " + ChatColor.RED + "not" + ChatColor.YELLOW + " prevent" : "The filter will try to prevent");
            hoverText[2] = ChatColor.YELLOW + "chat messages from showing";
            hoverText[3] = ChatColor.YELLOW + "up in any other filter!";

            drawHoveringText(Arrays.asList(hoverText), mouseX, mouseY);
        }
    }

    private boolean permitDone() {
        boolean doneBtnEnabled = nameField.getText().trim().length() > 0;

        switch (preset) {
            case Key_Words:
            case Custom:
                doneBtnEnabled &= filterInputField.getText().trim().length() > 0;
                break;
        }

        doneBtnEnabled &= !(nameTaken = ChatFilterContainer.getChatPageEntries().contains(nameField.getText()) && !modifying);

        return doneBtnEnabled;
    }
}
