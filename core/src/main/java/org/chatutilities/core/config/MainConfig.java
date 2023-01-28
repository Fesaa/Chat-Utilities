package org.chatutilities.core.config;

import java.util.HashMap;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.AddonActivityWidget.AddonActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;
import org.chatutilities.core.CU;
import org.chatutilities.core.gui.activity.ChatListenerActivity;
import org.chatutilities.core.gui.activity.TextReplacementActivity;
import org.chatutilities.core.imp.ChatListener;
import org.chatutilities.core.imp.TextReplacement;

@SuppressWarnings("FieldMayBeFinal")
@ConfigName("settings")
public class MainConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ChatListenerSubConfig chatListener = new ChatListenerSubConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> textReplacement = new ConfigProperty<>(true);

  private ConfigProperty<HashMap<Integer, TextReplacement>> textReplacements = new ConfigProperty<>(new HashMap<>());
  private ConfigProperty<HashMap<Integer, ChatListener>> chatListeners = new ConfigProperty<>(new HashMap<>());

  public HashMap<Integer, TextReplacement> getTextReplacements() {
    return this.textReplacements.get();
  }
  public HashMap<Integer, ChatListener> getChatListeners() {
    return this.chatListeners.get();
  }

  @MethodOrder(after = "textReplacement")
  @AddonActivitySetting
  public Activity openTextReplacementActivity() {
    return new TextReplacementActivity(CU.get());
  }

  @MethodOrder(after = "chatListener")
  @AddonActivitySetting
  public Activity openChatListenerActivity() {
    return new ChatListenerActivity(CU.get());
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }
  public ChatListenerSubConfig getChatListenerSubConfig() {
    return this.chatListener;
  }
  public ConfigProperty<Boolean> textReplacement() {
    return this.textReplacement;
  }

}
