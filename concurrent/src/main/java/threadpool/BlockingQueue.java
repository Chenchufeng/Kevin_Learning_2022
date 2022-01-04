package threadpool;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: Kevin
 * @Date: 2022/1/4 21:55
 * @Description: 自定义线程池
 */
class ThreadPool{
    //任务队列
    private BlockingQueue<Runnable> taskQueue;
    //线程集合
    private HashSet<Worker> workers=new HashSet<Worker>();
    //核心线程数
    private int coreSize;
    //获取任务的超时时间
    private long timeOut;

    private TimeUnit timeUnit;

    //执行任务
    public void execute(Runnable task){
        //当任务数没有超过核心线程数，直接交给worker对象执行
        //如果任务数超过coreSize，加入任务队列暂存
        synchronized (workers){
            if (workers.size()<coreSize){
                Worker worker = new Worker(task);
                workers.add(worker);
                worker.start();
            }else {
                taskQueue.put(task);
            }
        }
    }

    public ThreadPool(int coreSize, long timeOut, TimeUnit timeUnit,int queueCapacity) {
        this.coreSize = coreSize;
        this.timeOut = timeOut;
        this.timeUnit = timeUnit;
        this.taskQueue=new BlockingQueue<Runnable>(queueCapacity);
    }

    class Worker extends Thread{
        private Runnable task;

        public Worker(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            //执行任务
            //1) 当task不为空，执行任务
            //2）当task执行完毕，再接着从任务队列获取任务并执行

        }
    }

}

public class BlockingQueue<T> {

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    //1.任务队列
    private Deque<T> queue = new ArrayDeque<T>();
    //2.锁
    private ReentrantLock lock = new ReentrantLock();
    //3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();
    //4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();
    //5.容量
    private int capacity;

    //带超时的阻塞获取
    public T poll(long timeOut, TimeUnit unit){
        lock.lock();
        try {
            //将timeout统一转为纳秒
            long nanos = unit.toNanos(timeOut);
            while (queue.isEmpty()) {
                try {
                    if (nanos<=0){
                        return null;
                    }
                    //返回的是剩余时间
                    nanos=emptyWaitSet.awaitNanos(nanos);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    //阻塞获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T t = queue.removeFirst();
            fullWaitSet.signal();
            return t;
        } finally {
            lock.unlock();
        }
    }

    //阻塞添加
    public void put(T element){
        lock.lock();
        try {
            while (queue.size()==capacity){
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(element);
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    //获取大小
    public int size(){
        lock.lock();
        try {
            return queue.size();
        }finally {
            lock.unlock();
        }
    }
}