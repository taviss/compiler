package runtime.instructions;

import java.nio.ByteBuffer;
import java.util.Scanner;

public class GetI extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        Scanner scan = new Scanner(System.in);
        int i = Integer.parseInt(scan.nextLine());
        stack.putInt(i);
    }

    @Override
    public void run(ByteBuffer stack, int offset) {

    }
}
