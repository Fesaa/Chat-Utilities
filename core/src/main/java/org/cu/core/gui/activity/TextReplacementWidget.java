package org.cu.core.gui.activity;

import net.kyori.adventure.text.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import org.cu.core.imp.TextReplacement;
import java.util.UUID;

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

    IconWidget iconWidget = new IconWidget(Icon.head(UUID.fromString("17d896dd-814a-4cfb-aa44-b935af4dcdeb")));
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
