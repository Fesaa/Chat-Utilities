package org.chatutilities.core.gui.activity;

import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.Widget;

public abstract class ChatInputTabActivity<T extends Widget> extends Activity {
  protected T contentWidget;

  public ChatInputTabActivity() {
  }

  public boolean isHovered() {
    return this.document.isHovered() && this.contentWidget != null && this.contentWidget.isHovered();
  }
}

