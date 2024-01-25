package art.ameliah.laby.addons.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.network.server.ServerAddress;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class TextReplacementEntry extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @TextFieldSetting
  private final ConfigProperty<String> displayName = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> text = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> message = new ConfigProperty<>("");

  @SwitchSetting
  private final ConfigProperty<Boolean> onReceive = new ConfigProperty<>(false);

  private ServerConfig serverConfig = new ServerConfig();

  public TextReplacementEntry() {
  }

  public TextReplacementEntry(
      boolean enabled,
      String displayName,
      String text,
      String message,
      boolean onReceive,
      ServerConfig serverConfig
  ) {
    this.enabled.set(enabled);
    this.displayName.set(displayName);
    this.text.set(text);
    this.message.set(message);
    this.onReceive.set(onReceive);
    this.serverConfig = serverConfig;
  }

  public ConfigProperty<Boolean> getEnabled() {
    return enabled;
  }

  public ConfigProperty<String> displayName() {
    return this.displayName;
  }

  public ConfigProperty<String> message() {
    return this.message;
  }

  public ConfigProperty<String> getText() {
    return text;
  }

  public ServerConfig serverConfig() {
    return this.serverConfig;
  }

  public ConfigProperty<Boolean> onReceive() {
    return this.onReceive;
  }

  public ServerAddress serverAddress() {
    return this.serverConfig.enabled().get()
        ? ServerAddress.parse(this.serverConfig.address().get())
        : null;
  }

  public void setServerAddress(String serverAddress) {
    this.serverConfig = new ServerConfig(true, serverAddress);
  }

  public TextReplacementEntry copy() {
    return new TextReplacementEntry(
        this.enabled.get(),
        this.displayName.get(),
        this.text.get(),
        this.message.get(),
        this.onReceive.get(),
        this.serverConfig.copy()
    );
  }
}
