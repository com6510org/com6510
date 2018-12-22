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
    List<FotoData> initData;
    public MyViewModel (Application application) {
        super(application);
        // creation and connection to the Repository
        mRepository = new MyRepository(application);
        fotoDataToDisplay = mRepository.getFotoData();
    }


    /**
     * getter for the live data
     * @return
     */
    LiveData<FotoData> getFotoDataToDisplay() {
        if (fotoDataToDisplay == null) {
            fotoDataToDisplay = new MutableLiveData<FotoData>();
        }
        return fotoDataToDisplay;
    }

    /**
     * request by the UI to generate a new random number
     */
    public void generateNewFoto(String path) {
        mRepository.generateNewFoto(path);
    }
    public List<FotoData> getAllFoto(){return initData;}
    public void deleteAllElement(){
        mRepository.deletAll();
    }
}
