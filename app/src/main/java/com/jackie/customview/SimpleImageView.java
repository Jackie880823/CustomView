/*
 *    Copyright 2016 The Open Source Project of Jackie Zhu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.jackie.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by on 16/5/10.
 *
 * @author Jackie Zhu
 * @version 1.0
 */
public class SimpleImageView extends View {
    // 画笔
    private Paint mBitmapPaint;

    // 图片
    private Drawable mDrawable;

    // View的宽度
    private int mWidth;
    // View的高度
    private int mHeight;
    /**
     * 添加文字
     */
    private CharSequence text;
    /**
     * 被添加的文字的大小
     */
    private int textSize;
    /**
     * 被添加的文字颜色
     */
    private int textColor;

    public SimpleImageView(Context context) {
        this(context, null);
    }

    public SimpleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 根据属性初始化
        initAttrs(attrs);

        // 初始化画笔
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
    }

    /**
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = null;

            try {
                array = getContext().obtainStyledAttributes(attrs, R.styleable.SimpleImageView);
                mDrawable = array.getDrawable(R.styleable.SimpleImageView_src);
                text = array.getText(R.styleable.SimpleImageView_text);
                textSize = array.getDimensionPixelSize(R.styleable.SimpleImageView_textSize, 24);
                textColor = array.getColor(R.styleable.SimpleImageView_textColor, Color.BLACK);

                // 测量 Drawable对象的宽, 高
                measureDrawable();
            } finally {
                if (array != null) {
                    array.recycle();
                }
            }
        }
    }

    /**
     * 测量Drawable对象的宽和高,若Drawable对象为空则抛出异常
     */
    private void measureDrawable() {
        if (mDrawable == null) {
            throw new RuntimeException("drawable is null");
        }
        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取宽度的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 获取宽度的大小
        int width = MeasureSpec.getSize(widthMeasureSpec);

        // 获取高度的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // 获取高度的大小
        int height = MeasureSpec.getSize(heightMeasureSpec);
        // 设置View的宽高
        setMeasuredDimension(measureWidth(widthMode, width), measureHeight(heightMode, height));
    }

    /**
     * 测量View的高
     * @param heightMode 高的模式
     * @param height {@link MeasureSpec#getSize(int)}得到的高
     * @return 测量后的高
     */
    private int measureHeight(int heightMode, int height) {
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.EXACTLY:
                mHeight = height;
                break;
        }
        return mHeight;
    }

    /**
     * 测量View的宽
     * @param widthMode 宽的模式
     * @param width {@link MeasureSpec#getSize(int)}得到的宽
     * @return 测量后的宽
     */
    private int measureWidth(int widthMode, int width) {
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.EXACTLY:
                mWidth = width;
                break;
        }
        return mWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable == null) {
            return;
        }

        // 根据测量的后的高宽按比例来放大图片
        Bitmap bitmap = Bitmap.createScaledBitmap(drawableToBitmap(mDrawable), getMeasuredWidth(),
                getMeasuredHeight(), true);

        // 绘制图片
        canvas.drawBitmap(bitmap, getLeft(), getTop(), mBitmapPaint);
        if (!TextUtils.isEmpty(text)) {
            // 保存画布
            canvas.save();
            // 施加90º
            canvas.rotate(90);
            // 设置文字颜色
            mBitmapPaint.setColor(textColor);
            // 设置文字大小
            mBitmapPaint.setTextSize(textSize);
            // 绘制文本
            canvas.drawText(text, 0, text.length(), getLeft() + 50, getTop() - 50, mBitmapPaint);
            // 恢复原来的状态
            canvas.restore();
        }
    }

    /**
     * {@link Drawable} 转 {@link Bitmap}
     * @param drawable 需要转成{@link Bitmap}的{@link Drawable} 实例
     * @return 返回转换成功后的 {@link Bitmap}
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(result);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return result;
    }

}
