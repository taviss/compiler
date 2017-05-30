package runtime.instructions;

public abstract class ExtFunc {
    protected Object arg;
    
    public ExtFunc(Object arg) {
        this.arg = arg;
    }
    
    public abstract void run();
}
