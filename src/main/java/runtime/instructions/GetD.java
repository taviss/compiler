package runtime.instructions;

import java.nio.ByteBuffer;
import java.util.Scanner;

public class GetD extends ExtFunc{
    @Override
    public void run(ByteBuffer stack) {
        Scanner scan = new Scanner(System.in);
        double i = Double.parseDouble(scan.nextLine());
        stack.putDouble(i);
    }

    @Override
    public void run(ByteBuffer stack, int offset) {

    }
}
