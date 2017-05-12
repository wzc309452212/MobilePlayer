package Base;

import android.content.Context;
import android.view.View;

/**
 * Created by Administrator on 2017/5/12.
 */
public abstract class BasePager {
    /*
    上下文
     */
    public final Context context;
    public View rootView;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    /*
    强制由孩子实现，实现特定效果
     */
    public abstract View initView();
    /*
    当子页面需要初始化数据，联网请求数据或者绑定数据的时候要重写该方法
     */
    public void initData(){}
}
