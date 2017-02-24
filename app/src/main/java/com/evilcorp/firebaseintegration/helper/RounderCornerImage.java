package com.evilcorp.firebaseintegration.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.evilcorp.firebaseintegration.R;

/**
 * Created by hristo.stoyanov on 20-Feb-17.
 */

public class RounderCornerImage extends BitmapImageViewTarget {
    private ImageView view;
    private Context context;
    private static final int BORDER_WIDTH = 10;
    private int borderColor;
    public RounderCornerImage(Context context, ImageView view) {
        super(view);
        this.view = view;
        this.context = context;
        borderColor = ContextCompat.getColor(context, R.color.black);
    }
    @Override
    protected void setResource(Bitmap resource) {
        Canvas canvas = new Canvas(resource);
        canvas.drawBitmap(resource, 0, 0, null);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(borderColor);

        int circleDelta = (BORDER_WIDTH / 2);
        int radius = (canvas.getWidth() / 2) - circleDelta;
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2,radius,borderPaint);

        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
        circularBitmapDrawable.setCircular(true);
        view.setImageDrawable(circularBitmapDrawable);
    }
}
