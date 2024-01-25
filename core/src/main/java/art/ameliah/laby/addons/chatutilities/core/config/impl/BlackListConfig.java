package art.ameliah.laby.addons.chatutilities.core.config.impl;

import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.annotation.ParentSwitch;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class BlackListConfig extends Config {

  @ParentSwitch
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);

  @TextFieldSetting
  private final ConfigProperty<String> wordOne = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> wordTwo = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> wordThree = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> wordFour = new ConfigProperty<>("");

  @TextFieldSetting
  private final ConfigProperty<String> wordFive = new ConfigProperty<>("");

  public BlackListConfig() {
  }

  public BlackListConfig(boolean enabled, String one, String two, String three, String four, String five) {
    this.enabled.set(enabled);
    this.wordOne.set(one);
    this.wordTwo.set(two);
    this.wordThree.set(three);
    this.wordFour.set(four);
    this.wordFive.set(five);
  }

  public boolean isBlockedByBlackList(String msg) {
    return  (
        this.enabled.get() &&
       ((msg.contains(this.wordOne.get()) && !this.wordOne.isDefaultValue())
     || (msg.contains(this.wordTwo.get()) && !this.wordTwo.isDefaultValue())
     || (msg.contains(this.wordThree.get()) && !this.wordThree.isDefaultValue())
     || (msg.contains(this.wordFour.get()) && !this.wordFour.isDefaultValue())
     || (msg.contains(this.wordFive.get()) && !this.wordFive.isDefaultValue()))
    );
  }

}
