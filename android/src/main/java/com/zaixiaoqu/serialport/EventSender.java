package com.zaixiaoqu.serialport;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.WritableMap;

public interface EventSender {
    void sendEvent(String eventName, @Nullable WritableMap event);
}
