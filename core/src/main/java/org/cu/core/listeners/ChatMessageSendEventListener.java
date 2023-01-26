package org.cu.core.listeners;

import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import org.cu.core.CU;
import org.cu.core.imp.TextReplacement;

public class ChatMessageSendEventListener {

  private final CU addon;

  public ChatMessageSendEventListener(CU addon) {this.addon = addon;}

  @Subscribe
  public void onChatMessageSendEvent(ChatMessageSendEvent chatMessageSendEvent) {
    if (!this.addon.configuration().textReplacement().get()) {return;}

    String msg = chatMessageSendEvent.getMessage();

    for (TextReplacement textReplacement : this.addon.configuration().getTextReplacements().values()) {
      if (!textReplacement.isEnabled()) {
        continue;
      }
      msg = msg.replace(textReplacement.getText(), textReplacement.getMessage());
    }

    chatMessageSendEvent.changeMessage(msg);

  }

}
