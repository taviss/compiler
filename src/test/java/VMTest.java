import domain.analyzer.DomainAnalyzer;
import org.junit.Before;
import org.junit.Test;
import runtime.instructions.Instruction;
import runtime.virtual.machine.VirtualMachine;
import syntax.analyzer.SyntaxAnalyzer;
import token.Token;

import java.util.ArrayList;
import java.util.List;

import static runtime.instructions.Opcode.*;

public class VMTest {
    private VirtualMachine virtualMachine;
    private DomainAnalyzer domainAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;
    
    @Before
    public void setUp() {
        virtualMachine = new VirtualMachine();
        domainAnalyzer = new DomainAnalyzer();
        syntaxAnalyzer = new SyntaxAnalyzer(new ArrayList<Token>());
        domainAnalyzer.addExtFuncs();
    }
    
    @Test
    public void VMtest()
    {
        int v = virtualMachine.getInstructions().indexOf(virtualMachine.addInstrI(O_PUSHCT_I,3));
        virtualMachine.addInstrI(O_STORE, VirtualMachine.INT_SIZE);
        int l1 = virtualMachine.getInstructions().indexOf(virtualMachine.addInstrA(O_PUSHCT_A,v));
        virtualMachine.addInstrI(O_LOAD, VirtualMachine.INT_SIZE);
        virtualMachine.addInstrA(O_CALLEXT, domainAnalyzer.requireSymbol("put_i"));
        virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrI(O_LOAD, VirtualMachine.INT_SIZE);
        virtualMachine.addInstrI(O_PUSHCT_I,1);
        virtualMachine.addInstr(O_SUB_I);
        virtualMachine.addInstrI(O_STORE, VirtualMachine.INT_SIZE);
        virtualMachine.addInstrA(O_PUSHCT_A,v);
        virtualMachine.addInstrI(O_LOAD, VirtualMachine.INT_SIZE);
        virtualMachine.addInstrA(O_JT_I,l1);
        virtualMachine.addInstr(O_HALT);
        virtualMachine.run();
    }

}
