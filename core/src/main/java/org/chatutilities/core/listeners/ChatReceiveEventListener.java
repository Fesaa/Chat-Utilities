package org.chatutilities.core.listeners;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.labymod.api.client.chat.ChatExecutor;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.event.ClickEvent;
import net.labymod.api.client.component.event.HoverEvent;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.Style;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.resources.sound.MinecraftSounds;
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
    if (!this.addon.configuration().getChatListenerSubConfig().isEnabled()
        || !this.addon.configuration().enabled().get()) {
      return;
    }

    ClientPlayer p = this.addon.labyAPI().minecraft().getClientPlayer();
    if (p == null) {
      return;
    }

    for (ChatListener chatListener : this.addon.configuration().getChatListeners().values()) {
      if (!chatListener.isEnabled()) {
        continue;
      }

      String msg = chatListener.getMsg();
      Pattern pattern = this.addon.getPatternHashMap().get(chatListener.getID());
      if (pattern == null) {
        String name = p.getName();
        pattern = Pattern.compile(chatListener.getRegex().replace("&player", name));
        this.addon.getPatternHashMap().put(chatListener.getID(), pattern);
      }

      Matcher matcher = pattern.matcher(chatReceiveEvent.chatMessage().getPlainText());
      if (!matcher.matches()) {
        continue;
      }

      // Anti Spam
      ChatListenerSubConfig chatListenerSubConfig = this.addon.configuration().getChatListenerSubConfig();
      boolean useAntiSpam = chatListenerSubConfig.getUseAntiSpam().get();
      boolean canUse;
      if (useAntiSpam) {
        long now = (new Date()).getTime();
        int maxFrequency = chatListenerSubConfig.getMaxFrequency().get();
        int maxTimeSpan = chatListenerSubConfig.getMaxTimeSpan().get();
        canUse = !chatListener.canUse(now, maxFrequency, maxTimeSpan);
        chatListener.addUsage(now);
      } else {
        canUse = true;
      }

      for (int i = 1; i < matcher.groupCount(); i++) {
        msg = msg.replace("&" + (i), matcher.group(i));
      }

      if (chatListener.isChat() && canUse) {
        smartSendWithDelay(chatListener, msg);
      }

      if (chatListener.isCommand()) {
        smartSendWithDelay(chatListener, "/" + msg);
      }

      if (chatListener.isSound()) {
        MinecraftSounds minecraftSounds = this.addon.labyAPI().minecraft().sounds();
        ResourceLocation sound = ResourceLocation.create("minecraft", chatListener.getSoundId());
        if (chatListener.getDelay() > 0) {
          Executors.newScheduledThreadPool(
              Runtime.getRuntime().availableProcessors()).schedule(
                  () -> minecraftSounds.playSound(sound, 100, 1),
              chatListener.getDelay(), TimeUnit.MILLISECONDS);
        } else {
          minecraftSounds.playSound(sound, 100, 1);
        }
      }

    }

    if (this.addon.configuration().getCopy().get()) {
      String msg = chatReceiveEvent.chatMessage().getPlainText();
      if (msg.trim().equals("")) {
        return;
      }

    Style onlyWhiteColour = Style.builder()
        .color(NamedTextColor.WHITE)
        .undecorate(TextDecoration.STRIKETHROUGH,
            TextDecoration.BOLD,
            TextDecoration.ITALIC,
            TextDecoration.OBFUSCATED,
            TextDecoration.UNDERLINED)
            .build();
    Style onlyGreenColour = Style.builder()
        .color(NamedTextColor.GREEN)
        .undecorate(TextDecoration.STRIKETHROUGH,
            TextDecoration.BOLD,
            TextDecoration.ITALIC,
            TextDecoration.OBFUSCATED,
            TextDecoration.UNDERLINED)
        .build();

      chatReceiveEvent.setMessage(chatReceiveEvent.message()
          .append(Component.text(" [").style(onlyWhiteColour))
          .append(Component.text("Copy").style(onlyGreenColour)
              .clickEvent(ClickEvent.copyToClipboard(msg))
              .hoverEvent(HoverEvent.showText(Component.text("Click to copy."))))
          .append(Component.text("]").style(onlyWhiteColour))
      );
    }

  }

  private void smartSendWithDelay(ChatListener chatListener, String msg) {
    ChatExecutor chatExecutor = this.addon.labyAPI().minecraft().chatExecutor();
    if (chatListener.getDelay() > 0) {
      Executors.newScheduledThreadPool(
          Runtime.getRuntime().availableProcessors()).schedule(
              () ->
                  chatExecutor.chat(msg, false),
          chatListener.getDelay(), TimeUnit.MILLISECONDS);
    } else {
      chatExecutor.chat(msg, false);
    }
  }
}
