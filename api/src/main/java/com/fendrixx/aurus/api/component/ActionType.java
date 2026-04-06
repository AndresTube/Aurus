package com.fendrixx.aurus.api.component;

public enum ActionType {

  /** The component is being rendered for the first time to a player. */
  RENDER,

  /** Left-click on the component. */
  CLICK,

  /** Right-click (secondary click) on the component. */
  CLICK_LEFT,

  /** Scroll wheel event inside the component's hitbox. */
  SCROLL,

  /**
   * Cursor entered the component's hitbox this tick.
   * Fired once on the transition; not every tick while hovered.
   */
  HOVER_START,

  /**
   * Cursor left the component's hitbox this tick.
   * Fired once on the transition.
   */
  HOVER_END,

  UPDATE,
  /**
   * Generic "cursor is over this component" — kept for backwards compat
   * and simple sub-components that don't need enter/leave distinction.
   * Fired every tick the cursor remains inside the hitbox.
   */
  HOVER,

  /** The component is being removed from the world. */
  DECONSTRUCT;
}