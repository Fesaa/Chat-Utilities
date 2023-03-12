package org.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatListenerEntry extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> displayName = new ConfigProperty<>("");

  @SwitchSetting
  private final ConfigProperty<Boolean> useRegex = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> regex = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> text = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> soundId = new ConfigProperty<>("");

  @SliderSetting(min = 0, max = 10)
  private final ConfigProperty<Integer> delay = new ConfigProperty<>(0);

  @SwitchSetting
  private final ConfigProperty<Boolean> chat = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> command = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> sound = new ConfigProperty<>(false);

  private ServerConfig serverConfig = new ServerConfig();

  public ChatListenerEntry() {
  }

  public ChatListenerEntry(
      boolean enabled,
      String displayName,
      boolean useRegex,
      String regex,
      String text,
      String soundId,
      int delay,
      boolean chat,
      boolean command,
      boolean sound,
      ServerConfig serverConfig
  ) {
    this.enabled.set(enabled);
    this.displayName.set(displayName);
    this.useRegex.set(useRegex);
    this.regex.set(regex);
    this.text.set(text);
    this.soundId.set(soundId);
    this.delay.set(delay);
    this.chat.set(chat);
    this.command.set(command);
    this.sound.set(sound);
    this.serverConfig = serverConfig;
  }

  public ConfigProperty<Boolean> getEnabled() {
    return enabled;
  }

  public ConfigProperty<String> getText() {
    return text;
  }

  public ConfigProperty<Boolean> getChat() {
    return chat;
  }

  public ConfigProperty<Boolean> getCommand() {
    return command;
  }

  public ConfigProperty<Boolean> getSound() {
    return sound;
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

  public ConfigProperty<String> getSoundId() {
    return soundId;
  }

  public ConfigProperty<Boolean> getUseRegex() {
    return useRegex;
  }

  public ServerConfig getServerConfig() {
    return serverConfig;
  }

  public ChatListenerEntry copy() {
    return new ChatListenerEntry(
        this.enabled.get(),
        this.displayName.get(),
        this.useRegex.get(),
        this.regex.get(),
        this.text.get(),
        this.soundId.get(),
        this.delay.get(),
        this.chat.get(),
        this.command.get(),
        this.sound.get(),
        this.serverConfig
    );
  }
}
