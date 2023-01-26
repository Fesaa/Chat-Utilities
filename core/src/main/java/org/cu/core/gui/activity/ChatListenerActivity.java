package org.cu.core.gui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.labymod.api.client.gui.screen.LabyScreen;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import org.cu.core.CU;
import org.cu.core.imp.ChatListener;
import org.jetbrains.annotations.Nullable;

@AutoActivity
@Link("manage_cl.lss")
@Link("overview_cl.lss")
public class ChatListenerActivity extends Activity {

  private final CU addon;

  private final Pattern isInt = Pattern.compile("\\d*");

  private final VerticalListWidget<ChatListenerWidget> chatListenerList;

  private final List<ChatListenerWidget> chatListenerWidgets;

  private ButtonWidget removeButton;
  private ButtonWidget editButton;

  private FlexibleContentWidget inputWidget;

  private Action action;

  public ChatListenerActivity(CU addon) {
    this.addon = addon;

    this.chatListenerWidgets = new ArrayList<>();
    addon.configuration().getChatListeners().forEach((ID, chatListener) -> this.chatListenerWidgets.add(new ChatListenerWidget(chatListener)));

    this.chatListenerList = new VerticalListWidget<>();
    this.chatListenerList.addId("chat-listener-list");
    this.chatListenerList.setSelectCallback(textReplacementWidget -> {
      this.editButton.setEnabled(true);
      this.removeButton.setEnabled(true);
    });

    this.chatListenerList.setDoubleClickCallback(textReplacementWidget -> this.setAction(Action.EDIT));
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("chat-listener-container");
    for (ChatListenerWidget chatListenerWidget : this.chatListenerWidgets) {
      this.chatListenerList.addChild(chatListenerWidget);
    }

    container.addFlexibleContent(new ScrollWidget(this.chatListenerList));

    ChatListenerWidget selectedChatListener = this.chatListenerList.session().getSelectedEntry();
    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("overview-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.add", () -> this.setAction(Action.ADD)));

    this.editButton = ButtonWidget.i18n("labymod.ui.button.edit", () -> this.setAction(Action.EDIT));
    this.editButton.setEnabled(Objects.nonNull(selectedChatListener));
    menu.addEntry(this.editButton);

    this.removeButton = ButtonWidget.i18n("labymod.ui.button.remove", () -> this.setAction(Action.REMOVE));
    this.removeButton.setEnabled(Objects.nonNull(selectedChatListener));
    menu.addEntry(this.removeButton);

    container.addContent(menu);
    this.document().addChild(container);
    if (Objects.isNull(this.action)) {
      return;
    }

    DivWidget manageContainer = new DivWidget();
    manageContainer.addId("manage-container");

    Widget overlayWidget;
    switch (this.action) {
      default:
      case ADD:
        ChatListenerWidget newChatListener = new ChatListenerWidget(ChatListener.createDefault());
        overlayWidget = this.initializeManageContainer(newChatListener);
        break;
      case EDIT:
        overlayWidget = this.initializeManageContainer(selectedChatListener);
        break;
      case REMOVE:
        overlayWidget = this.initializeRemoveContainer(selectedChatListener);
        break;
    }

