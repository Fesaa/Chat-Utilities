package art.ameliah.laby.addons.chatutilities.core.config.impl;


import art.ameliah.laby.addons.chatutilities.core.CU;
import art.ameliah.laby.addons.cubepanion.core.Cubepanion;
import art.ameliah.laby.addons.cubepanion.core.managers.CubepanionManager;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class CubeCraftSupport extends Config {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> inParty = new ConfigProperty<>(false);

  @SwitchSetting
  private final ConfigProperty<Boolean> inGame = new ConfigProperty<>(false);

  public CubeCraftSupport() {
  }

  public CubeCraftSupport(boolean enabled, boolean inParty, boolean inGame) {
    this.enabled.set(enabled);
    this.inParty.set(inParty);
    this.inGame.set(inGame);
  }

  public CubeCraftSupport copy() {
    return new CubeCraftSupport(
        this.enabled.get(),
        this.inParty.get(),
        this.inGame.get()
    );
  }

  public boolean canUse() {
    if (!enabled.get()) {
      return true;
    }

    if (!CU.get().isCubepanionSupported()) {
      return true;
    }

    CubepanionManager m = CU.get().getCubepanion().getManager();
    if (inParty.get() && !m.getPartyManager().isInParty()) {
      return false;
    }

    if (inGame.get() && m.isInPreLobby()) {
      return false;
    }

    return true;
  }


}
