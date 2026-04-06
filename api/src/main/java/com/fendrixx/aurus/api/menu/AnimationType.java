package com.fendrixx.aurus.api.menu;

public enum AnimationType {
  NONE,
  /** Scale from 0 → normal on open, normal → 0 on close. */
  SCALE,
  /** Interpolated translation slide from an offset. */
  SLIDE,
  /** Text-display background-color alpha fade (open/close). */
  FADE;
}