package harmonization;

import model.RawDataRecord;
import model.HarmonizedData;

public interface IHarmonizer {
    HarmonizedData harmonize(RawDataRecord rawRecord);
}
