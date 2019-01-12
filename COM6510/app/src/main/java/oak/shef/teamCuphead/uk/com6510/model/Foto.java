package oak.shef.teamCuphead.uk.com6510.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Foto implements ClusterItem {
    public final String title;
    public final String description;
    public final Bitmap profilePhoto;
    private final LatLng mPosition;

    public Foto(LatLng position, String title, Bitmap pictureResource,String des) {
        this.title = title;
        profilePhoto = pictureResource;
        mPosition = position;
        description=des;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}