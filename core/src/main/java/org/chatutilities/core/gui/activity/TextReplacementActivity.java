package org.chatutilities.core.gui.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import org.chatutilities.core.CU;
import org.chatutilities.core.imp.TextReplacement;
import org.jetbrains.annotations.Nullable;

@AutoActivity
@Link("manage_tr.lss")
@Link("overview_tr.lss")
public class TextReplacementActivity extends Activity {

  private final CU addon;

  private final VerticalListWidget<TextReplacementWidget> textReplacementList;

  private final List<TextReplacementWidget> textReplacementWidgets;

  private ButtonWidget removeButton;
  private ButtonWidget editButton;

  private FlexibleContentWidget inputWidget;

  private Action action;

  public TextReplacementActivity(CU addon) {
    this.addon = addon;

    this.textReplacementWidgets = new ArrayList<>();
    addon.configuration().getTextReplacements().forEach((ID, textReplacement) -> this.textReplacementWidgets.add(new TextReplacementWidget(textReplacement)));

    this.textReplacementList = new VerticalListWidget<>();
    this.textReplacementList.addId("text-replacement-list");
    this.textReplacementList.setSelectCallback(textReplacementWidget -> {
        this.editButton.setEnabled(true);
        this.removeButton.setEnabled(true);
    });

    this.textReplacementList.setDoubleClickCallback(textReplacementWidget -> this.setAction(Action.EDIT));
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("text-replacement-container");
    for (TextReplacementWidget textReplacementWidget : this.textReplacementWidgets) {
      this.textReplacementList.addChild(textReplacementWidget);
    }

    container.addFlexibleContent(new ScrollWidget(this.textReplacementList));

    TextReplacementWidget selectedTextReplacement = this.textReplacementList.session().getSelectedEntry();
    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("overview-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.add", () -> this.setAction(Action.ADD)));

    this.editButton = ButtonWidget.i18n("labymod.ui.button.edit", () -> this.setAction(Action.EDIT));
    this.editButton.setEnabled(Objects.nonNull(selectedTextReplacement));
    menu.addEntry(this.editButton);

    this.removeButton = ButtonWidget.i18n("labymod.ui.button.remove", () -> this.setAction(Action.REMOVE));
    this.removeButton.setEnabled(Objects.nonNull(selectedTextReplacement));
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
        TextReplacementWidget newTextReplacement = new TextReplacementWidget(TextReplacement.createDefault());
        overlayWidget = this.initializeManageContainer(newTextReplacement);
        break;
      case EDIT:
        overlayWidget = this.initializeManageContainer(selectedTextReplacement);
        break;
      case REMOVE:
        overlayWidget = this.initializeRemoveContainer(selectedTextReplacement);
        break;
    }

    manageContainer.addChild(overlayWidget);
    this.document().addChild(manageContainer);
  }

  private FlexibleContentWidget initializeRemoveContainer(TextReplacementWidget textReplacementWidget) {
    this.inputWidget = new FlexibleContentWidget();
    this.inputWidget.addId("remove-container");

    ComponentWidget confirmationWidget = ComponentWidget.i18n("cu.gui.textReplacement.manage.remove.title");
    confirmationWidget.addId("remove-confirmation");
    this.inputWidget.addContent(confirmationWidget);

    TextReplacementWidget previewWidget = new TextReplacementWidget(textReplacementWidget.getTextReplacement());
    previewWidget.addId("remove-preview");
    this.inputWidget.addContent(previewWidget);

    HorizontalListWidget menu = new HorizontalListWidget();
    menu.addId("remove-button-menu");

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.remove", () -> {
      this.addon.configuration().getTextReplacements().remove(textReplacementWidget.getTextReplacement().getID());
      this.textReplacementWidgets.remove(textReplacementWidget);
      this.textReplacementList.session().setSelectedEntry(null);
      this.setAction(null);
    }));

    menu.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    this.inputWidget.addContent(menu);

    return this.inputWidget;
  }

  private DivWidget initializeManageContainer(TextReplacementWidget textReplacementWidget) {
    ButtonWidget doneButton = ButtonWidget.i18n("labymod.ui.button.done");
    TextFieldWidget messageTextField = new TextFieldWidget();


    DivWidget inputContainer = new DivWidget();
    inputContainer.addId("input-container");

    this.inputWidget = new FlexibleContentWidget();
    this.inputWidget.addId("input-list");

    // Text to replace
    ComponentWidget labelText = ComponentWidget.i18n("cu.gui.textReplacement.manage.text");
    labelText.addId("label-text");
    this.inputWidget.addContent(labelText);

    TextFieldWidget textTextField = new TextFieldWidget();
    textTextField.setText(textReplacementWidget.getTextReplacement().getText());
    textTextField.updateListener(newValue -> doneButton.setEnabled(
        !textTextField.getText().trim().isEmpty()
        && !messageTextField.getText().trim().isEmpty()
      ));
    this.inputWidget.addContent(textTextField);

    // Text to replace with
    ComponentWidget labelMessage = ComponentWidget.i18n("cu.gui.textReplacement.manage.message");
    labelMessage.addId("label-text");
    this.inputWidget.addContent(labelMessage);

    messageTextField.setText(textReplacementWidget.getTextReplacement().getMessage());
    messageTextField.updateListener(newValue -> doneButton.setEnabled(
        !textTextField.getText().trim().isEmpty()
        && !messageTextField.getText().trim().isEmpty()
    ));
    this.inputWidget.addContent(messageTextField);

    // Enabled?
    HorizontalListWidget checkBoxList = new HorizontalListWidget();
    checkBoxList.addId("checkbox-list");

    DivWidget enabledDiv = new DivWidget();
    enabledDiv.addId("checkbox-div");

    ComponentWidget enabledText = ComponentWidget.i18n("cu.gui.textReplacement.manage.enabled");
    enabledText.addId("checkbox-name");
    enabledDiv.addChild(enabledText);

    CheckBoxWidget enabledWidget = new CheckBoxWidget();
    enabledWidget.addId("checkbox-item");
    enabledWidget.setState(textReplacementWidget.getTextReplacement().isEnabled() ? State.CHECKED : State.UNCHECKED);
    enabledDiv.addChild(enabledWidget);
    checkBoxList.addEntry(enabledDiv);

    this.inputWidget.addContent(checkBoxList);


    // Confirm buttons
    HorizontalListWidget buttonList = new HorizontalListWidget();
    buttonList.addId("edit-button-menu");


    doneButton.setEnabled(
        !textTextField.getText().trim().isEmpty()
        && !messageTextField.getText().trim().isEmpty());
    doneButton.setPressable(() -> {
      this.addon.configuration().getTextReplacements().remove(textReplacementWidget.getTextReplacement().getID());
      TextReplacement textReplacement = textReplacementWidget.getTextReplacement();
      textReplacement.setEnabled(enabledWidget.state() == State.CHECKED);
      textReplacement.setText(textTextField.getText());
      textReplacement.setMessage(messageTextField.getText());

      this.addon.configuration().getTextReplacements().put(textReplacement.getID(), textReplacement);
      this.textReplacementWidgets.remove(textReplacementWidget);
      this.textReplacementWidgets.add(new TextReplacementWidget(textReplacement));
      
      this.setAction(null);
    });

    buttonList.addEntry(doneButton);

    buttonList.addEntry(ButtonWidget.i18n("labymod.ui.button.cancel", () -> this.setAction(null)));
    inputContainer.addChild(this.inputWidget);
    this.inputWidget.addContent(buttonList);
    return inputContainer;
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
