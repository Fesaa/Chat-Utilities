package org.cu.core;

import com.google.inject.Singleton;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.models.addon.annotation.AddonListener;
import org.cu.core.config.MainConfig;
import org.cu.core.imp.ChatListener;
import org.cu.core.listeners.ChatMessageSendEventListener;
import org.cu.core.listeners.ChatReceiveEventListener;
import java.util.HashMap;
import java.util.regex.Pattern;

@Singleton
@AddonListener
public class CU extends LabyAddon<MainConfig> {

  private final HashMap<Integer, Pattern> patternHashMap = new HashMap<>();

  @Override
  protected void enable() {
    this.registerSettingCategory();
    this.populateHasMap();

    this.registerListener(ChatReceiveEventListener.class);
    this.registerListener(ChatMessageSendEventListener.class);
  }

  @Override
  protected Class<MainConfig> configurationClass() {
    return MainConfig.class;
  }

  public void populateHasMap() {
    ClientPlayer p = this.labyAPI().minecraft().clientPlayer();
    if (p == null) {
      return;
    }
    String name = p.getName();
    for (ChatListener chatListener : this.configuration().getChatListeners().values()) {
      this.patternHashMap.put(chatListener.getID(),
          Pattern.compile(chatListener.getRegex().replace("&player", name)));
    }
  }

  public HashMap<Integer, Pattern> getPatternHashMap() {
    return this.patternHashMap;
  }
}
