package oak.shef.teamCuphead.uk.com6510;

import java.util.List;

import oak.shef.teamCuphead.uk.com6510.database.FotoData;

public interface AsyncResponse {
    void processFinish(List<FotoData> output);
}
