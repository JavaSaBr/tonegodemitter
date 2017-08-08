package tonegod.emitter.test;

import com.jme3.app.SimpleApplication;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;

/**
 * The base test.
 *
 * @author JavaSaBr
 */
public class SetUpTest {

    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(1);

    private static SimpleApplication application = null;

    private static synchronized SimpleApplication getApplication() {

        if (application == null) {

            application = new SimpleApplication() {
                @Override
                public void simpleInitApp() {
                    COUNT_DOWN_LATCH.countDown();
                }
            };
            application.setShowSettings(false);

            final Thread appthread = new Thread(() -> application.start());
            appthread.setDaemon(true);
            appthread.start();

            try {
                COUNT_DOWN_LATCH.await();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return application;
    }

    @BeforeAll
    public static void setUpLoaderTest() {
        getApplication();
    }
}
