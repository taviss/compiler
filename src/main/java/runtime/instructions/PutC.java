package runtime.instructions;

import java.nio.ByteBuffer;

public class PutC extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        System.out.print(stack.getChar());
    }

    @Override
    public void run(ByteBuffer stack, int offset) {
        run(stack);
    }
}
