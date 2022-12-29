package org.cu.core.config;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class ChatListenerSubConfig extends Config {

  @ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> useAntiSpam = new ConfigProperty<>(false);

  @SliderSetting(min=0, max=10)
  private final ConfigProperty<Integer> maxFrequency = new ConfigProperty<>(2);

  @SliderSetting(min=0, max=10)
  private final ConfigProperty<Integer> maxTimeSpan = new ConfigProperty<>(5);

  public boolean isEnabled() {
    return this.enabled.get();
  }

  public ConfigProperty<Boolean> getUseAntiSpam() {
    return this.useAntiSpam;
  }

  public ConfigProperty<Integer> getMaxFrequency() {
    return maxFrequency;
  }

  public ConfigProperty<Integer> getMaxTimeSpan() {
    return maxTimeSpan;
  }
}
