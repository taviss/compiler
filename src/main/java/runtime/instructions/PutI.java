package runtime.instructions;

import runtime.virtual.machine.VirtualMachine;

import java.nio.ByteBuffer;

public class PutI extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        int out = stack.getInt(stack.position() - VirtualMachine.INT_SIZE);
        System.out.print(out);
    }

    @Override
    public void run(ByteBuffer stack, int offset) {
        run(stack);
    }
}
