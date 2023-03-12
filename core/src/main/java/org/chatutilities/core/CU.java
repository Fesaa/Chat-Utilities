package org.chatutilities.core;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.widget.widgets.activity.chat.ChatButtonWidget;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.chatutilities.core.config.MainConfig;
import org.chatutilities.core.gui.activity.ChatListenerChatActivity;
import org.chatutilities.core.gui.activity.TextReplacementChatActivity;
import org.chatutilities.core.listeners.ChatMessageSendEventListener;
import org.chatutilities.core.listeners.ChatReceiveEventListener;
import org.chatutilities.core.listeners.ConfigurationSaveEventListener;

@AddonMain
public class CU extends LabyAddon<MainConfig> {
  private static CU instance;

  public CU() {
    instance = this;
  }

  public static CU get() {
    return instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();

    this.registerListener(new ChatReceiveEventListener(this));
    this.registerListener(new ChatMessageSendEventListener(this));
    this.registerListener(new ConfigurationSaveEventListener(this));

    if (this.configuration().textReplacement().get()) {
      this.labyAPI().chatProvider().chatInputService().register(CU.getTextReplacementWidget());
    }
    if (this.configuration().getChatListenerSubConfig().isEnabled()) {
    this.labyAPI().chatProvider().chatInputService().register(CU.getChatListenerWidget());
    }
  }

  @Override
  protected Class<MainConfig> configurationClass() {
    return MainConfig.class;
  }

  public static ChatButtonWidget getTextReplacementWidget() {
    ResourceLocation resourceLocation = ResourceLocation.create("chatutilities", "sprites.png");
    return ChatButtonWidget.icon("textreplacement",
        Icon.sprite16(resourceLocation, 0, 0),
        TextReplacementChatActivity::new);
  }

  public static ChatButtonWidget getChatListenerWidget() {
    ResourceLocation resourceLocation = ResourceLocation.create("chatutilities", "sprites.png");
    return ChatButtonWidget.icon("chatlisteners",
        Icon.sprite16(resourceLocation, 1, 0),
        ChatListenerChatActivity::new);
  }

}
