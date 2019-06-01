package com.hereticpurge.simplespeeddial.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.ContactsContract;

import com.hereticpurge.simplespeeddial.R;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class ImageHelper {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
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

        if (lookupUri != null) {
            InputStream photoStream = ContactsContract
                    .Contacts
                    .openContactPhotoInputStream(context.getContentResolver(), lookupUri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(photoStream);
            photoBitmap = BitmapFactory.decodeStream(bufferedInputStream);
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
}
