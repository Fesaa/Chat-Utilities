package org.chatutilities.core.gui.activity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.labymod.api.Laby;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.activities.labymod.child.SettingContentActivity;
import net.labymod.api.client.gui.screen.activity.activities.labymod.child.SettingContentActivity.HeaderType;
import net.labymod.api.client.gui.screen.activity.types.chatinput.ChatInputTabSettingActivity;
import net.labymod.api.client.gui.screen.widget.AbstractWidget;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.ScreenRendererWidget;
import org.chatutilities.core.CU;
import org.chatutilities.core.config.MainConfig;
import org.chatutilities.core.config.impl.ChatListenerEntry;
import org.chatutilities.core.config.impl.ChatListenerEntry.MatchType;
import org.jetbrains.annotations.NotNull;

@Link("chatinput/entry.lss")
@AutoActivity
public class ChatListenerChatActivity extends ChatInputTabSettingActivity<FlexibleContentWidget> {

  private ChatListenerEntry original;
  private ChatListenerEntry editing;
  private MainConfig config;

  public ChatListenerChatActivity() {
    this.config = CU.get().configuration();
  }

  public void initialize(Parent parent) {
    super.initialize(parent);
    this.document.addChild(this.createWindow());
    this.config = CU.get().configuration();
  }

  private @NotNull AbstractWidget<?> createWindow() {
    this.contentWidget = new FlexibleContentWidget();
    this.contentWidget.addId("window");
    DivWidget titleBar = new DivWidget();
    titleBar.addId("title-bar");
    ComponentWidget title = ComponentWidget.component(Component.translatable("chatutilities.chatInput.chatlisteners.name"));
    title.addId("title");
    titleBar.addChild(title);
    DivWidget buttonWrapper = new DivWidget();
    buttonWrapper.addId("button");
    IconWidget iconWidget;
    if (this.editing == null) {
      iconWidget = new IconWidget(SpriteCommon.SMALL_ADD);
      iconWidget.addId("icon");
      iconWidget.setHoverComponent(Component.translatable("labymod.ui.button.add"));
      buttonWrapper.addChild(iconWidget);
      buttonWrapper.setPressable(() -> {
        this.original = null;
        this.editing = new ChatListenerEntry();
        this.reload();
      });
    } else {
      iconWidget = new IconWidget(SpriteCommon.SMALL_CHECKED);
      iconWidget.addId("icon");
      iconWidget.setHoverComponent(Component.translatable("labymod.ui.button.save"));
      buttonWrapper.addChild(iconWidget);
      buttonWrapper.setPressable(() -> {
        if (!this.editing.getDisplayName().get().isEmpty()) {

          if (this.editing.getMatchType().get().equals(MatchType.REGEX)) {
            try {
              Pattern.compile(this.editing.getRegex().get());
            } catch (PatternSyntaxException e) {
              Laby.labyAPI().minecraft().chatExecutor().displayClientMessage(
                  Component.text("[", NamedTextColor.WHITE)
                      .append(Component.text("CU", NamedTextColor.GREEN))
                      .append(Component.text("]", NamedTextColor.WHITE))
                      .append(Component.translatable("chatutilities.errors.invalidRegex", NamedTextColor.RED))
              );
              this.editing.getEnabled().set(false);
            }
          }
          this.config.getChatListeners().get().remove(this.original == null ? this.editing : this.original);
          this.config.getChatListeners().get().add(this.editing);
          CU.get().saveConfiguration();
        }

        this.original = null;
        this.editing = null;
        this.reload();
      });
    }

    titleBar.addChild(buttonWrapper);
    this.contentWidget.addContent(titleBar);
    DivWidget contentWrapper = new DivWidget();
    contentWrapper.addId("content-wrapper");
    if (this.editing == null) {
      VerticalListWidget<Widget> list = new VerticalListWidget<>();
      list.addId("entries");

      ChatListenerEntry[] chatListenerEntries = this.config.getChatListeners().get().toArray(new ChatListenerEntry[0]);
      Arrays.sort(chatListenerEntries, new SortByDisplayName());
      for (ChatListenerEntry entry : chatListenerEntries) {
        list.addChild(this.createEntry(entry));
      }
      ScrollWidget scrollWidget = new ScrollWidget(list);
      scrollWidget.addId("scroll-widget");
      contentWrapper.addChild(scrollWidget);
    } else {
      ScreenRendererWidget screen = new ScreenRendererWidget(true);
      screen.addId("settings");
      SettingContentActivity settings = new SettingContentActivity(this.editing.asRegistry("chatlistener").translationId("chatInput.tab.chatlistener"));
      settings.setHeaderType(HeaderType.FIXED);
      screen.displayScreen(settings);
      contentWrapper.addChild(screen);
    }

    this.contentWidget.addFlexibleContent(contentWrapper);
    return this.contentWidget;
  }

  @NotNull private Widget createEntry(@NotNull ChatListenerEntry entry) {
    DivWidget list = new DivWidget();
    list.addId("entry");
    String displayName = entry.getDisplayName().get();
    if (displayName.isEmpty()) {
      displayName = entry.getRegex().get();
    }

    ComponentWidget keyWidget = ComponentWidget.component(Component.text(displayName, NamedTextColor.GREEN));
    keyWidget.addId("display-name");
    list.addChild(keyWidget);
    IconWidget delete = new IconWidget(SpriteCommon.SMALL_X);
    list.addChild(delete).addId("delete");
    delete.setPressable(() -> {
      this.config.getChatListeners().get().remove(entry);
      CU.get().saveConfiguration();
      this.reload();
    });
    list.setPressable(() -> {
      this.original = entry;
      this.editing = entry.copy();
      if (this.editing.getDisplayName().get().isEmpty()) {
        this.editing.getDisplayName().set(this.editing.getRegex().get());
      }
      this.reload();
    });
    return list;
  }

  private static class SortByDisplayName implements Comparator<ChatListenerEntry> {
    @Override
    public int compare(ChatListenerEntry o1, ChatListenerEntry o2) {
      return o1.getDisplayName().get().compareTo(o2.getDisplayName().get());
    }
  }


}
