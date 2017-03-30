package com.evilcorp.firebaseintegration.helper;

import java.io.ByteArrayOutputStream;

/**
 * Created by hristo.stoyanov on 22-Feb-17.
 */

public class BitmapHelper {
    public static byte[] resizeBitmap(String photoPath, int targetW, int targetH) {
        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(newBitmap,targetW,targetH,false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
