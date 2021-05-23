package com.unclezs.novel.app.presenter;

import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.app.base.BasePresenter;
import com.unclezs.novel.app.db.entity.SearchRecord;
import com.unclezs.novel.app.model.SearchBookModel;
import com.unclezs.novel.app.views.fragment.other.SearchBookFragment;
import com.xuexiang.xormlite.AppDataBaseRepository;
import com.xuexiang.xormlite.db.DBService;
import com.xuexiang.xutil.data.DateUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2021/05/21 12:36
 */
public class SearchBookPresenter extends BasePresenter<SearchBookFragment> {
    private final DBService<SearchRecord> dbService;
    private final SearchBookModel model;

    public SearchBookPresenter(SearchBookFragment view) {
        dbService = AppDataBaseRepository.getInstance().getDataBase(SearchRecord.class);
        SearchBookModel.StateListener listener = new SearchBookModel.StateListener() {
            @Override
            public void onFinished(boolean hasMore) {
                view.onSearchFinished(hasMore);
            }

            @Override
            public void addMore(Novel novel) {
                view.addMore(novel);
            }
        };
        this.model = new SearchBookModel(view, listener);
    }

    public void saveQueryHistory(String query) {
        try {
            SearchRecord searchRecord = dbService.queryForColumnFirst("content", query);
            if (searchRecord == null) {
                searchRecord = new SearchRecord().setContent(query).setTime(DateUtils.getNowMills());
                dbService.insert(searchRecord);
            } else {
                searchRecord.setTime(DateUtils.getNowMills());
                dbService.updateData(searchRecord);
            }
            view.refreshRecord();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearSearchRecord() {
        try {
            dbService.deleteAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<SearchRecord> queryAllSearchRecord() {
        try {
            return dbService.queryAllOrderBy("time", false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void doSearch(String keyword) {
        model.doSearch(keyword);
    }

    public void loadMore() {
        model.loadMore();
    }
}
