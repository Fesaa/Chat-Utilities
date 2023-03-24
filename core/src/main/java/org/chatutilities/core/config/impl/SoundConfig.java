package org.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class SoundConfig extends Config {
  @TextFieldSetting
  private final ConfigProperty<String> soundId = new ConfigProperty<>("");

  @SliderSetting(min=0, max = 100)
  private final ConfigProperty<Integer> volume = new ConfigProperty<>(100);

  @SliderSetting(min=0, max=2, steps = 0.1F)
  private final ConfigProperty<Float> pitch = new ConfigProperty<>(1F);

  public ConfigProperty<String> getSoundId() {
    return soundId;
  }

  public ConfigProperty<Float> getPitch() {
    return pitch;
  }

  public ConfigProperty<Integer> getVolume() {
    return volume;
  }

  public SoundConfig(String soundId, int volume, float pitch) {
    this.soundId.set(soundId);
    this.volume.set(volume);
    this.pitch.set(pitch);
  }

  public SoundConfig copy() {
    return new SoundConfig(this.soundId.get(), this.volume.get(), this.pitch.get());
  }

  public SoundConfig() {
  }


}
