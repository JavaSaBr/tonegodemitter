package tonegod.emitter.test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.FlyByCamera;
import com.jme3.system.AppSettings;
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

            final AppSettings settings = new AppSettings(true);
            settings.setWidth(1024);
            settings.setHeight(768);

            final SimpleApplication app = new SimpleApplication() {
                @Override
                public void simpleInitApp() {

                    final FlyByCamera flyByCamera = getFlyByCamera();
                    flyByCamera.setMoveSpeed(5);
                    flyByCamera.setDragToRotate(true);

                    getInputManager().setCursorVisible(true);

                    COUNT_DOWN_LATCH.countDown();
                }
            };
            app.setShowSettings(false);
            app.setSettings(settings);

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
