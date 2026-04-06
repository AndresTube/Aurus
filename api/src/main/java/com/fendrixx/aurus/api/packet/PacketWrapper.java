package com.fendrixx.aurus.api.packet;

public final class PacketWrapper {

  private final Object[] data;

  public PacketWrapper(Object... data) {
    this.data = data;
  }

  public Object get(int index) {
    return data[index];
  }

  public <T> T get(int index, Class<T> type) {
    return type.cast(data[index]);
  }

  public <T> T getOrDefault(int index, T def) {
    try {
      final T value = (T) get(index);
      return value == null ? def : value;
    } catch (IndexOutOfBoundsException ignored) {
      return def;
    }
  }

  public Object[] raw() {
    return data;
  }
}