package runtime.instructions;

import java.util.ArrayList;

public class Instruction {
    private Opcode opcode;
    private Object[] rawValues;
    
    public Instruction() {
        rawValues = new Object[2];
    }
    
    public Instruction(Opcode opcode) {
        this();
        this.opcode = opcode;
    }
    
    public Instruction(Opcode opcode, Object value1) {
        this(opcode);
        this.rawValues[0] = value1;
    }

    public Instruction(Opcode opcode, Object value1, Object value2) {
        this(opcode, value1);
        this.rawValues[1] = value1;
    }
    
    public ExtFunc getExtFunc() {
        if(rawValues[0] instanceof ExtFunc) {
            return (ExtFunc) rawValues[0];
        }
        throw new InstructionException("No ExtFunc found");
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public void setOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    public Object[] getRawValues() {
        return rawValues;
    }

    public void setRawValues(Object[] rawValues) {
        this.rawValues = rawValues;
    }
    
    public void setFirstArg(Object object) {
        this.rawValues[0] = object;
    }

    public void setSecondArg(Object object) {
        this.rawValues[1] = object;
    }
    
    public int getInt(int arg) {
        if(rawValues[arg] instanceof Integer) {
            return (int) rawValues[arg];
        }
        throw new InstructionException(opcode + " doesn't have an integer value.");
    }

    public double getDouble(int arg) {
        if(rawValues[arg] instanceof Double) {
            return (double) rawValues[arg];
        }
        throw new InstructionException(opcode + " doesn't have a double value.");
    }

    public Instruction getAddr(int arg) {
        if(rawValues[arg] instanceof Instruction) {
            return (Instruction) rawValues[arg];
        }
        throw new InstructionException(opcode + " doesn't have an address value.");
    }
}
