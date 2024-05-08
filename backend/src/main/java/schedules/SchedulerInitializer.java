package schedules;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import shared.session.SimpleSessionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class SchedulerInitializer implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newScheduledThreadPool(1);

        // Schedule a task to run every 1 day
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Clearing expired sessions...");
            SimpleSessionManager.getInstance().removeExpiredSessions();
            System.out.println("Cleared expired sessions");
        }, 0, 1, TimeUnit.DAYS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
