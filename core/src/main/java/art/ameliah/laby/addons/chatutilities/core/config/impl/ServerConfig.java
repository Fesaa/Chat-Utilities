package art.ameliah.laby.addons.chatutilities.core.config.impl;

import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.client.network.server.ServerData;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ShowSettingInParent;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

public class ServerConfig extends Config {

  @SwitchSetting
  @ShowSettingInParent
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);

  @SwitchSetting
  @SettingRequires("enabled")
  private final ConfigProperty<Boolean> singlePlayer = new ConfigProperty<>(true);

  @TextFieldSetting
  @SettingRequires("enabled")
  private final ConfigProperty<String> address = new ConfigProperty<>("");

  public ServerConfig() {
  }

  public ServerConfig(boolean enabled, String address) {
    this.enabled.set(enabled);
    this.address.set(address);
  }

  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> getSinglePlayer() {
    return this.singlePlayer;
  }

  public ConfigProperty<String> address() {
    return this.address;
  }

  public ServerConfig copy() {
    return new ServerConfig(this.enabled.get(), this.address.get());
  }


  public boolean notAllowedToBeUsed(ServerData data) {
    if (!this.enabled.get()) {
      return false;
    }

    if (Laby.labyAPI().minecraft().isSingleplayer()) {
      return !this.singlePlayer.get();
    }

    if (data == null) {
      return true;
    }

    return !this.address.isDefaultValue() && !this.address.get().equals(data.address().getHost());

  }


}
