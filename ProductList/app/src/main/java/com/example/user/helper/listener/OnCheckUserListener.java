package com.example.user.helper.listener;

import com.example.user.model.User;

public interface OnCheckUserListener {
    void onSucceed(User user);
    void onFailed();
}
