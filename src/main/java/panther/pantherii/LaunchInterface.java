package panther.pantherii;

public class LaunchInterface extends Thread {
    private String[] args;
    public LaunchInterface(String[] args) {
        this.args = args;
    }
    @Override
    public void run() {
        PantherInterface.main(args);
    }
}
