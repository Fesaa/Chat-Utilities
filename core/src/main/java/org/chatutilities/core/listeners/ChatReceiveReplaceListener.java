package org.chatutilities.core.listeners;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.event.Priority;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.chat.ChatReceiveEvent;
import net.labymod.api.event.client.render.PlayerNameTagRenderEvent;
import org.chatutilities.core.CU;
import org.chatutilities.core.config.impl.TextReplacementEntry;
import java.util.function.Supplier;

public class ChatReceiveReplaceListener {

  private final CU addon;

  public ChatReceiveReplaceListener(CU addon) {
    this.addon = addon;
  }

  @Subscribe(value = Priority.LATEST)
  public void replace(ChatReceiveEvent e) {
    if (!addon.configuration().textReplacement().get()) {
      return;
    }

    Component msg = e.message();
    boolean replaced = false;

    for (TextReplacementEntry entry : addon.configuration().getTextReplacements().get()) {
      if (entry.getEnabled().get() && entry.onReceive().get()) {
        if (replace(msg, entry.getText().get(), () -> Component.text(entry.message().get()))) {
          replaced = true;
        }
      }
    }

    // We don't want false reports because of this...
    if (replaced) {
      msg.append(PlayerNameTagRenderEvent.EDITED_COMPONENT);
    }

    e.setMessage(msg);
    }

    /*
      * This method is a modified (argument names) version of the replace method from
      * https://github.com/labymod-addons/customnametags/blob/main/core/src/main/java/net/labymod/addons/customnametags/CustomNameTags.java
     */
  public boolean replace(
      Component component,
      String replacing,
      Supplier<Component> replacement
  ) {
    boolean replaced = false;
    for (Component child : component.getChildren()) {
      if (this.replace(child, replacing, replacement)) {
        replaced = true;
      }
    }

    if (component instanceof TranslatableComponent) {
      for (Component argument : ((TranslatableComponent) component).getArguments()) {
        if (this.replace(argument, replacing, replacement)) {
          replaced = true;
        }
      }
    }

    if (component instanceof TextComponent textComponent) {
      String text = textComponent.getText();

      int next = text.indexOf(replacing);
      if (next != -1) {
        replaced = true;
        int length = text.length();
        if (next == 0) {
          if (length == replacing.length()) {
            textComponent.text("");
            component.append(0, replacement.get());
            return true;
          }

          if (length > replacing.length() && text.charAt(replacing.length()) != ' ') {
            return false;
          }
        }

        textComponent.text("");
        int lastNameAt = 0;
        int childIndex = 0;
        for (int i = 0; i < length; i++) {
          if (i != next) {
            continue;
          }

          if (i > lastNameAt) {
            component.append(childIndex++, Component.text(text.substring(lastNameAt, i)));
          }

          component.append(childIndex++, replacement.get());
          lastNameAt = i + replacing.length();
        }

        // no way to properly check for this in chat
        if (lastNameAt < length) {
          component.append(childIndex, Component.text(text.substring(lastNameAt)));
        }
      }
    }

    return replaced;
  }
  }

