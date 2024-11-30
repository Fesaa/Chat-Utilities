package art.ameliah.laby.addons.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.annotation.ShowSettingInParent;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import java.util.HashMap;
import java.util.Map;

public class AdvancedCooldown extends Config {

  @Exclude
  private long lastUsage = 0;

  @Exclude
  private Map<String, Long> groupUsage = new HashMap<>();

  public AdvancedCooldown() {
    group.visibilitySupplier(() -> this.cooldownType.get() == CooldownType.PARTIAL);
    groupUsage.clear();
    lastUsage = 0;
  }

  @SwitchSetting
  @ShowSettingInParent
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);

  @SliderSetting(min = 0, max = 10, steps = 0.1F)
  private ConfigProperty<Float> cooldown = new ConfigProperty<>(0F);

  @DropdownSetting
  private ConfigProperty<CooldownType> cooldownType = new ConfigProperty<>(CooldownType.GLOBAL);

  @SliderSetting(min = 1, max = 10, steps = 1)
  private ConfigProperty<Integer> group = new ConfigProperty<>(0);

  public boolean isEnabled() {
    return this.enabled.get();
  }

  public float getCooldown() {
    return this.cooldown.get();
  }

  public CooldownType getCooldownType() {
    return this.cooldownType.get();
  }

  public ConfigProperty<Integer> getGroup() {
    return this.group;
  }

  public long getLastUsage() {
    return this.lastUsage;
  }

  public void setLastUsage(long lastUsage) {
    this.lastUsage = lastUsage;
  }

  public Map<String, Long> getGroupUsage() {
    return this.groupUsage;
  }

  public enum CooldownType {
    GLOBAL,
    PARTIAL
  }


}
