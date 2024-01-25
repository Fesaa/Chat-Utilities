package art.ameliah.laby.addons.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class SoundConfig extends Config {

  @ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);
  @TextFieldSetting
  private final ConfigProperty<String> soundId = new ConfigProperty<>("");

  @SliderSetting(min=0, max = 100)
  private final ConfigProperty<Integer> volume = new ConfigProperty<>(100);

  @SliderSetting(min=0.5f, max=2, steps = 0.1F)
  private final ConfigProperty<Float> pitch = new ConfigProperty<>(1F);

  public ConfigProperty<Boolean> getEnabled() {
    return enabled;
  }

  public ConfigProperty<String> getSoundId() {
    return soundId;
  }

  public ConfigProperty<Float> getPitch() {
    return pitch;
  }

  public ConfigProperty<Integer> getVolume() {
    return volume;
  }

  public SoundConfig(boolean enabled, String soundId, int volume, float pitch) {
    this.enabled.set(enabled);
    this.soundId.set(soundId);
    this.volume.set(volume);
    this.pitch.set(pitch);
  }

  public SoundConfig copy() {
    return new SoundConfig(this.enabled.get(), this.soundId.get(), this.volume.get(), this.pitch.get());
  }

  public SoundConfig() {
  }


}
