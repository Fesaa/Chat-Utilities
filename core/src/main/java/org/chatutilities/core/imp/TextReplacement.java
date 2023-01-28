package org.chatutilities.core.imp;


import java.util.Date;

public class TextReplacement {

  private final int ID;
  private boolean enabled;
  private String text;
  private String message;

  private TextReplacement(int ID, boolean enabled, String text, String message) {
    this.ID = ID;
    this.enabled = enabled;
    this.text = text;
    this.message = message;
  }

  public static TextReplacement create(int ID, boolean enabled, String text, String message) {
    return new TextReplacement(ID, enabled, text, message);
  }

  public static TextReplacement createDefault() {
    return create((int) (new Date()).getTime(), true, "", "");
  }

  public int getID() {
    return this.ID;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
