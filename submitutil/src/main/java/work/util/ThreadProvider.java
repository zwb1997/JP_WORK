package work.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadProvider {
    private static int CORE_POOL_SIZE = 3;
    private static int MAX_MUN_POOL_SIZE = 6;
    private static long KEEP_ALIVE_TIME = 60;
    private static final LinkedBlockingDeque WORK_QUEUE = new LinkedBlockingDeque<>(12);
    private static ExecutorService service = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_MUN_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, WORK_QUEUE);
}
