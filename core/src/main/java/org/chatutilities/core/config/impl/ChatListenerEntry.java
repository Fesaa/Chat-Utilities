package org.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatListenerEntry extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> displayName = new ConfigProperty<>("");

  @DropdownSetting
  private final ConfigProperty<MatchType> matchType = new ConfigProperty<>(MatchType.EQUALS);

  @DropdownSetting
  private final ConfigProperty<ReplyType> replyType = new ConfigProperty<>(ReplyType.CHAT);

  @TextFieldSetting
  private final ConfigProperty<String> regex = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> text = new ConfigProperty<>("");

  private SoundConfig soundConfig = new SoundConfig();

  @SliderSetting(min = 0, max = 10)
  private final ConfigProperty<Integer> delay = new ConfigProperty<>(0);

  private ServerConfig serverConfig = new ServerConfig();

  public ChatListenerEntry() {
  }

  public ChatListenerEntry(
      boolean enabled,
      String displayName,
      MatchType matchType,
      ReplyType replyType,
      String regex,
      String text,
      SoundConfig soundConfig,
      int delay,
      ServerConfig serverConfig
  ) {
    this.enabled.set(enabled);
    this.displayName.set(displayName);
    this.matchType.set(matchType);
    this.replyType.set(replyType);
    this.regex.set(regex);
    this.text.set(text);
    this.soundConfig = soundConfig;
    this.delay.set(delay);
    this.serverConfig = serverConfig;
  }

  public ConfigProperty<Boolean> getEnabled() {
    return enabled;
  }

  public ConfigProperty<MatchType> getMatchType() {
    return matchType;
  }

  public ConfigProperty<ReplyType> getReplyType() {
    return replyType;
  }

  public ConfigProperty<String> getText() {
    return text;
  }

  public ConfigProperty<Integer> getDelay() {
    return delay;
  }

  public ConfigProperty<String> getDisplayName() {
    return displayName;
  }

  public ConfigProperty<String> getRegex() {
    return regex;
  }

  public SoundConfig getSoundConfig() {
    return soundConfig;
  }


  public ServerConfig getServerConfig() {
    return serverConfig;
  }

  public ChatListenerEntry copy() {
    return new ChatListenerEntry(
        this.enabled.get(),
        this.displayName.get(),
        this.matchType.get(),
        this.replyType.get(),
        this.regex.get(),
        this.text.get(),
        this.soundConfig,
        this.delay.get(),
        this.serverConfig
    );
  }

  public enum MatchType {
    EQUALS,
    CONTAINS,
    REGEX
  }

  public enum ReplyType {
    CHAT,
    COMMAND,
    SOUND
  }

}
