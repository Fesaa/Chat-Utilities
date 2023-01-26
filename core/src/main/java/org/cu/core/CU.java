package org.cu.core;

import java.util.HashMap;
import java.util.regex.Pattern;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.cu.core.config.MainConfig;
import org.cu.core.imp.ChatListener;
import org.cu.core.listeners.ChatMessageSendEventListener;
import org.cu.core.listeners.ChatReceiveEventListener;

@AddonMain
public class CU extends LabyAddon<MainConfig> {

  private final HashMap<Integer, Pattern> patternHashMap = new HashMap<>();

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
    this.populateHasMap();

    this.registerListener(new ChatReceiveEventListener(this));
    this.registerListener(new ChatMessageSendEventListener(this));
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
