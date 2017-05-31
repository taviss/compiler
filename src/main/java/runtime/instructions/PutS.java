package runtime.instructions;

import java.nio.ByteBuffer;

public class PutS extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        System.out.print(stack.get());
    }

    @Override
    public void run(ByteBuffer stack, int offset) {
        char[] chars = new char[100];
        stack.position(stack.position() - offset);
        int i = 0;
        while(offset > 0) {
            chars[i] = stack.getChar();
            i++;
        }
        System.out.print(chars.toString());
    }
}
