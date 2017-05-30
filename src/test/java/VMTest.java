import org.junit.Before;
import org.junit.Test;
import runtime.instructions.Instruction;
import runtime.virtual.machine.VirtualMachine;
import static runtime.instructions.Opcode.*;

public class VMTest {
    private VirtualMachine virtualMachine;
    
    @Before
    public void setUp() {
        virtualMachine = new VirtualMachine();
    }
    
    @Test
    void VMtest()
    {
        Instruction L1;
        Instruction v = virtualMachine.addInstrI(O_PUSHCT_I,3);
        virtualMachine.addInstrI(O_STORE, Integer.SIZE));
        L1=virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrI(O_LOAD, Integer.SIZE);
        virtualMachine.addInstrA(O_CALLEXT,requireSymbol(&symbols,"put_i")->addr);
        virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrI(O_LOAD, Integer.SIZE);
        virtualMachine.addInstrI(O_PUSHCT_I,1);
        virtualMachine.addInstr(O_SUB_I);
        virtualMachine.addInstrI(O_STORE, Integer.SIZE);
        virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrI(O_LOAD, Integer.SIZE);
        virtualMachine.addInstrA(O_JT_I,L1);
        virtualMachine.addInstr(O_HALT);
    }

}
