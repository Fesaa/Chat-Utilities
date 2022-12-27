package org.cu.core.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import org.cu.core.CU;
import org.cu.core.imp.ChatListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReceiveEventListener {

  private final CU addon;

  @Inject
  public ChatReceiveEventListener(CU addon) {this.addon = addon;}

  @Subscribe
  public void onChatReceiveEvent(ChatReceiveEvent chatReceiveEvent) {
    if (!this.addon.configuration().chatListeners().get()) {return;}

    for (ChatListener chatListener : this.addon.configuration().getChatListeners().values()) {
      if (!chatListener.isEnabled()) {
        continue;
      }

      String msg = chatListener.getMsg();
      Matcher matcher;

      if (this.addon.getPatternHashMap().containsKey(chatListener.getID())) {
        matcher = this.addon.getPatternHashMap().get(chatListener.getID())
            .matcher(chatReceiveEvent.chatMessage().getPlainText());
      } else {
        ClientPlayer p = this.addon.labyAPI().minecraft().clientPlayer();
        String name = p.getName();
        Pattern pattern = Pattern.compile(chatListener.getRegex().replace("&player", name));
        matcher = pattern.matcher(chatReceiveEvent.chatMessage().getPlainText());
      }

      if (!matcher.matches()) {
        continue;
      }

      for (int j = 0; j + 1 <= matcher.groupCount(); j++) {
        msg = msg.replace("&" + (j+1), matcher.group(j+1));
      }

      if (chatListener.isChat()) {
        smartSendWithDelay(chatListener, msg);
      }

      if (chatListener.isCommand()) {
        smartSendWithDelay(chatListener, "/" + msg);
      }

      if (chatListener.isSound()) {
        if (chatListener.getDelay() > 0) {
          Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
            this.addon.labyAPI().minecraft().sounds().playSound(ResourceLocation.create("minecraft", chatListener.getSoundId()), 100, 1);
          }, chatListener.getDelay(), TimeUnit.MILLISECONDS);
        } else {
          this.addon.labyAPI().minecraft().sounds().playSound(ResourceLocation.create("minecraft", chatListener.getSoundId()), 100, 1);
        }
      }

    }

  }

  private void smartSendWithDelay(ChatListener chatListener, String msg) {
    if (chatListener.getDelay() > 0) {
      Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()).schedule(() -> {
        this.addon.labyAPI().minecraft().chatExecutor().chat(msg, false);
      }, chatListener.getDelay(), TimeUnit.MILLISECONDS);
    } else {
      this.addon.labyAPI().minecraft().chatExecutor().chat(msg, false);
    }
  }
}
