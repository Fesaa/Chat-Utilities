package org.chatutilities.core.gui.activity;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.ResourceLocation;
import org.chatutilities.core.imp.TextReplacement;

@AutoWidget
public class TextReplacementWidget extends SimpleWidget {

  private TextReplacement textReplacement;

  public TextReplacementWidget(TextReplacement textReplacement) {
    this.textReplacement = textReplacement;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    if (this.textReplacement.isEnabled()) {
      this.removeId("disabled");
    } else {
      this.addId("disabled");
    }

    ResourceLocation resourceLocation = ResourceLocation.create("chatutilities", "sprites.png");
    IconWidget iconWidget = new IconWidget(Icon.sprite16(resourceLocation, 0, 0));
    iconWidget.addId("avatar");
    this.addChild(iconWidget);

    ComponentWidget textWidget = ComponentWidget.component(Component.text(this.textReplacement.getText()));
    textWidget.addId("text");
    this.addChild(textWidget);

    ComponentWidget messageWidget = ComponentWidget.component(Component.text(this.textReplacement.getMessage()));
    messageWidget.addId("message");
    this.addChild(messageWidget);
  }

  public TextReplacement getTextReplacement() {
    return this.textReplacement;
  }

  public void setTextReplacement(TextReplacement textReplacement) {
    this.textReplacement = textReplacement;
  }
}
