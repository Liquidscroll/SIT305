package com.example.chatbot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

public class CustomIconDrawable {

    public static Drawable getCustomIconDrawable(Context context, @ColorInt int color, String text, int width, int height, boolean isTextCircle) {
        if (isTextCircle) {
            // Create a shape for a text circle
            Shape shape = new Shape() {
                @Override
                public void draw(Canvas canvas, Paint paint) {
                    paint.setColor(color);
                    paint.setAntiAlias(true);

                    // Draw a circle
                    float radius = Math.min(width, height) / 2f;
                    canvas.drawCircle(width / 2f, height / 2f, radius, paint);

                    // Set text properties and draw text
                    paint.setColor(Color.DKGRAY);
                    paint.setTextSize(radius * 0.8f);
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                    // Calculate vertical alignment for text
                    float textOffset = (paint.descent() + paint.ascent()) / 2f;
                    canvas.drawText(text, width / 2f, height / 2f - textOffset, paint);
                }
            };
            ShapeDrawable drawable = new ShapeDrawable(shape);
            drawable.setIntrinsicWidth(width);
            drawable.setIntrinsicHeight(height);
            return drawable;
        }
        else
        {
            // Otherwise we want our AI image
            return ContextCompat.getDrawable(context, R.drawable.outline_auto_awesome_24);
        }
    }
}
