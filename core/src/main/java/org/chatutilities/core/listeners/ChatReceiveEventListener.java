package org.chatutilities.core.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import net.labymod.api.Laby;
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
import net.labymod.api.util.concurrent.task.Task;
import org.chatutilities.core.CU;
import org.chatutilities.core.config.ChatListenerSubConfig;
import org.chatutilities.core.config.impl.ChatListenerEntry;
import org.chatutilities.core.config.impl.ChatListenerEntry.MatchType;
import org.chatutilities.core.config.impl.SoundConfig;

public class ChatReceiveEventListener {

  private final CU addon;

  private final HashMap<String, List<Long>> usage = new HashMap<>();
  private final Style onlyWhiteColour = Style.builder()
      .color(NamedTextColor.WHITE)
      .undecorate(TextDecoration.STRIKETHROUGH,
          TextDecoration.BOLD,
          TextDecoration.ITALIC,
          TextDecoration.OBFUSCATED,
          TextDecoration.UNDERLINED)
      .build();
  private final Style onlyGreenColour = Style.builder()
      .color(NamedTextColor.GREEN)
      .undecorate(TextDecoration.STRIKETHROUGH,
          TextDecoration.BOLD,
          TextDecoration.ITALIC,
          TextDecoration.OBFUSCATED,
          TextDecoration.UNDERLINED)
      .build();

  public ChatReceiveEventListener(CU addon) {this.addon = addon;}

  private final List<String> regexReplaceChars = Arrays.asList("\\", ")", "(", "[", "]", "+", ".", "^", "?", "{", "}", "$", "|");

  @Subscribe
  public void onChatReceiveEvent(ChatReceiveEvent e) {
    if (!this.addon.configuration().enabled().get()) {
      return;
    }

    if (this.addon.configuration().getChatListenerSubConfig().isEnabled()) {
      ClientPlayer p = this.addon.labyAPI().minecraft().getClientPlayer();
      if (p == null) {
        return;
      }

      for (ChatListenerEntry chatListener : this.addon.configuration().getChatListeners().get()) {
        if (!chatListener.getEnabled().get()
            || (chatListener.getServerConfig().enabled().get()
            && chatListener.getServerConfig().notAllowedToBeUsed(
            this.addon.labyAPI().serverController().getCurrentServerData()
        ))) {
          continue;
        }

        String msg = chatListener.getText().get();

        if (chatListener.getBlackListConfig().isBlockedByBlackList(e.chatMessage().getPlainText())) {
          continue;
        }

        String name = p.getName();
        String matchMessage = chatListener.getRegex().get().replace("&player", name);

        MatchType matchType = chatListener.getMatchType().get();

        if (matchType.equals(MatchType.SIMPLIFIED_REGEX)) {
          for (String regexChar : this.regexReplaceChars) {
            matchMessage = matchMessage.replace(regexChar, "\\" + regexChar);
          }
          matchMessage = matchMessage.replace("*", ".*");
          matchMessage = matchMessage.replace("%", "(.*)");
          matchType = MatchType.REGEX;
        }

        if (matchType.equals(MatchType.REGEX)) {
          Pattern pattern;
          try {
            pattern = Pattern.compile(matchMessage);
          } catch (PatternSyntaxException patternSyntaxException) {
            continue;
          }
          Matcher matcher = pattern.matcher(e.chatMessage().getPlainText());
          if (!matcher.matches()) {
            continue;
          }

          for (int i = 1; i <= matcher.groupCount(); i++) {
            msg = msg.replace("%" + i, matcher.group(i));
          }
        }

        if (matchType.equals(MatchType.EQUALS)) {
          if (!e.chatMessage().getPlainText().equals(matchMessage)) {
            continue;
          }
        }

        if (matchType.equals(MatchType.CONTAINS)) {
          if (!e.chatMessage().getPlainText().contains(matchMessage)) {
            continue;
          }
        }

        String ID = chatListener.getDisplayName().get() + chatListener.getRegex().get() + chatListener.getText().get();
        if (!this.usage.containsKey(ID)) {
          this.usage.put(ID, new ArrayList<>());
        }

        // Anti Spam
        ChatListenerSubConfig chatListenerSubConfig = this.addon.configuration().getChatListenerSubConfig();
        boolean useAntiSpam = chatListenerSubConfig.getUseAntiSpam().get();
        boolean canUse;
        if (useAntiSpam) {
          long now = (new Date()).getTime();
          int maxFrequency = chatListenerSubConfig.getMaxFrequency().get();
          int maxTimeSpan = chatListenerSubConfig.getMaxTimeSpan().get();
          canUse = this.canUse(now, maxFrequency, maxTimeSpan, ID);
          this.usage.get(ID).add(now);
        } else {
          canUse = true;
        }

        if (!chatListener.getText().isDefaultValue() && (canUse || chatListener.getText().get().startsWith("/"))) {
          smartSendWithDelay(chatListener, msg);
        }

        if (chatListener.getSoundConfig().getEnabled().get()) {
          SoundConfig soundConfig = chatListener.getSoundConfig();
          MinecraftSounds minecraftSounds = this.addon.labyAPI().minecraft().sounds();
          ResourceLocation sound = ResourceLocation.create("minecraft", soundConfig.getSoundId().get());
          if (chatListener.getDelay().get() > 0) {
            Task.builder(() ->
                minecraftSounds.playSound(sound, soundConfig.getVolume().get(), soundConfig.getPitch().get()))
                .delay((int) (chatListener.getDelay().get() * 1000), TimeUnit.MILLISECONDS)
                .build()
                .execute();
          } else {
            minecraftSounds.playSound(sound, soundConfig.getVolume().get(), soundConfig.getPitch().get());
          }
        }
      }
    }

    if (this.addon.configuration().getCopy().get()) {
      String msg = e.chatMessage().getPlainText();
      if (msg.trim().equals("")) {
        return;
      }
      e.setMessage(e.message()
          .append(Component.text(Laby.references().componentMapper()
                  .translateColorCodes('&', '\u00a7', this.addon.configuration().getCopyText().get()))
              .clickEvent(ClickEvent.copyToClipboard(msg))
              .hoverEvent(HoverEvent.showText(Component.text(this.addon.configuration().getCopyTooltip().get()))))
      );
    }

  }

  private void smartSendWithDelay(ChatListenerEntry chatListener, String msg) {
    ChatExecutor chatExecutor = this.addon.labyAPI().minecraft().chatExecutor();
    if (chatListener.getDelay().get() > 0) {
      Task.builder(() ->
          chatExecutor.chat(msg, false))
          .delay((int) (chatListener.getDelay().get() * 1000), TimeUnit.MILLISECONDS)
          .build()
          .execute();
    } else {
      chatExecutor.chat(msg, false);
    }
  }

  public boolean canUse(long now, int maxUses, int maxTime, String key) {
    int c = 0;
    for (Long useTime : this.usage.get(key)) {
      if (now - useTime < maxTime * 1000 *  60L) {
        c++;
      }
    }
    if (c == 0) {
      this.usage.get(key).clear();
      return true;
    }

    return c < maxUses;
  }
}
