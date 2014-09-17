package com.outbound.ui.util;

import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by zeki on 31/08/2014.
 */
public class CustomImageView extends ImageView {
    public static float radius = 18.0f;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = 36.0f;
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);

        // Create a circular path.
//        final float halfWidth = canvas.getWidth()/2;
//        final float halfHeight = canvas.getHeight()/2;
//        final float radius = Math.max(halfWidth, halfHeight);
//        final Path path = new Path();
//        path.addCircle(halfWidth, halfHeight, radius, Path.Direction.CCW);
//
//        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
//        canvas.drawPath(path, paint);

//        super.onDraw(canvas);
//
//        // Load the bitmap as a shader to the paint.
//        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        final Shader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        paint.setShader(shader);
//
//        // Draw a circle with the required radius.
//        final float halfWidth = canvas.getWidth()/2;
//        final float halfHeight = canvas.getHeight()/2;
//        final float radius = Math.max(halfWidth, halfHeight);
//        canvas.drawCircle(halfWidth, halfHeight, radius, paint);
    }
}
