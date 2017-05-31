package runtime.instructions;

import runtime.virtual.machine.VirtualMachine;

import java.nio.ByteBuffer;

public class PutD extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        double out = stack.getDouble(stack.position() - VirtualMachine.DOUBLE_SIZE);
        System.out.print(out);
    }

    @Override
    public void run(ByteBuffer stack, int offset) {
        run(stack);
    }
}
