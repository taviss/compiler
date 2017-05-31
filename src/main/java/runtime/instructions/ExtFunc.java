package runtime.instructions;

import java.nio.ByteBuffer;

public abstract class ExtFunc {
    public abstract void run(ByteBuffer stack);
    public abstract void run(ByteBuffer stack, int offset);
}
