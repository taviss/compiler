package runtime.virtual.machine;

import domain.symbols.Symbol;
import runtime.instructions.ExtFunc;
import runtime.instructions.Instruction;
import runtime.instructions.InstructionException;
import runtime.instructions.Opcode;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Predicate;

import static runtime.instructions.Opcode.O_CALL;

public class VirtualMachine {
    private ByteBuffer stack;
    private LinkedList<Instruction> instructions;
    private static final int STACK_SIZE = 32 * 1024;
    public static final int INT_SIZE = 4;
    public static final int DOUBLE_SIZE = 8;
    
    public VirtualMachine() {
        this.instructions = new LinkedList<>();
        this.stack = ByteBuffer.allocate(STACK_SIZE);
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

    public Instruction addInstrA(Opcode opcode, Symbol symbol){
        Instruction instruction = new Instruction(opcode, symbol);
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
        Iterator<Instruction> instructionIterator = instructions.iterator();
        Instruction instruction = instructionIterator.next();
        int frameIndex = 0, oldFrameIndex;
        int oldStackIndex;
        LinkedList<Integer> stackSizes = new LinkedList<>();
        while(true) {
            //printf("%p/%d\t",IP,SP-stack);
            
            switch(instruction.getOpcode()){
                case O_CALL: {
                    aVal1 = instruction.getAddr(0);
                    //printf("CALL\t%p\n",aVal1);
                    //byte[] bytes = serializeObject(instructions.pop());
                    stack.putInt(instructions.indexOf(instructions.pop()));
                    //stackSizes.push(bytes.length);
                    instruction = aVal1;
                    break;
                }
                case O_CALLEXT: {
                    ExtFunc extFunc = instruction.getSymbol().getExtFunc();
                    extFunc.run(stack, instruction.getSymbol().getOffset());
                    instruction = instructionIterator.next();
                    break;
                }
                case O_CAST_I_D: {
                    iVal1=stack.getInt(stack.position() - INT_SIZE);
                    dVal1=(double)iVal1;
                    stack.putDouble(dVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_DROP: {
                    iVal1=instruction.getInt(0);
                    //removeBytesFrom(stack, iVal1);
                    stack.position(stack.position() - iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_ENTER: {
                    iVal1=instruction.getInt(0);
                    //byte[] obj = serializeObject(frameList);
                    stack.putInt(frameIndex);
                    //stackSizes.push(obj.length);
                    oldFrameIndex = frameIndex;
                    frameIndex = stack.position();
                    stack.position(stack.position() + iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_EQ_D: {
                    dVal1=stack.getDouble(stack.position() - DOUBLE_SIZE);
                    dVal2=stack.getDouble(stack.position() - DOUBLE_SIZE);
                    stack.putInt(dVal2==dVal1 ? 1 : 0);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_HALT:
                    return;
                case O_INSERT: {
                    iVal1=instruction.getInt(0);
                    iVal2=instruction.getInt(1);
                    if(stack.position() + iVal1 > STACK_SIZE) throw new InstructionException("Out of stack");
                    for(int i = stack.position()-iVal1; i < iVal1; i++) {
                        stack.put(i+iVal2, stack.get(i));
                    }
                    int j = 0;
                    for(int i = stack.position()+iVal2; i < iVal2; i++) {
                        stack.put(stack.position()-iVal1+j, stack.get(i));
                        j++;
                    }
                    stack.position(stack.position() + iVal2);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_JT_I: {
                    iVal1=stack.getInt(stack.position() - INT_SIZE);
                    instruction=iVal1 > 0 ? instruction.getAddr(0) : instructionIterator.next();
                    break;
                }
                case O_LOAD: {
                    iVal1=instruction.getInt(0);
                    /*
                    byte[] bytes1 = new byte[STACK_SIZE];
                    for(int i = 0; i < stackSizes.pop(); i++) {
                        bytes1[i] = stack.get();
                    }
                    aVal1=deserializeInstruction(bytes1);*/

                    int index = stack.getInt(stack.position() - INT_SIZE);

                    if(stack.position() + iVal1 > STACK_SIZE) throw new InstructionException("Out of stack");
                    for(int i = 0; i < iVal1; i++) {
                        stack.put(stack.position() + i, stack.get(index+i));
                    }
                    stack.position(stack.position() + iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_OFFSET: {
                    iVal1=stack.getInt(stack.position() - INT_SIZE);
                    /*
                    byte[] bytes2 = new byte[STACK_SIZE];
                    for(int i = 0; i < stackSizes.pop(); i++) {
                        bytes2[i] = stack.get();
                    }
                    aVal1=deserializeInstruction(bytes2);
                    */
                    int index = stack.getInt(stack.position() - INT_SIZE);
                    stack.putInt(index+iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_PUSHFPADDR: {
                    iVal1=instruction.getInt(0);
                    stack.putInt(frameIndex+iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_PUSHCT_A: {
                    aVal1=instruction.getAddr(0);
                    stack.putInt(instructions.indexOf(aVal1));
                    instruction = instructionIterator.next();
                    break;
                }
                case O_PUSHCT_I: {
                    iVal1=instruction.getInt(0);
                    stack.putInt(iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_PUSHCT_C: {
                    iVal1=instruction.getInt(0);
                    stack.putInt(iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_PUSHCT_D: {
                    dVal1=instruction.getDouble(0);
                    stack.putDouble(dVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_RET: {
                    iVal1=instruction.getInt(0);
                    iVal2=instruction.getInt(1);
                    oldStackIndex=stack.position();
                    stack.position(frameIndex);
                    frameIndex = stack.getInt(stack.position() - INT_SIZE);
                    instruction = instructions.get(stack.getInt(stack.position() - INT_SIZE));
                    if(stack.position()-iVal1 < 0) throw new RuntimeException("STACK ERROR");
                    stack.position(stack.position()-iVal1);
                    for(int i = 0; i < iVal2; i++) {
                        stack.put(stack.position()+i, stack.get(oldStackIndex-iVal2));
                    }
                    stack.position(stack.position() + iVal2);
                    break;
                }
                case O_STORE: {
                    iVal1=instruction.getInt(0);
                    int index = stack.position() - iVal1;
                    //if(SP-(sizeof(void*)+iVal1)<stack)err("not enough stack bytes for SET");
                    int oldPos = stack.position();
                    stack.position(index);
                    for(int i = 0; i < iVal1; i++) {
                        stack.put(stack.position()+i, stack.get(oldPos-iVal1+i));
                    }
                    stack.position(iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_SUB_D: {
                    dVal1 = stack.getDouble(stack.position() - DOUBLE_SIZE);
                    dVal2 = stack.getDouble(stack.position() - DOUBLE_SIZE);
                    stack.putDouble(dVal2 - dVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                case O_SUB_I: {
                    iVal1 = stack.getInt(stack.position() - INT_SIZE);
                    iVal2 = stack.getInt(stack.position() - INT_SIZE);
                    stack.putInt(iVal2 - iVal1);
                    instruction = instructionIterator.next();
                    break;
                }
                default:
                    throw new InstructionException("Unknown opcode");
            }
        }
    }

    public void removeBytesFrom(ByteBuffer bf, int n) {
        int initialPos = bf.position();
        for(int i = bf.position(); i > n; i--) {
            bf.put(i, (byte)0);
        }
        bf.position(initialPos - n);
    }

    public byte[] serializeObject(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(byteArrayOutputStream);
            out.writeObject(object);
            out.flush();
            return byteArrayOutputStream.toByteArray();
        } catch(IOException e) {
            System.out.println("Ser gone bad");
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    public Instruction deserializeInstruction(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(byteArrayInputStream);
           return (Instruction) in.readObject();
        } catch(IOException|ClassNotFoundException e) {
            System.out.println("ERROR");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

}
