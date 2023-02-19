package org.chatutilities.core.listeners;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.chatutilities.core.CU;
import org.chatutilities.core.config.ChatListenerSubConfig;
import org.chatutilities.core.imp.ChatListener;

public class ChatReceiveEventListener {

  private final CU addon;

  public ChatReceiveEventListener(CU addon) {this.addon = addon;}

  @Subscribe
  public void onChatReceiveEvent(ChatReceiveEvent chatReceiveEvent) {
    if (!this.addon.configuration().getChatListenerSubConfig().isEnabled()) {return;}

    for (ChatListener chatListener : this.addon.configuration().getChatListeners().values()) {
      if (!chatListener.isEnabled()) {
        continue;
      }

      String msg = chatListener.getMsg();
      Pattern pattern;
      Matcher matcher;

      if (this.addon.getPatternHashMap().containsKey(chatListener.getID())) {
        pattern = this.addon.getPatternHashMap().get(chatListener.getID());
      } else {
        ClientPlayer p = this.addon.labyAPI().minecraft().clientPlayer();
        String name = p.getName();
        pattern = Pattern.compile(chatListener.getRegex().replace("&player", name));
        this.addon.getPatternHashMap().put(chatListener.getID(), pattern);
      }

      matcher = pattern.matcher(chatReceiveEvent.chatMessage().getPlainText());

      if (!matcher.matches()) {
        continue;
      }

      // Anti Spam
      long now = (new Date()).getTime();
      ChatListenerSubConfig chatListenerSubConfig = this.addon.configuration().getChatListenerSubConfig();
      boolean useAntiSpam = chatListenerSubConfig.getUseAntiSpam().get();
      int maxFrequency = chatListenerSubConfig.getMaxFrequency().get();
      int maxTimeSpan = chatListenerSubConfig.getMaxTimeSpan().get();
      boolean canUse = !chatListener.canUse(now, maxFrequency, maxTimeSpan);
      chatListener.addUsage(now);

      for (int j = 0; j + 1 <= matcher.groupCount(); j++) {
        msg = msg.replace("&" + (j+1), matcher.group(j+1));
      }

      if (chatListener.isChat() && (!useAntiSpam || canUse)) {
        smartSendWithDelay(chatListener, msg);
      }

      if (chatListener.isCommand()) {
        smartSendWithDelay(chatListener, "/" + msg);
      }

      if (chatListener.isSound()) {
        if (chatListener.getDelay() > 0) {
          Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> this.addon.labyAPI().minecraft().sounds().playSound(ResourceLocation.create("minecraft", chatListener.getSoundId()), 100, 1), chatListener.getDelay(), TimeUnit.MILLISECONDS);
        } else {
          this.addon.labyAPI().minecraft().sounds().playSound(ResourceLocation.create("minecraft", chatListener.getSoundId()), 100, 1);
        }
      }

    }

    if (this.addon.configuration().getCopy().get()) {
      String msg = chatReceiveEvent.chatMessage().getPlainText();
      if (msg.trim().equals("")) {
        return;
      }
      this.addon.logger().info(msg);
      chatReceiveEvent.setMessage(chatReceiveEvent.message()
          .append(Component.text(" [", NamedTextColor.WHITE))
          .append(Component.text("Copy", NamedTextColor.GREEN)
              .clickEvent(ClickEvent.copyToClipboard(msg))
              .hoverEvent(HoverEvent.showText(Component.text("Click to copy."))))
          .append(Component.text("]", NamedTextColor.WHITE))
      );
    }

  }

  private void smartSendWithDelay(ChatListener chatListener, String msg) {
    if (chatListener.getDelay() > 0) {
      Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> this.addon.labyAPI().minecraft().chatExecutor().chat(msg, false), chatListener.getDelay(), TimeUnit.MILLISECONDS);
    } else {
      this.addon.labyAPI().minecraft().chatExecutor().chat(msg, false);
    }
  }
}
