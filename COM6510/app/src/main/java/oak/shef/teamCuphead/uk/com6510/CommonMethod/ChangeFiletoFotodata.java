package oak.shef.teamCuphead.uk.com6510.CommonMethod;

import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oak.shef.teamCuphead.uk.com6510.model.FotoData;

public class ChangeFileToFotodata {
    private InitFunction initFunction=new InitFunction();
    public List<FotoData> getFotoData(List<File> returnedPhotos,Location mylocation) {

        List<FotoData> imageElementList = new ArrayList<>();

        for (File file : returnedPhotos) {
            try {
                String path = file.getAbsolutePath();
                ExifInterface exif = new ExifInterface(path);
                String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
                String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                double lat = initFunction.score2dimensionality(latitude);
                double lon = initFunction.score2dimensionality(longitude);
                if (latitudeRef != null && longitudeRef != null ) {
                    if (latitudeRef.equals("S")) {
                        lat = -lat;
                    }
                    if (longitudeRef.equals("W")) {
                        lon = -lon;
                    }
                } else {
                    lat = mylocation.getLatitude();
                    lon = mylocation.getLongitude();
                }
                if(date==null){
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    Date fotodate = new Date(System.currentTimeMillis());
                    date = simpleDateFormat.format(fotodate);
                }
                imageElementList.add(new FotoData("Add a title", "Add a description", path, date, lat, lon,1.0));
            } catch (Exception ee) {
                Log.i("Date", "Date or location does not exist");
            }
        }

        return imageElementList;
    }
}
