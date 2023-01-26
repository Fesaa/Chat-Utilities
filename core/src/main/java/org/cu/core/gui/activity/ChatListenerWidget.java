package org.cu.core.gui.activity;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.resources.ResourceLocation;
import org.cu.core.imp.ChatListener;
public class ChatListenerWidget extends SimpleWidget {

  private ChatListener chatListener;

  public ChatListenerWidget(ChatListener chatListener) {
    this.chatListener = chatListener;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    if (this.chatListener.isEnabled()) {
      this.removeId("disabled");
    } else {
      this.addId("disabled");
    }

    ResourceLocation resourceLocation = ResourceLocation.create("cu", "sprites.png");
    IconWidget iconWidget = new IconWidget(Icon.sprite16(resourceLocation, 1, 0));
    iconWidget.addId("avatar");
    this.addChild(iconWidget);

    ComponentWidget textWidget = ComponentWidget.component(Component.text(this.chatListener.getRegex()));
    textWidget.addId("text");
    this.addChild(textWidget);

    ComponentWidget messageWidget = ComponentWidget.component(Component.text(this.chatListener.getMsg()));
    messageWidget.addId("message");
    this.addChild(messageWidget);
  }

  public ChatListener getChatListener() {
    return this.chatListener;
  }

  public void setChatListener(ChatListener chatListener) {
    this.chatListener = chatListener;
  }

}
