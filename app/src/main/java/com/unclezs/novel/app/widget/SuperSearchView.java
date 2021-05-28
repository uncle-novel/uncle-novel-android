package com.unclezs.novel.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.unclezs.novel.app.R;
import com.xuexiang.xui.utils.ResUtils;

import org.jetbrains.annotations.NotNull;

/**
 * @author blog.unclezs.com
 * @date 2021/05/19 20:05
 */
public class SuperSearchView extends SearchView {

    public SuperSearchView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        setIconifiedByDefault(false);
        setSubmitButtonEnabled(true);
        onActionViewExpanded();
        SearchAutoComplete searchAutoComplete = findViewById(R.id.search_src_text);
        searchAutoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        searchAutoComplete.setPadding(25, 0, 0, 0);
        searchAutoComplete.setBackgroundColor(Color.TRANSPARENT);
        setBackground(ResUtils.getDrawable(R.drawable.bg_analysis_input));
        // 移除搜索图标
        View searchIcon = findViewById(R.id.search_mag_icon);
        ((ViewGroup) searchIcon.getParent()).removeView(searchIcon);

        ImageView closeButton = findViewById(R.id.search_close_btn);
        ImageView goButton = findViewById(R.id.search_go_btn);
        setIconDark(closeButton, goButton);

        LinearLayout editFrame = findViewById(R.id.search_edit_frame);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editFrame.getLayoutParams();
        params.setMargins(20, 0, 10, 0);
        editFrame.setLayoutParams(params);
        closeButton.setScaleX(0.8f);
        closeButton.setScaleY(0.8f);
        closeButton.setPadding(0, 0, 10, 0);
        goButton.setPadding(0, 0, 10, 0);
    }

    public SuperSearchView(@NonNull Context context) {
        this(context, null);
    }

    private void setIconDark(ImageView... imageViews) {
        for (ImageView imageView : imageViews) {
            Drawable wrappedDrawable = DrawableCompat.wrap(imageView.getDrawable());
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#888888"));
        }
    }
}
