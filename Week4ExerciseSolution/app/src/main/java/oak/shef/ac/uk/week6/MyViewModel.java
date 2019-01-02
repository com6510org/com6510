package oak.shef.ac.uk.week6;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.List;

import oak.shef.ac.uk.week6.database.FotoData;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository mRepository;
    LiveData<FotoData> fotoDataToDisplay;
    public MyViewModel (Application application) {
        super(application);
        // creation and connection to the Repository
        mRepository = new MyRepository(application);

    }



    LiveData<FotoData> getFotoDataToDisplay(String path) {
        fotoDataToDisplay = mRepository.getFoto(path);
        if (fotoDataToDisplay == null) {
            fotoDataToDisplay = new MutableLiveData<FotoData>();
        }
        return fotoDataToDisplay;
    }

    /**
     * request by the UI to generate a new random number
     */
    public void generateNewFoto(List<FotoData> list) {
        mRepository.generateNewFoto(list);

    }

   /* public void generateNewFoto(String path,String date,String latitude,String longitude) {
        mRepository.generateNewFoto(path,date,latitude,longitude);
    }
*/


    public void deleteAllElement(){
        mRepository.deletAll();
    }


    public void getAllPhotos(AsyncResponse resp, List<String> myPicturePath)
    {
        mRepository.getAllPhotos(resp, myPicturePath);
    }
}
