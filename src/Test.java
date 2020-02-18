import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class Test {

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.setMaximumPoolSize(5);

        int size = 20;
        while(size > 0) {
            if (executor.getActiveCount() < executor.getMaximumPoolSize()) {
                try {
                    executor.execute(new TestRunnable(size - 1));
                    size--;
                } catch (RejectedExecutionException ex) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex1) {

                    }
                }
            }
//            System.out.println("Active Count: " + executor.getActiveCount() + " Max Pool Size: " + executor.getMaximumPoolSize());
        }

        executor.shutdown();

    }

    public static class TestRunnable implements Runnable {
        int threadCount;

        public TestRunnable(int threadCount) {
            this.threadCount = threadCount;
        }

        @Override
        public void run() {
            System.out.println("Thread: " + threadCount + " started.");
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {

            }
            System.out.println("Thread: " + threadCount + " finished.");
        }
    }
}
