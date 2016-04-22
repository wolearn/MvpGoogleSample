package com.demo.mvpgooglesample.login;

import android.text.TextUtils;

/**
 * Created by wulei
 * Data: 2016/4/21.
 */
public class LoginPresenter implements LoginContract.Present{
    private final LoginContract.View mView;

    public LoginPresenter(LoginContract.View view)
    {
        this.mView = view;

        //我这里直接把activity作为view，所以不需要
        //mView.setPresenter(this);
    }

    @Override
    public void login() {
        if(!validator())
        {
            return;
        }

        boolean result = LoginHttp.getInstance().httpLogin(mView.getAccount(), mView.getPassword());

        if(result){
            mView.loginSuccess();
        }else {
            mView.loginError("account or password is error");
        }
    }

    @Override
    public void start() {
        //TODO
    }

    /**
     * 登录参数校验
     *
     * @return
     */
    private boolean validator() {
        if (TextUtils.isEmpty(mView.getAccount())) {
            mView.loginError("account is empty");
            return false;
        }

        if (TextUtils.isEmpty(mView.getPassword())) {
            mView.loginError("account is empty");
            return false;
        }
        return true;
    }
}
