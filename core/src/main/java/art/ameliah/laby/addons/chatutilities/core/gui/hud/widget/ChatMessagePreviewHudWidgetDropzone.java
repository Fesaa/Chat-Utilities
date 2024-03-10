package art.ameliah.laby.addons.chatutilities.core.gui.hud.widget;

import net.labymod.api.client.gui.hud.HudWidgetRendererAccessor;
import net.labymod.api.client.gui.hud.binding.dropzone.HudWidgetDropzone;
import net.labymod.api.client.gui.hud.position.HudSize;
import net.labymod.api.client.gui.hud.position.HudWidgetAnchor;

public class ChatMessagePreviewHudWidgetDropzone extends HudWidgetDropzone {

  public ChatMessagePreviewHudWidgetDropzone() {
    super("chat-utilities::chat_message_preview_dropzone");
  }

  @Override
  public float getX(HudWidgetRendererAccessor renderer, HudSize hudSize) {
    return renderer.getArea().getLeft() + 5.0F;
  }

  @Override
  public float getY(HudWidgetRendererAccessor renderer, HudSize hudSize) {
    return renderer.getArea().getBottom() - 40.0F;
  }

  @Override
  public ChatMessagePreviewHudWidgetDropzone copy() {
    return new ChatMessagePreviewHudWidgetDropzone();
  }

  @Override
  public HudWidgetAnchor getAnchor() {
    return HudWidgetAnchor.CENTER_BOTTOM;
  }
}
