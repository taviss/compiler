package runtime.instructions;

import java.nio.ByteBuffer;
import java.util.Scanner;

public class GetS extends ExtFunc {
    @Override
    public void run(ByteBuffer stack) {
        Scanner scan = new Scanner(System.in);
        String i = scan.nextLine();
        stack.put(i.getBytes());
    }

    @Override
    public void run(ByteBuffer stack, int offset) {

    }
}
