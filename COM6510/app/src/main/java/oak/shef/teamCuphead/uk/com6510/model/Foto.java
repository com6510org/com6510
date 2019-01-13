package oak.shef.teamCuphead.uk.com6510.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * This is the model data type for the photo which used to generate the cluster item.
 * The ClusterItem is the google map API provide. Which used to generate the cluster marker on
 * the google map. The ClusterItem is to generate the cluster markers.
 * Use the Clustering marker ,to change the data from Fotodata to Foto is necessary.
 */
public class Foto implements ClusterItem {
    public final String title;
    public final String description;
    public final Bitmap profilePhoto;
    private final LatLng mPosition;

    /**
     * Class constructor.
     *
     * @param position        The photo position where it was taken
     * @param title           The title of the photo and show in the info window
     * @param pictureResource The bitmap for the picture
     * @param des             The description of the photo and show in the info window
     */
    public Foto(LatLng position, String title, Bitmap pictureResource, String des) {
        this.title = title;
        profilePhoto = pictureResource;
        mPosition = position;
        description = des;
    }

    @Override
    /**
     * This method used to get the position.
     * @return return the photo position
     */
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    /**
     * This method used to get the title.
     * @return return the photo title
     */
    public String getTitle() {
        return null;
    }

    @Override
    /**
     * This method used to get the snippet.
     * @return return the photo snippet
     */
    public String getSnippet() {
        return null;
    }
}