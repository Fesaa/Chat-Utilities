package art.ameliah.laby.addons.chatutilities.core.gui.hud.widget;


import art.ameliah.laby.addons.chatutilities.core.gui.hud.widget.ChatMessagePreviewWidget.ChatMessagePreviewConfig.DisplayFilter;
import net.labymod.api.Laby;
import net.labymod.api.client.chat.ChatExecutor;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.hud.binding.category.HudWidgetCategory;
import net.labymod.api.client.gui.hud.hudwidget.text.Formatting;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidget;
import net.labymod.api.client.gui.hud.hudwidget.text.TextHudWidgetConfig;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine;
import net.labymod.api.client.gui.hud.hudwidget.text.TextLine.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.dropdown.DropdownWidget.DropdownSetting;
import net.labymod.api.client.render.font.ComponentMapper;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import art.ameliah.laby.addons.chatutilities.core.gui.hud.widget.ChatMessagePreviewWidget.ChatMessagePreviewConfig;

public class ChatMessagePreviewWidget extends TextHudWidget<ChatMessagePreviewConfig> {

  private TextLine line;
  private final Component preview;
  private final ChatExecutor chat;
  private final ComponentMapper componentMapper;


  public ChatMessagePreviewWidget() {
    super("chat-utilities::chat_message_preview", ChatMessagePreviewConfig.class);

    this.bindCategory(HudWidgetCategory.MISCELLANEOUS);
    this.bindDropzones(new ChatMessagePreviewHudWidgetDropzone());

    this.chat = Laby.references().chatExecutor();
    this.componentMapper = Laby.references().componentMapper();
    this.preview = Component.text("This ", NamedTextColor.BLUE)
        .append(Component.text("is ", NamedTextColor.GREEN))
        .append(Component.text("a ", NamedTextColor.RED))
        .append(Component.text("preview!", NamedTextColor.YELLOW))
        .append(Component.text("bla bla bla", Style.builder().decorate(TextDecoration.OBFUSCATED).build()));
  }

  @Override
  public void onTick(boolean inEditor) {
    if (inEditor) {
     this.line.updateAndFlush(this.preview);
     this.line.setState(State.VISIBLE);
     return;
    }

    String msg = this.chat.getChatInputMessage();
    if (msg == null || msg.isEmpty() || msg.startsWith("/")) {
      // Don't render anything if the message is empty or a command
      this.line.setState(State.HIDDEN);
      return;
    }

    String component = this.componentMapper.translateColorCodes(msg);
    boolean equals = component.equals(msg);

    if (this.config.getDisplayFilter().get().equals(DisplayFilter.WHEN_FORMAT) && equals) {
      this.line.setState(State.HIDDEN);
      return;
    }

    this.line.updateAndFlush(Component.text(component));
    this.line.setState(State.VISIBLE);
  }

  @Override
  public void load(ChatMessagePreviewConfig config) {
    super.load(config);

    this.config.formatting().set(Formatting.VALUE_ONLY);
    this.line = super.createLine("Remove me", "");
  }

  public static class ChatMessagePreviewConfig extends TextHudWidgetConfig {

    @DropdownSetting
    private final ConfigProperty<DisplayFilter> displayFilter = new ConfigProperty<>(DisplayFilter.WHEN_FORMAT);

    public ConfigProperty<DisplayFilter> getDisplayFilter() {
      return displayFilter;
    }

    public enum DisplayFilter {
      ALWAYS,
      WHEN_FORMAT;
    }
  }

}
