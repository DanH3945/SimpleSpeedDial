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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Random;

import timber.log.Timber;

public class ImageHelper {

    public static int IGNORE_COLOR = -1;

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

    public static Bitmap getDefaultContactIcon(Context context, int color) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_contact_icon);
        if (color <= IGNORE_COLOR) {
            return bitmap;
        }
        return colorDefaultContactPhoto(color, bitmap);
    }

    @Nullable
    public static Bitmap getContactPhotoRounded(Context context, Uri lookupUri) {

        Bitmap photoBitmap = getContactPhoto(context, lookupUri);

        if (photoBitmap != null) {
            int pixels = context.getResources().getDimensionPixelSize(R.dimen.widget_photo_rounding_pixels);
            photoBitmap = ImageHelper.getRoundedCornerBitmap(photoBitmap, pixels);
        }

        return photoBitmap;
    }

    private static Bitmap colorDefaultContactPhoto(int defaultColor, Bitmap bitmap) {
        // Todo better system to select colors for default icons.
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int red = Color.red(defaultColor);
        int green = Color.green(defaultColor);
        int blue = Color.blue(defaultColor);

        for (int i = 0; i < pixels.length; i++) {
            if (Color.alpha(pixels[i]) > 254) {
                pixels[i] = Color.argb(Color.alpha(pixels[i]), red, green, blue);
            } else {
                pixels[i] = Color.argb(255, 255, 255, 255);
            }
        }

        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        resultBitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixels));

        return resultBitmap;
    }

    public static int getRandomContactIconColorInt(Context context) {

        int[] colors = context.getResources().getIntArray(R.array.iconColors);

        Random random = new Random();

        int randomInt = random.nextInt(colors.length);

        int colorInt = colors[randomInt];

        int blue = Color.red(colorInt);
        int green = Color.green(colorInt);
        int red = Color.blue(colorInt);

        return Color.argb(0, red, green, blue);
    }
}
