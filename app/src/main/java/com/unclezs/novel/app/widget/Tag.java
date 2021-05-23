package com.unclezs.novel.app.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.unclezs.novel.app.R;


/**
 * @author blog.unclezs.com
 * @date 2021/05/18 14:36
 */
public class Tag extends AppCompatTextView {

    public Tag(@NonNull Context context) {
        this(context, null, android.R.attr.textViewStyle);
    }

    public Tag(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public Tag(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.parseColor("#009688"));
        setTextColor(Color.WHITE);
        setPadding(10, 5, 10, 5);
        setBackgroundResource(R.drawable.widget_tag);
        setTextSize(12);
    }

}
