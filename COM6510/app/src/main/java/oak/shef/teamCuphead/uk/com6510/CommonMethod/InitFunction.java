package oak.shef.teamCuphead.uk.com6510.CommonMethod;

import android.app.Activity;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import oak.shef.teamCuphead.uk.com6510.model.FotoData;
import pl.aprilapps.easyphotopicker.EasyImage;

public class InitFunction {
    /**
     * Initial the EasyImage . Set the taken photos not to copy into public gallery app.
     * The app already add the taken photo from the cache.
     * Set the copy the picked images to the public Gallery is false.
     * The app already add the picked photo from the cache.
     * Allow gallery can be multiple picked.
     *
     * @param context which context activity the easyimage used.
     */

    public void initEasyImage(Context context) {
        EasyImage.configuration(context)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(false)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    /**
     * The initData is to initial the data if the user is the first time open the App
     * In this method will change the list of photo path to the FotoData.
     * The FotoDate includes:
     * <ul>
     * <li>The photo title
     * <li>The photo description
     * <li>The photo path
     * <li>The photo date
     * <li>The photo latitude
     * <li>The photo longitude
     * <li>The photo type
     * </ul>
     *
     * The title and description have default value "Add a title" and "Add a description"
     * The path is the the photo path.
     * The date is when the photo taken
     * The latitude and longitude is where the photo taken.
     *
     * In this method, the location can not added by the google map listener .
     * And the date can not added the same as system time.
     *
     * The type is 1.0(From garllery)
     * @param myPicturePath the picture path list what we get from the gallery
     * @return Return the list in FotoData which store into the Room
     */

    public List<FotoData> initData(List<String> myPicturePath) {
        List<FotoData> newList = new ArrayList<FotoData>();
        for (int i = 0; i < myPicturePath.size(); i++) {
            try {
                String path = myPicturePath.get(i);
                ExifInterface exif = new ExifInterface(path);
                String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

                double lat = score2dimensionality(latitude);
                double lon = score2dimensionality(longitude);
                if (latitudeRef != null && longitudeRef != null) {
                    if (latitudeRef.equals("S")) {
                        lat = -lat;
                    }
                    if (longitudeRef.equals("W")) {
                        lon = -lon;
                    }
                }
                newList.add(new FotoData("Add a title", "Add a description", myPicturePath.get(i), date, lat, lon, 2.0));
            } catch (Exception ee) {
                Log.i("Date", "Date or location does not exist");
            }
        }
        return newList;
    }

    public double score2dimensionality(String string) {
        double dimensionality = 0.0;
        if (null == string) {
            return dimensionality;
        }

        String[] split = string.split(",");
        for (int i = 0; i < split.length; i++) {

            String[] s = split[i].split("/");
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            dimensionality = dimensionality + v / Math.pow(60, i);
        }
        return dimensionality;
    }

    /**
     * This method is get the photo path from the gallery
     * uri is the path of the images and we need retrive them from the MediaStore
     * @param activity the activity which use this method
     * @return Return the list of path which photo stores in the gallery
     */

    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

}
