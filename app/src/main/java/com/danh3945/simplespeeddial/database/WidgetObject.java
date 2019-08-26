package com.danh3945.simplespeeddial.database;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.danh3945.simplespeeddial.image.ImageHelper;

import javax.annotation.Nullable;

public abstract class WidgetObject {

    public abstract String getName();

    public abstract String getNumber();

    public abstract Uri getLookupUri();

    public Drawable getContactPhotoSquare(Context context, int heightPx, int widthPx) {
        if (getLookupUri() == null) {
            return null;
        }

        Bitmap bitmap = ImageHelper.getContactPhoto(context, getLookupUri());
        if (bitmap == null) {

            if (heightPx > -1 && widthPx > -1) {
                return ImageHelper.getDefaultContactIconSquare(getName(), heightPx, widthPx);
            }

            return ImageHelper.getDefaultContactIconSquare(getName());
        }

        Drawable bitmapDrawable = new BitmapDrawable(context.getResources(), bitmap);

        return bitmapDrawable;
    }

    @Nullable
    public Drawable getContactPhotoSquare(Context context) {
        return getContactPhotoSquare(context, -1, -1);
    }

    @Nullable
    public Drawable getContactPhotoRounded(Context context) {
        return getContactPhotoRounded(context, -1, -1);
    }

    public Drawable getContactPhotoRounded(Context context, int heightPx, int widthPx) {
        if (getLookupUri() == null) {
            return null;
        }
        Drawable drawable = ImageHelper.getContactPhotoRounded(context, getLookupUri());
        if (drawable == null) {

            if (heightPx > -1 && widthPx > -1) {
                return ImageHelper.getDefaultContactIconRounded(getName(), heightPx, widthPx);
            }
            return ImageHelper.getDefaultContactIconRounded(getName());
        }

        return drawable;
    }
}
