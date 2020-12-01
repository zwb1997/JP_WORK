package work.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import work.model.DefaultNamedFactory;

/**
 * @author x
 */
@Component("ThreadProvider")
public class ThreadProvider {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadProvider.class);
    private static final int CORE_POOL_SIZE = 40;
    private static final int MAX_CORE_POOL_SIZE = 50;
    private static final long KEEP_ALIVE_TIME = 6 * 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final int BLOCKING_DEQUE_SIZE = 100;
    private static final LinkedBlockingDeque<Runnable> BLOCKING_DEQUE = new LinkedBlockingDeque<>(BLOCKING_DEQUE_SIZE);
    private static final ThreadFactory NAMED_FACTORY = new DefaultNamedFactory();
    private static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAX_CORE_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, BLOCKING_DEQUE, NAMED_FACTORY,
            // reject handler
            (Runnable r, ThreadPoolExecutor executor) -> {
                int onWorkingSize = executor.getActiveCount();
                int existsSize = BLOCKING_DEQUE.size();
                int remainingSize = BLOCKING_DEQUE.remainingCapacity();
                LOG.warn("detect rejected strategy >>on working size :{}\twating in queue size :{}\tremaining size :{}",
                        onWorkingSize, existsSize, remainingSize);
                throw new RejectedExecutionException("execution queue is full");
            });

    static {
        EXECUTOR_SERVICE.allowCoreThreadTimeOut(true);
    }

    /**
     * submit task
     * 
     * if active task counts achive > MAX_CORE_POOL_SIZE + BLOCKING_DEQUE_SIZE ,work
     * will not submit and wating
     * 
     * @param task
     * @return
     */
    public static boolean submitTask(Runnable task) {
        boolean flag = false;
        int activeWorkCounts = EXECUTOR_SERVICE.getActiveCount();
        int largetPoolCounts = EXECUTOR_SERVICE.getLargestPoolSize();
        long taskCounts = EXECUTOR_SERVICE.getTaskCount();
        long completeTaskCounts = EXECUTOR_SERVICE.getCompletedTaskCount();

        LOG.info(
                "core_pool_size :{}\tmax_pool_size :{}\tblocking_queue_size :{}\tactive_task_counts :{}\tappear_larget_counts :{}\tapproximate_done_task_counts :{}\tcomplete_task_counts :{}",
                CORE_POOL_SIZE, MAX_CORE_POOL_SIZE, BLOCKING_DEQUE_SIZE, activeWorkCounts, largetPoolCounts, taskCounts,
                completeTaskCounts);
        try {
            EXECUTOR_SERVICE.submit(task);
            flag = true;
        } catch (Exception e) {
            LOG.error("submit task occure error , message :{}", e);
        }
        return flag;
    }
}
