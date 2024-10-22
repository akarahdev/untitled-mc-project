package dev.akarah.util;

public class ExceptionUtils {
    public interface ExceptionableRunnable {
        public void run() throws Exception;
    }

    public static void moveToRuntime(ExceptionableRunnable exceptionableRunnable) {
        try {
            exceptionableRunnable.run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface ExceptionableSupplier<T> {
        public T supply() throws Exception;
    }
    public static<T> T moveToRuntime(ExceptionableSupplier<T> exceptionableRunnable) {
        try {
            return exceptionableRunnable.supply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
