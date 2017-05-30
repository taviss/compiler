package runtime.virtual.machine;

import runtime.instructions.Instruction;
import runtime.instructions.Opcode;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

import static runtime.instructions.Opcode.O_CALL;

public class VirtualMachine {
    private ByteBuffer stack;
    private LinkedList<Instruction> instructions;
    
    public VirtualMachine() {
        this.instructions = new LinkedList<>();
    }
    
    public LinkedList<Instruction> getInstructions() {
        return this.instructions;
    }

    
    public void insertInstrAfter(Instruction after, Instruction instruction) {
        int index = instructions.indexOf(after) + 1;
        instructions.add(index, instruction);
    }

    public Instruction addInstr(Opcode opcode)
    {
        Instruction instruction = new Instruction(opcode);
        instructions.add(instruction);
        return instruction;
    }

    public Instruction addInstrAfter(Instruction after, Opcode opcode)
    {
        Instruction instruction = new Instruction(opcode);
        insertInstrAfter(after, instruction);
        return instruction;
    }
    
    public Instruction addInstrA(Opcode opcode, Instruction addr){
        Instruction instruction = new Instruction(opcode, addr);
        instructions.add(instruction);
        return instruction;
    }

    public Instruction addInstrI(Opcode opcode, int val){
        Instruction instruction = new Instruction(opcode, val);
        instructions.add(instruction);
        return instruction;
    }

    public Instruction addInstrII(Opcode opcode, int val1, int val2){
        Instruction instruction = new Instruction(opcode, val1, val2);
        instructions.add(instruction);
        return instruction;
    }
    
    public void deleteInstructionsAfter(Instruction start) {
        instructions.removeIf(new Predicate<Instruction>() {
            @Override
            public boolean test(Instruction instruction) {
                return instructions.indexOf(instruction) > instructions.indexOf(start);
            }
        });
    }

    public void run() {
        int iVal1,iVal2;
        double dVal1,dVal2;
        Instruction aVal1;
        while(true){
            //printf("%p/%d\t",IP,SP-stack);
            Instruction instruction = instructions.pop();
            switch(instruction.getOpcode()){
                case O_CALL:
                    aVal1=instruction.getAddr(0);
                    //printf("CALL\t%p\n",aVal1);
                    pusha(IP->next);
                    IP=(Instr*)aVal1;
                    break;
                case O_CALLEXT:
                    printf("CALLEXT\t%p\n",IP->args[0].addr);
                    (*(void(*)())IP->args[0].addr)();
                    IP=IP->next;
                    break;
                case O_CAST_I_D:
                    iVal1=popi();
                    dVal1=(double)iVal1;
                    printf("CAST_I_D\t(%ld -> %g)\n",iVal1,dVal1);
                    pushd(dVal1);
                    IP=IP->next;
                    break;
                case O_DROP:
                    iVal1=IP->args[0].i;
                    printf("DROP\t%ld\n",iVal1);
                    if(SP-iVal1<stack)err("not enough stack bytes");
                    SP-=iVal1;
                    IP=IP->next;
                    break;
                case O_ENTER:
                    iVal1=IP->args[0].i;
                    printf("ENTER\t%ld\n",iVal1);
                    pusha(FP);
                    FP=SP;
                    SP+=iVal1;
                    Laborator Limbaje formale si tehnici de compilare, Universitatea Politehnica Timisoara. © Aciu Razvan Mihai
                        IP=IP->next;
                    break;
                case O_EQ_D:
                    dVal1=popd();
                    dVal2=popd();
                    printf("EQ_D\t(%g==%g -> %ld)\n",dVal2,dVal1,dVal2==dVal1);
                    pushi(dVal2==dVal1);
                    IP=IP->next;
                    break;
                case O_HALT:
                    printf("HALT\n");
                    return;
                case O_INSERT:
                    iVal1=IP->args[0].i; // iDst
                    iVal2=IP->args[1].i; // nBytes
                    printf("INSERT\t%ld,%ld\n",iVal1,iVal2);
                    if(SP+iVal2>stackAfter)err("out of stack");
                    memmove(SP-iVal1+iVal2,SP-iVal1,iVal1); //make room
                    memmove(SP-iVal1,SP+iVal2,iVal2); //dup
                    SP+=iVal2;
                    IP=IP->next;
                    break;
                case O_JT_I:
                    iVal1=popi();
                    printf("JT\t%p\t(%ld)\n",IP->args[0].addr,iVal1);
                    IP=iVal1?IP->args[0].addr:IP->next;
                    break;
                case O_LOAD:
                    iVal1=IP->args[0].i;
                    aVal1=popa();
                    printf("LOAD\t%ld\t(%p)\n",iVal1,aVal1);
                    if(SP+iVal1>stackAfter)err("out of stack");
                    memcpy(SP,aVal1,iVal1);
                    SP+=iVal1;
                    IP=IP->next;
                    break;
                case O_OFFSET:
                    iVal1=popi();
                    aVal1=popa();
                    printf("OFFSET\t(%p+%ld -> %p)\n",aVal1,iVal1,aVal1+iVal1);
                    pusha(aVal1+iVal1);
                    IP=IP->next;
                    break;
                case O_PUSHFPADDR:
                    iVal1=IP->args[0].i;
                    printf("PUSHFPADDR\t%ld\t(%p)\n",iVal1,FP+iVal1);
                    pusha(FP+iVal1);
                    IP=IP->next;
                    break;
                case O_PUSHCT_A:
                    aVal1=IP->args[0].addr;
                    printf("PUSHCT_A\t%p\n",aVal1);
                    pusha(aVal1);
                    IP=IP->next;
                    break;
                case O_RET:
                    iVal1=IP->args[0].i; // sizeArgs
                    Laborator Limbaje formale si tehnici de compilare, Universitatea Politehnica Timisoara. © Aciu Razvan Mihai
                        iVal2=IP->args[1].i; // sizeof(retType)
                    printf("RET\t%ld,%ld\n",iVal1,iVal2);
                    oldSP=SP;
                    SP=FP;
                    FP=popa();
                    IP=popa();
                    if(SP-iVal1<stack)err("not enough stack bytes");
                    SP-=iVal1;
                    memmove(SP,oldSP-iVal2,iVal2);
                    SP+=iVal2;
                    break;
                case O_STORE:
                    iVal1=IP->args[0].i;
                    if(SP-(sizeof(void*)+iVal1)<stack)err("not enough stack bytes for SET");
                    aVal1=*(void**)(SP-((sizeof(void*)+iVal1)));
                    printf("STORE\t%ld\t(%p)\n",iVal1,aVal1);
                    memcpy(aVal1,SP-iVal1,iVal1);
                    SP-=sizeof(void*)+iVal1;
                    IP=IP->next;
                    break;
                case O_SUB_D:
                    dVal1=popd();
                    dVal2=popd();
                    printf("SUB_D\t(%g-%g -> %g)\n",dVal2,dVal1,dVal2-dVal1);
                    pushd(dVal2-dVal1);
                    IP=IP->next;
                    break;
                default:
                    err("invalid opcode: %d",IP->opcode);
            }
        }
    }

}
