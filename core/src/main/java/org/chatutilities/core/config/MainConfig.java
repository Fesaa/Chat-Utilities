package org.chatutilities.core.config;

import java.util.ArrayList;
import java.util.List;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import org.chatutilities.core.config.impl.ChatListenerEntry;
import org.chatutilities.core.config.impl.TextReplacementEntry;

@ConfigName("settings")
public class MainConfig extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> copy = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> copyButtonText = new ConfigProperty<>("Copy");

  private final ChatListenerSubConfig chatListener = new ChatListenerSubConfig();

  @SwitchSetting
  private final ConfigProperty<Boolean> textReplacement = new ConfigProperty<>(true);

  @Exclude
  private final ConfigProperty<List<TextReplacementEntry>> textReplacements = new ConfigProperty<>(new ArrayList<>());

  @Exclude
  private final ConfigProperty<List<ChatListenerEntry>> chatListeners = new ConfigProperty<>(new ArrayList<>());

  public ConfigProperty<List<TextReplacementEntry>> getTextReplacements() {
    return textReplacements;
  }

  public ConfigProperty<List<ChatListenerEntry>> getChatListeners() {
    return this.chatListeners;
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

  public ConfigProperty<Boolean> getCopy() {
    return copy;
  }

  public ConfigProperty<String> getCopyText() {
    return copyButtonText;
  }
}