    manageContainer.addChild(overlayWidget);
    this.document().addChild(manageContainer);
  }

  private FlexibleContentWidget initializeRemoveContainer(ChatListenerWidget chatListenerWidget) {
    this.inputWidget = new FlexibleContentWidget();
    this.inputWidget.addId("remove-container");

    ComponentWidget confirmationWidget = ComponentWidget.i18n("cu.gui.chatListener.manage.remove");
    confirmationWidget.addId("remove-confirmation");
    this.inputWidget.addContent(confirmationWidget);

    ChatListenerWidget previewWidget = new ChatListenerWidget(chatListenerWidget.getChatListener());
    previewWidget.addId("remove-preview");
    this.inputWidget.addContent(previewWidget);

    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("remove-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.remove", () -> {
      this.addon.configuration().getChatListeners().remove(chatListenerWidget.getChatListener().getID());
      this.chatListenerWidgets.remove(chatListenerWidget);
      this.chatListenerList.session().setSelectedEntry(null);
      this.setAction(null);
    }));

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    this.inputWidget.addContent(menu);

    return this.inputWidget;
  }

  private DivWidget initializeManageContainer(ChatListenerWidget chatListenerWidget) {
    // Disable buttons to prevent double tab
    this.editButton.setEnabled(false);
    this.removeButton.setEnabled(false);

    // Needed for validation
    ButtonWidget doneButton = ButtonWidget.i18n("labymod.ui.button.done");
    TextFieldWidget messageTextField = new TextFieldWidget();
    TextFieldWidget soundIDTextField = new TextFieldWidget();
    CheckBoxWidget chatCheckBox = new CheckBoxWidget();
    CheckBoxWidget commandCheckBox = new CheckBoxWidget();
    CheckBoxWidget soundCheckBox = new CheckBoxWidget();


    DivWidget inputContainer = new DivWidget();
    inputContainer.addId("input-container");

    this.inputWidget = new FlexibleContentWidget();
    this.inputWidget.addId("input-list");

    // Regex
    ComponentWidget labelRegex = ComponentWidget.i18n("cu.gui.chatListener.manage.regex");
    labelRegex.addId("label-text");
    this.inputWidget.addContent(labelRegex);

    TextFieldWidget regexTextField = new TextFieldWidget();
    regexTextField.setText(chatListenerWidget.getChatListener().getRegex());
    regexTextField.updateListener(newValue -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
                            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    this.inputWidget.addContent(regexTextField);

    // Message
    ComponentWidget labelMessage = ComponentWidget.i18n("cu.gui.chatListener.manage.message");
    labelMessage.addId("label-text");
    this.inputWidget.addContent(labelMessage);


    messageTextField.setText(chatListenerWidget.getChatListener().getMsg());
    messageTextField.updateListener(newValue -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
                            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    this.inputWidget.addContent(messageTextField);

    // Sound ID
    ComponentWidget labelSoundID = ComponentWidget.i18n("cu.gui.chatListener.manage.sound_id");
    labelSoundID.addId("label-text");
    this.inputWidget.addContent(labelSoundID);


    soundIDTextField.setText(chatListenerWidget.getChatListener().getSoundId());
    soundIDTextField.updateListener(newValue -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    this.inputWidget.addContent(soundIDTextField);

    // Delay
    ComponentWidget labelDelay = ComponentWidget.i18n("cu.gui.chatListener.manage.delay");
    labelDelay.addId("label-text");
    this.inputWidget.addContent(labelDelay);


    TextFieldWidget delayTextField = new TextFieldWidget();
    delayTextField.setText(String.valueOf(chatListenerWidget.getChatListener().getDelay()));
    delayTextField.validator(newValue -> this.isInt.matcher(newValue).matches());
    this.inputWidget.addContent(delayTextField);


    // Boolean options

    HorizontalListWidget chatCommandCheckBoxList = new HorizontalListWidget();
    chatCommandCheckBoxList.addId("checkbox-list");

    // Chat?
    DivWidget chatDiv = new DivWidget();
    chatDiv.addId("checkbox-div");

    ComponentWidget chatText = ComponentWidget.i18n("cu.gui.chatListener.manage.chat");
    chatText.addId("checkbox-name");
    chatDiv.addChild(chatText);


    chatCheckBox.addId("checkbox-item");
    chatCheckBox.setState(chatListenerWidget.getChatListener().isChat() ? State.CHECKED : State.UNCHECKED);
    chatCheckBox.setActionListener(() -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    chatDiv.addChild(chatCheckBox);
    chatCommandCheckBoxList.addEntry(chatDiv);

    // Command?
    DivWidget commandDiv = new DivWidget();
    commandDiv.addId("checkbox-div");

    ComponentWidget commandText = ComponentWidget.i18n("cu.gui.chatListener.manage.command");
    commandText.addId("checkbox-name");
    commandDiv.addChild(commandText);


    commandCheckBox.addId("checkbox-item");
    commandCheckBox.setState(chatListenerWidget.getChatListener().isCommand() ? State.CHECKED : State.UNCHECKED);
    commandCheckBox.setActionListener(() -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    commandDiv.addChild(commandCheckBox);
    chatCommandCheckBoxList.addEntry(commandDiv);

    this.inputWidget.addContent(chatCommandCheckBoxList);

    // Sound? Enabled?
    HorizontalListWidget soundEnabledCheckBoxList = new HorizontalListWidget();
    soundEnabledCheckBoxList.addId("checkbox-list");

    // Sound?
    DivWidget soundDiv = new DivWidget();
    soundDiv.addId("checkbox-div");

    ComponentWidget soundText = ComponentWidget.i18n("cu.gui.chatListener.manage.sound");
    soundText.addId("checkbox-name");
    soundDiv.addChild(soundText);


    soundCheckBox.addId("checkbox-item");
    soundCheckBox.setState(chatListenerWidget.getChatListener().isSound() ? State.CHECKED : State.UNCHECKED);
    soundCheckBox.setActionListener(() -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    soundDiv.addChild(soundCheckBox);
    soundEnabledCheckBoxList.addEntry(soundDiv);

    // Enabled?
    DivWidget enabledDiv = new DivWidget();
    enabledDiv.addId("checkbox-div");

    ComponentWidget enabledText = ComponentWidget.i18n("cu.gui.chatListener.manage.enabled");
    enabledText.addId("checkbox-name");
    enabledDiv.addChild(enabledText);

    CheckBoxWidget enabledCheckBox = new CheckBoxWidget();
    enabledCheckBox.addId("checkbox-item");
    enabledCheckBox.setState(chatListenerWidget.getChatListener().isEnabled() ? State.CHECKED : State.UNCHECKED);
    enabledCheckBox.setActionListener(() -> doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
            chatCheckBox, commandCheckBox, soundCheckBox)
    ));
    enabledDiv.addChild(enabledCheckBox);
    soundEnabledCheckBoxList.addEntry(enabledDiv);

    this.inputWidget.addContent(soundEnabledCheckBoxList);

    // Confirm buttons
    HorizontalListWidget buttonList = new HorizontalListWidget();
    buttonList.addId("edit-button-menu");


    doneButton.setEnabled(
        validateDoneButton(regexTextField, messageTextField, soundIDTextField,
            chatCheckBox, commandCheckBox, soundCheckBox)
    );
    doneButton.setPressable(() -> {
      this.addon.configuration().getChatListeners().remove(chatListenerWidget.getChatListener().getID());
      ChatListener chatListener = chatListenerWidget.getChatListener();
      chatListener.setEnabled(enabledCheckBox.state() == State.CHECKED);
      chatListener.setRegex(regexTextField.getText());
      chatListener.setMsg(messageTextField.getText());
      chatListener.setDelay(Integer.parseInt(delayTextField.getText()));
      chatListener.setSoundId(soundIDTextField.getText());
      chatListener.setChat(chatCheckBox.state() == State.CHECKED);
      chatListener.setCommand(commandCheckBox.state() == State.CHECKED);
      chatListener.setSound(soundCheckBox.state() == State.CHECKED);

      this.addon.configuration().getChatListeners().put(chatListener.getID(), chatListener);
      this.addon.populateHasMap();

      this.chatListenerWidgets.remove(chatListenerWidget);
      this.chatListenerWidgets.add(new ChatListenerWidget(chatListener));
      this.setAction(null);
    });

    buttonList.addEntry(doneButton);

    buttonList.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    this.inputWidget.addContent(buttonList);

    inputContainer.addChild(this.inputWidget);
    return inputContainer;
  }

  private boolean validateDoneButton(
      TextFieldWidget regexTextField, TextFieldWidget messageTextField, TextFieldWidget soundIDTextField,
      CheckBoxWidget chatCheckBox, CheckBoxWidget commandCheckBox, CheckBoxWidget soundCheckBox) {

    if (regexTextField.getText().trim().isEmpty()) {
      return false;
    }

    if (chatCheckBox.state() == State.CHECKED || commandCheckBox.state() == State.CHECKED) {
      if (messageTextField.getText().trim().isEmpty()) {
        return false;
      }
    }

    if (soundCheckBox.state() == State.CHECKED) {
      if (soundIDTextField.getText().trim().isEmpty()) {
        return false;
      }
    }

    try {
      Pattern.compile(regexTextField.getText());
    } catch (PatternSyntaxException e) {
      return false;
    }

    return true;
  }

  private void setAction(Action action) {
    this.action = action;
    this.reload();
  }

  @Override
  public <T extends LabyScreen> @Nullable T renew() {
    return null;
  }

  private enum Action {
    ADD, EDIT, REMOVE
  }
}
