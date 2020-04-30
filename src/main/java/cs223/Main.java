package cs223;

import cs223.simulator.Configuration;
import cs223.simulator.Simulator;

public class Main {

    public static void main(String[] args) {
        Configuration.initialize();
        Simulator simulator = new Simulator(Configuration.getConfig());
        simulator.simulate();
    }
}
