package type.analyzer;

import domain.symbols.Type;

/**
 * Created by octav on 4/26/2017.
 */
public class ReturnValue {
    private Type type;
    private boolean isLVal;
    private boolean isCtVal;
    private Object constantValue;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isLVal() {
        return isLVal;
    }

    public void setLVal(boolean LVal) {
        isLVal = LVal;
    }

    public boolean isCtVal() {
        return isCtVal;
    }

    public void setCtVal(boolean ctVal) {
        isCtVal = ctVal;
    }

    public Object getConstantValue() {
        return constantValue;
    }

    public void setConstantValue(Object constantValue) {
        this.constantValue = constantValue;
    }
}
