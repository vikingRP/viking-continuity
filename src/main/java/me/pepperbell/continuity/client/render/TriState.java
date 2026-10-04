package me.pepperbell.continuity.client.render;

public enum TriState {
  DEFAULT,
  TRUE,
  FALSE;

  public static TriState of(boolean b) {
    return b ? TRUE : FALSE;
  }
}
