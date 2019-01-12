package oak.shef.teamCuphead.uk.com6510.CommonMethod;

import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oak.shef.teamCuphead.uk.com6510.CommonMethod.InitFunction;
import oak.shef.teamCuphead.uk.com6510.model.FotoData;

public class StoreIntoRoom {
    private InitFunction initFunction=new InitFunction();
    public List<FotoData> storeIntoRoom(String path, Location myLocation) {


        List<FotoData> newList = new ArrayList<>();
        Log.i("CheckPoint", "  !100011!  ");
        Log.i("CheckPoint", "  !100011!  ");
        try {
            ExifInterface exif = new ExifInterface(path);
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            Log.i("CheckPoint",  latitude+"  !100012!  ");
            Log.i("CheckPoint", longitude+"  !100012!  ");
            double lat = initFunction.score2dimensionality(latitude);
            Log.i("CheckPoint",  "  !100012!  ");
            double lon = initFunction.score2dimensionality(longitude);
            if (latitudeRef != null && longitudeRef != null) {
                if (latitudeRef.equals("S")) {
                    lat = -lat;
                }
                if (longitudeRef.equals("W")) {
                    lon = -lon;
                }
            } else {
                lat = myLocation.getLatitude();
                lon = myLocation.getLongitude();
                Log.i("CheckPoint", lat + "  !1000!  ");
                Log.i("CheckPoint", lon + "  !1000!  ");
            }
            if(date==null){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                Date fotodate = new Date(System.currentTimeMillis());
                date = simpleDateFormat.format(fotodate);
            }
            newList.add(new FotoData("Add a title", "Add a description", path, date, lat, lon,1.0));

        } catch (Exception ee) {
            Log.i("Date", "date or location is not exist");
        }
        return newList;

    }
}
