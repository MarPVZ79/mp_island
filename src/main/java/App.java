import com.javarush.island.config.SimulationConfig;
import com.javarush.island.simulation.MultithreadedSimulation;
import com.javarush.island.simulation.SimpleSimulation;

public class App {
    public static void main(String[] args) {
        SimulationConfig config = SimulationConfig.builder();
        System.out.println("Остров версия " + config.getVersion());
        // Однопоточная симуляция
        if (config.getThreads() == 0) {
            System.out.println("Работа в однопоточном режиме");
            SimpleSimulation simpleSimulation = new SimpleSimulation(config);
            simpleSimulation.initialize();
            try {
                simpleSimulation.run(config.getInitSimulationTick());
            } catch (InterruptedException e) {
                System.out.println("Ошибка при работе simpleSimulation");
                throw new RuntimeException(e);
            }
        } else {
            // Многопоточная симуляция
            System.out.println("Работа в многопоточном режиме, число потоков = " + config.getThreads());
            MultithreadedSimulation multithreadedSimulation = new MultithreadedSimulation(config);
            multithreadedSimulation.initialize();

            multithreadedSimulation.start();

           try {
             Thread.sleep(3000);
           } catch (InterruptedException e) {
             throw new RuntimeException(e);
           }
           multithreadedSimulation.stop();
        }
        System.out.println("Работа Симуляции завершена!");
    }
}