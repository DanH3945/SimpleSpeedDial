package com.danh3945.simplespeeddial.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.danh3945.simplespeeddial.R;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class ImageHelper {

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Nullable
    public static Bitmap getContactPhoto(Context context, Uri lookupUri) {

        Bitmap photoBitmap = null;

        InputStream photoStream;
        if (lookupUri != null) {
            try {
                photoStream = ContactsContract
                        .Contacts
                        .openContactPhotoInputStream(context.getContentResolver(), lookupUri);
            } catch (SecurityException se) {
                Timber.i(se);
                photoStream = null;
            }
            if (photoStream != null) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(photoStream);
                photoBitmap = BitmapFactory.decodeStream(bufferedInputStream);

                try {
                    photoStream.close();
                    bufferedInputStream.close();
                } catch (IOException e) {
                    Timber.d(e);
                }
            }
        }

        return photoBitmap;
    }

    public static Drawable getDefaultContactIconSquare(String name, int heightPx, int widthPx) {
        int color = ColorGenerator.MATERIAL.getColor(name);

        String firstLetter = String.valueOf(name.charAt(0));

        return TextDrawable.builder()
                .beginConfig()
                .height(heightPx)
                .width(widthPx)
                .endConfig()
                .buildRect(firstLetter, color);
    }

    public static Drawable getDefaultContactIconSquare(String name) {
        int color = ColorGenerator.MATERIAL.getColor(name);

        String firstLetter = String.valueOf(name.charAt(0));

        return TextDrawable.builder().buildRect(firstLetter, color);
    }

    public static Drawable getDefaultContactIconRounded(String name, int heightPx, int widthPx) {
        int color = ColorGenerator.MATERIAL.getColor(name);

        String firstLetter = String.valueOf(name.charAt(0));

        return TextDrawable.builder()
                .beginConfig()
                .height(heightPx)
                .width(widthPx)
                .endConfig()
                .buildRound(firstLetter, color);
    }

    public static Drawable getDefaultContactIconRounded(String name) {
        int color = ColorGenerator.MATERIAL.getColor(name);

        String firstLetter = String.valueOf(name.charAt(0));

        return TextDrawable.builder().buildRound(firstLetter, color);

    }

    @Nullable
    public static Drawable getContactPhotoRounded(Context context, Uri lookupUri) {

        Bitmap photoBitmap = getContactPhoto(context, lookupUri);

        if (photoBitmap != null) {
            int pixels = context.getResources().getDimensionPixelSize(R.dimen.widget_photo_rounding_pixels);
            photoBitmap = ImageHelper.getRoundedCornerBitmap(photoBitmap, pixels);
            return new BitmapDrawable(context.getResources(), photoBitmap);
        }

        return null;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
