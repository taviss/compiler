package syntax.analyzer;

/**
 * Created by octav on 4/12/2017.
 */
public class ConsumedResult {

    //TODO split this into double/long/stringResult
    private double doubleVal;
    private long longVal;
    private String stringVal;
    private boolean valid = false;

    public double getDoubleVal() {
        return doubleVal;
    }

    public void setDoubleVal(double doubleVal) {
        this.doubleVal = doubleVal;
    }

    public long getLongVal() {
        return longVal;
    }

    public void setLongVal(long longVal) {
        this.longVal = longVal;
    }

    public String getStringVal() {
        return stringVal;
    }

    public void setStringVal(String stringVal) {
        this.stringVal = stringVal;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void validate() {
        this.valid = true;
    }
}
