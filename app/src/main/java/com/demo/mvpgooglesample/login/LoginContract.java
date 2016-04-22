package com.demo.mvpgooglesample.login;

import com.demo.mvpgooglesample.base.BasePresenter;
import com.demo.mvpgooglesample.base.BaseView;

/**
 * Created by wulei
 * Data: 2016/4/21.
 */
public class LoginContract {

    interface View extends BaseView<Present>{

        void loginError(String msg);

        void loginSuccess();

        String getAccount();

        String getPassword();
    }

    interface Present extends BasePresenter{

        void login();

    }
}
