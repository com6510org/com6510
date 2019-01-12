package oak.shef.teamCuphead.uk.com6510.database;

import java.util.List;

import oak.shef.teamCuphead.uk.com6510.model.FotoData;

public interface AsyncResponse {
    void processFinish(List<FotoData> output);
}
