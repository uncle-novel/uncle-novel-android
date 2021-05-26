package com.unclezs.novel.app.views.fragment.download;

import android.annotation.SuppressLint;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.app.App;
import com.unclezs.novel.app.R;
import com.unclezs.novel.app.base.BaseFragment;
import com.unclezs.novel.app.model.SpiderWrapper;
import com.unclezs.novel.app.presenter.DownloadingPresenter;
import com.unclezs.novel.app.utils.XToastUtils;
import com.unclezs.novel.app.utils.rx.RxUtils;
import com.unclezs.novel.app.views.adapter.DownloadingTaskAdapter;
import com.xuexiang.constant.PermissionConstants;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.app.ActivityLifecycleHelper;
import com.xuexiang.xutil.system.PermissionUtils;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Completable;
import lombok.Getter;

/**
 * @author blog.unclezs.com
 * @date 2021/05/26 9:44
 */
@Page
@SuppressLint("NonConstantResourceId")
public class DownloadingFragment extends BaseFragment<DownloadingPresenter> {
    public static final String BUS_ACTION_ADD_TASK = "BUS_ACTION_ADD_TASK";
    public static final String BUS_ACTION_MAX_TASK_NUM_CHANGE = "BUS_ACTION_MAX_TASK_NUM_CHANGE";
    @BindView(R.id.task_view)
    RecyclerView taskView;
    @Getter
    private DownloadingTaskAdapter adapter;


    @Override
    protected TitleBar initTitle() {
        return null;
    }

    @Override
    public DownloadingPresenter createPresenter() {
        return new DownloadingPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_downloading;
    }

    @Override
    protected void initArgs() {
        super.initArgs();
        RxBus.get().register(this);
    }

    @Override
    protected void initViews() {
        adapter = new DownloadingTaskAdapter(presenter);
        taskView.setLayoutManager(new LinearLayoutManager(requireContext()));
        taskView.setAdapter(adapter);
        Completable.create(emitter -> {
            presenter.restore();
            emitter.onComplete();
        }).compose(o -> RxUtils.threadTrans(o, getLifecycleOwner()))
            .doOnComplete(() -> presenter.runTask())
            .subscribe();
    }

    public void addTask(SpiderWrapper wrapper) {
        adapter.load(wrapper);
    }

    public void addTask(List<SpiderWrapper> wrapper) {
        taskView.post(() -> {
            adapter.clear();
            adapter.loadMore(wrapper);
        });
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,tags = {@Tag(BUS_ACTION_ADD_TASK)})
    public void submitDownload(Novel novel) {
        PermissionUtils.permission(PermissionConstants.STORAGE)
            .callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    presenter.addTask(novel);
                    XToastUtils.success("添加下载成功");
                }

                @Override
                public void onDenied() {
                    XToastUtils.success("请先授予存储权限");
                }
            })
            .request();
    }

    @Subscribe(thread = EventThread.MAIN_THREAD,tags = {@Tag(BUS_ACTION_MAX_TASK_NUM_CHANGE)})
    public void onMaxTaskNumChange(Integer num) {
        presenter.runTask();
        XToastUtils.success("更改成功");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }
}
