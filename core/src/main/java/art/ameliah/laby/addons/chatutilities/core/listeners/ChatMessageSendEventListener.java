package art.ameliah.laby.addons.chatutilities.core.listeners;

import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatMessageSendEvent;
import art.ameliah.laby.addons.chatutilities.core.CU;
import art.ameliah.laby.addons.chatutilities.core.config.impl.TextReplacementEntry;

public class ChatMessageSendEventListener {

  private final CU addon;

  public ChatMessageSendEventListener(CU addon) {this.addon = addon;}

  @Subscribe
  public void onChatMessageSendEvent(ChatMessageSendEvent e) {
    if (!this.addon.configuration().textReplacement().get()
    || !this.addon.configuration().enabled().get()) {
      return;
    }

    String msg = e.getMessage();

    for (TextReplacementEntry textReplacement : this.addon.configuration().getTextReplacements().get()) {
      if (!textReplacement.getEnabled().get()
      || (textReplacement.serverConfig().enabled().get()
      && textReplacement.serverConfig().notAllowedToBeUsed(
          this.addon.labyAPI().serverController().getCurrentServerData()
      ))) {
        continue;
      }
      msg = msg.replace(textReplacement.getText().get(), textReplacement.message().get());
    }

    e.changeMessage(msg, e.getHistoryText());

  }

}
