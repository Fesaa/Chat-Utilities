package org.cu.core;

import net.kyori.adventure.util.HSVLike;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.AddonActivityWidget.AddonActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.inject.LabyGuice;
import net.labymod.api.util.MethodOrder;
import org.cu.core.gui.activity.ChatListenerActivity;
import org.cu.core.gui.activity.TextReplacementActivity;
import org.cu.core.imp.ChatListener;
import org.cu.core.imp.TextReplacement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("FieldMayBeFinal")
@ConfigName("settings")
public class CUConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> chatListener = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> textReplacement = new ConfigProperty<>(true);

  private HashMap<Integer, TextReplacement> textReplacements = new HashMap<>();
  private HashMap<Integer, ChatListener> chatListeners = new HashMap<>();

  public HashMap<Integer, TextReplacement> getTextReplacements() {
    return this.textReplacements;
  }
  public HashMap<Integer, ChatListener> getChatListeners() {
    return this.chatListeners;
  }

  @MethodOrder(after = "textReplacement")
  @AddonActivitySetting
  public Activity openTextReplacementActivity() {
    return LabyGuice.getInstance(TextReplacementActivity.class);
  }

  @MethodOrder(after = "chatListener")
  @AddonActivitySetting
  public Activity openChatListenerActivity() {
    return LabyGuice.getInstance(ChatListenerActivity.class);
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }
  public ConfigProperty<Boolean> chatListeners() {
    return this.chatListener;
  }
  public ConfigProperty<Boolean> textReplacement() {
    return this.textReplacement;
  }

}
