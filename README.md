# MvpGoogleSample
google mvp实践

![head.jpg](http://upload-images.jianshu.io/upload_images/1931006-36ee6a7060af4771.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#前言
在项目中用了挺长时间的mvp了，总体来说感觉不错，最大的体验是activity的结构明显变得美观了。在google没有出官方的mvp示例之前，大家对mvp在项目中的应用都有一些差异。现在google大哥一声吼，官方的示例在github上出现，终于看到了标准化的东西。已经看到了不少介绍google示例工程的文章，这里不再赘述，这篇文章主要是介绍肿么把google的mvp用到自己的项目中，那么，我在前面探探路。。。。
#官方Demo
还没下载官方demo下来玩弄的各位看官可以先去这个地址瞅瞅：
>[https://github.com/googlesamples/android-architecture](https://github.com/googlesamples/android-architecture)

再介绍一篇比较好的导读文章
>[https://mp.weixin.qq.com/s?__biz=MzA3ODg4MDk0Ng==&mid=403539764&idx=1&sn=d30d89e6848a8e13d4da0f5639100e5f&scene=1&srcid=0412M65yVH3uABy3bpavy6x0&pass_ticket=5MIdEBvaDHMVsC%2BFJ3TixYz0hIsRA3xGpPK4qAFVQEeg5QSzD0XnSvXUaXx88e%2Fu#rd](https://mp.weixin.qq.com/s?__biz=MzA3ODg4MDk0Ng==&mid=403539764&idx=1&sn=d30d89e6848a8e13d4da0f5639100e5f&scene=1&srcid=0412M65yVH3uABy3bpavy6x0&pass_ticket=5MIdEBvaDHMVsC%2BFJ3TixYz0hIsRA3xGpPK4qAFVQEeg5QSzD0XnSvXUaXx88e%2Fu#rd)

#实践
准备了一个登陆模块的实践例子。个人喜欢把各种新的东西用登陆模块先实验的癖好，主要基于几点考虑。
1 登陆模块是几乎每个app都有的，注意“几乎”，代表了本人严谨的治学态度和对奇葩app的兼容并包的包容心。
2 UI简单，主要是2个editText和一个点击button。
3 其他网络，缓存。。。。

项目的结构如下
![1.png](http://upload-images.jianshu.io/upload_images/1931006-47d12227c2ae7d09.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
主要是红色区域的6个文件。google引入了BasePresenter和BaseView这2个文件，我借用过来了
```
public interface BasePresenter {    
/**    
 * 页面初始化的时候做的事情，根据业务决定是否需要     
 */    
void start();}
```
```
public interface BaseView<T> {    
/**     
  * 使用fragment作为view时，将activity中的presenter传递给fragment     
  * @param presenter     
  */    
void setPresenter(T presenter);
}
```
这两个类作为所有view和presenter的基类来使用。
* BasePresenter中的start方法是用来load页面时加载相应数据的，而登录模块暂时并不需要该方法，但是这个类毕竟是为整个业务模块服务的，别的业务可能需要，暂时保留。
* BaseView中的setPresenter方法是为了向fragment中传递activity中new出来的presenter对象。登录模块其实一个activity足以搞定，这个方法多余，但是保留该方法，理由同上。

开始具体业务。这里需要构建view和presenter。值得注意的点是，google将view和presenter放到了一个契约类中了，所以。。。我们可以少建一个文件了。。是的。。我们就是这种偷懒的程序猿。
```
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
```
这里view需要提供四个UI相关的操作，包括提供页面输入的信息，登录后的结果信息。而presenter只要login就好。
```
public class LoginActivity extends AppCompatActivity implements LoginContract.View, View.OnClickListener {
    private EditText edtAccount, edtPassword;
    private Button btnLogin;
    private LoginContract.Present mPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        initParams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresent.start();
    }

    private void initParams() {
        mPresent = new LoginPresenter(this);
    }

    private void initViews() {
        edtAccount = (EditText) findViewById(R.id.edt_account);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void loginError(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess() {
        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @Override
    public String getAccount() {
        return edtAccount.getText().toString();
    }

    @Override
    public String getPassword() {
        return edtPassword.getText().toString();
    }


    @Override
    public void setPresenter(LoginContract.Present presenter) {

    }

    @Override
    public void onClick(View v) {
        mPresent.login();
    }
}
```
这里activity是作为mvp中的view实现了契约类中的view接口。google的示例中都是用的fragment作为view，但是宝宝真的不需要在登录的页面搞fragment，所以。。。。这就是变通。。。变通。。再来看下P的代码
```
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
```
注意，如果你是fragment作为view，一定要加上mView.setPresenter(this)把P传递过去。google的例子是这种场景最好的示范。我写个Loginhttp类模拟网络请求作为model。
```
public class LoginHttp {
    private LoginHttp(){};

    private static class InstanceHolder{
        private static final LoginHttp instance = new LoginHttp();
    }

    public static LoginHttp getInstance()
    {
        return InstanceHolder.instance;
    }

    /**
     * 模拟网络请求
     * @param account
     * @param password
     * @return
     */
    public boolean httpLogin(String account, String password) {
        if(account.equals("aaa") && password.equals("aaa"))
        {
            return true;
        }

        return false;
    }
}
```
over，如果你快受不了快爆炸的activity，原味的mvp是你无悔的选择。有用请帮忙戳喜欢。。。。。
