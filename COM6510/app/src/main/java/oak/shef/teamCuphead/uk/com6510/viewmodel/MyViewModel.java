package oak.shef.teamCuphead.uk.com6510.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.Location;

import java.util.List;

import oak.shef.teamCuphead.uk.com6510.CommonMethod.LocationUpdateFunction;
import oak.shef.teamCuphead.uk.com6510.database.AsyncResponse;
import oak.shef.teamCuphead.uk.com6510.model.FotoData;
import oak.shef.teamCuphead.uk.com6510.CommonMethod.InitFunction;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository mRepository;
    private final InitFunction initFunction;
    private final LocationUpdateFunction locationUpdateFunction;
    LiveData<FotoData> fotoDataToDisplay;
    public MyViewModel(Application application) {
        super(application);
        // creation and connection to the Repository
        mRepository = new MyRepository(application);
        initFunction = new InitFunction();

        locationUpdateFunction = new LocationUpdateFunction() ;
    }


    /**
     * gets the path
     * @param path the path from the foto
     * @return
     */
    public LiveData<FotoData> getFotoDataToDisplay(String path) {
        fotoDataToDisplay = mRepository.getFoto(path);
        if (fotoDataToDisplay == null) {
            fotoDataToDisplay = new MutableLiveData<FotoData>();
        }
        return fotoDataToDisplay;
    }

    /**
     * calls a function in the repository to create the new photos
     * @param list
     */
    public void generateNewFoto(List<FotoData> list) {
        mRepository.generateNewFoto(list);

    }




    public void deleteAllElement(){
        mRepository.deleteAll();
    }

    /**
     * gets all the data from the database to display in the view
     * @param resp async response
     * @param myPicturePath paths of all the photos of the gallery
     */
    public void getAllPhotos(AsyncResponse resp, List<String> myPicturePath)
    {
        mRepository.getAllPhotos(resp, myPicturePath);

    }

    public void searchIt(String title, String desc, String date, AsyncResponse resp)
    {
        mRepository.searchAll(title,  desc,  date, resp);

    }

    public void initFunction(Context context){
        initFunction.initEasyImage(context);
    }
    public List<String> getImagesPath(Activity activity){
        return initFunction.getImagesPath(activity);
    }

    public List<FotoData> initData(List<String> myPicturePath){
       return initFunction.initData(myPicturePath);
    }
    public double score2dimensionality(String string){
        return initFunction.score2dimensionality(string);
    }
    public void startLocationUpdates(Context context,Activity activity){
        locationUpdateFunction.startLocationUpdates(context,activity);
    }
    public void stopLocationUpdates(){
        locationUpdateFunction.stopLocationUpdates();
    }
    public Location returnMyLocation(){
        return locationUpdateFunction.ReturnMyCurrentLocation();
    }
}
