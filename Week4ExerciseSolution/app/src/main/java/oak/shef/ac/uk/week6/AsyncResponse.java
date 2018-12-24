package oak.shef.ac.uk.week6;

import java.util.List;

import oak.shef.ac.uk.week6.database.FotoData;

public interface AsyncResponse {
    void processFinish(List<FotoData> output);
}
