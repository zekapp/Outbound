package com.outbound.util;

import java.util.List;

/**
 * Created by zeki on 21/10/2014.
 */

public abstract class MessagesResultCallback<T> {
    public abstract void done(List<T> newMessages, Exception e);
}
