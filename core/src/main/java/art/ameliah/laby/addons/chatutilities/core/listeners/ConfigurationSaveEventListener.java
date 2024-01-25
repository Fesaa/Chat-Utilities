package art.ameliah.laby.addons.chatutilities.core.listeners;

import net.labymod.api.client.chat.input.ChatInputRegistry;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.labymod.config.ConfigurationSaveEvent;
import art.ameliah.laby.addons.chatutilities.core.CU;
import art.ameliah.laby.addons.chatutilities.core.config.MainConfig;

public class ConfigurationSaveEventListener {

  private final CU addon;

  public ConfigurationSaveEventListener(CU addon) {this.addon = addon;}

  @Subscribe
  public void onConfigurationSaveEvent(ConfigurationSaveEvent e) {
    MainConfig config = this.addon.configuration();
    ChatInputRegistry chatInputRegistry = this.addon.labyAPI().chatProvider().chatInputService();

    if (!config.enabled().get()) {
      if (chatInputRegistry.getById("chatlisteners") != null) {
        chatInputRegistry.unregister("chatlisteners");
      }
      if (chatInputRegistry.getById("textreplacement") != null) {
        chatInputRegistry.unregister("textreplacement");
      }
      return;
    }

    if (!config.getChatListenerSubConfig().isEnabled()) {
      if (chatInputRegistry.getById("chatlisteners") != null) {
        chatInputRegistry.unregister("chatlisteners");
      }
    } else {
      if (chatInputRegistry.getById("chatlisteners") == null) {
        chatInputRegistry.register(CU.getChatListenerWidget());
      }
    }
    if (!config.textReplacement().get()) {
      if (chatInputRegistry.getById("textreplacement") != null) {
        chatInputRegistry.unregister("textreplacement");
      }
    } else {
      if (chatInputRegistry.getById("textreplacement") == null) {
        chatInputRegistry.register(CU.getTextReplacementWidget());
      }
    }
  }
}
