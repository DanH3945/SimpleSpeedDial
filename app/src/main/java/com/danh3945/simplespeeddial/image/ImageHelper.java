package com.danh3945.simplespeeddial.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.ContactsContract;

import com.danh3945.simplespeeddial.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

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

        if (photoBitmap == null) {
            Timber.d("Tried to load Null thumbnail: Loading default icon");
            photoBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_contact_icon);
            photoBitmap = colorDefaultContactPhoto(photoBitmap);
        }

        return photoBitmap;
    }

    public static Bitmap getContactPhotoRounded(Context context, Uri lookupUri) {

        Bitmap photoBitmap = getContactPhoto(context, lookupUri);

        if (photoBitmap != null) {
            int pixels = context.getResources().getDimensionPixelSize(R.dimen.widget_photo_rounding_pixels);
            photoBitmap = ImageHelper.getRoundedCornerBitmap(photoBitmap, pixels);
        }

        return photoBitmap;
    }

    private static Bitmap colorDefaultContactPhoto(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = Color.argb(Color.alpha(pixels[i]), 255, 0, 0);
        }

        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        resultBitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixels));

        return resultBitmap;
    }
}
