package runtime.instructions;

import java.nio.ByteBuffer;
import java.util.Scanner;

public class GetC extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        Scanner scan = new Scanner(System.in);
        char i = scan.nextLine().charAt(0);
        stack.putChar(i);
    }

    @Override
    public void run(ByteBuffer stack, int offset) {

    }
}
