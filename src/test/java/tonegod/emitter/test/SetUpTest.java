package tonegod.emitter.test;

import com.jme3.app.SimpleApplication;
import org.jetbrains.annotations.NotNull;
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

    protected static synchronized @NotNull SimpleApplication getApplication() {

        if (application == null) {

            final SimpleApplication app = new SimpleApplication() {
                @Override
                public void simpleInitApp() {
                    COUNT_DOWN_LATCH.countDown();
                }
            };
            app.setShowSettings(false);

            final Thread appThread = new Thread(app::start);
            appThread.setDaemon(true);
            appThread.start();

            try {
                COUNT_DOWN_LATCH.await();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }

            application = app;
        }

        return application;
    }

    @BeforeAll
    public static void setUpLoaderTest() {
        getApplication();
    }
}
