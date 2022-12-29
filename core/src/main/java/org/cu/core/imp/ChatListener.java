package org.cu.core.imp;

import org.cu.core.CU;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatListener {

  private final int ID;
  private boolean enabled;
  private String regex;
  private String msg;
  private String soundId;
  private int delay;
  private boolean chat;
  private boolean command;
  private boolean sound;
  private List<Long> usageList;


  private ChatListener(int ID, boolean enabled, String regex, String msg, String soundId, int delay, boolean chat, boolean command, boolean sound) {
    this.ID = ID;
    this.enabled = enabled;
    this.regex = regex;
    this.msg = msg;
    this.delay = delay;
    this.soundId = soundId;
    this.chat = chat;
    this.command = command;
    this.sound = sound;
    this.usageList = new ArrayList<>();
  }

  public static ChatListener create(int ID, boolean enabled, String regex, String msg, String soundId, int delay,  boolean chat, boolean command, boolean sound) {
    return new ChatListener(ID, enabled, regex, msg, soundId, delay,  chat, command, sound);
  }

  public static ChatListener createDefault() {
    return new ChatListener((int) (new Date()).getTime(), true, "", "", "", 0, false, false, false);
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getID() {
    return this.ID;
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public String getSoundId() {
    return soundId;
  }

  public void setSoundId(String soundId) {
    this.soundId = soundId;
  }

  public boolean isChat() {
    return chat;
  }

  public boolean isCommand() {
    return command;
  }

  public boolean isSound() {
    return sound;
  }

  public void setChat(boolean chat) {
    this.chat = chat;
  }

  public void setCommand(boolean command) {
    this.command = command;
  }

  public void setSound(boolean sound) {
    this.sound = sound;
  }

  public void addUsage(long now) {
    this.usageList.add(now);
  }

  public boolean canUse(long now, int maxUses, int maxTime) {
    int c = 0;
    for (Long useTime : this.usageList) {
      if (now - useTime < maxTime * 1000 *  60L) {
        c++;
      }
    }
    if (c == 0) {
      this.resetUsages();
      return true;
    }

    return c < maxUses;
  }

  private void resetUsages() {
    this.usageList = new ArrayList<>();
  }
}
