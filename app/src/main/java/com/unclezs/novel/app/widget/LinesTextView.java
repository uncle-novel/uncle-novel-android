package com.unclezs.novel.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author blog.unclezs.com
 * @date 2021/05/18 15:08
 */
public class LinesTextView extends AppCompatTextView {


    public LinesTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateLines();
        super.onDraw(canvas);
    }

    private void calculateLines() {
        int mHeight = getMeasuredHeight();
        int lHeight = getLineHeight();
        int lines = mHeight / lHeight;
        setLines(lines);
    }
}
