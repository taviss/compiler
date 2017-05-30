package runtime.instructions;

public class Put extends ExtFunc {

    public Put(Object arg) {
        super(arg);
    }

    @Override
    public void run() {
        System.out.print(super.arg);
    }
}
