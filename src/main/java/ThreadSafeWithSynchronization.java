import com.sun.tools.javac.Main;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ThreadSafeWithSynchronization {

    private static Logger LOGGER;
    static {
        try(FileInputStream fileInputStream = new FileInputStream("src/main/resources/logparameters.txt")) {

            LogManager.getLogManager().readConfiguration(fileInputStream);
            LOGGER = Logger.getLogger(Main.class.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final List<Integer> randoms = new ArrayList<>();

    private final Thread producer = new Thread(()->
    {
        while (true) {
            synchronized (randoms) {
                for (int i = 0; i < new Random().nextInt(100); i++) {
                    int number = new Random().nextInt(100);
                    LOGGER.info(" Wrote: " + number);
                    randoms.add(number);
                }
                randoms.notifyAll();
                try {
                    randoms.wait();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE,"Error: ", e);
                    e.printStackTrace();
                }
            }
        }

    });

    private final Thread consumer = new Thread(()->
    {
        while (true) {
            synchronized (randoms) {
                for (Integer integer : randoms) {
                    LOGGER.info("Deleted: " + integer);
                }
                randoms.clear();
                randoms.notifyAll();
                try {
                    randoms.wait();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE,"Error: ", e);
                    e.printStackTrace();
                }
            }
        }
    });

    public void start(){
        producer.start();
        consumer.start();
    }
}