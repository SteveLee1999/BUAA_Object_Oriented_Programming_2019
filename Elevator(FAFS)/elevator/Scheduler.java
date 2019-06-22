package elevator;

import com.oocourse.elevator1.PersonRequest;
import java.util.concurrent.LinkedBlockingQueue;

public class Scheduler
{
    private static volatile Scheduler scheduler;
    
    private Scheduler()
    {
        Scheduler.busy = true;
        Scheduler.requestQueue = new LinkedBlockingQueue<>();
        Scheduler.end = false;
    }
    
    public static Scheduler getInstance()
    {
        if (scheduler == null)
        {
            synchronized (Elevator.class)
            {
                if (scheduler == null)
                {
                    scheduler = new Scheduler();
                }
            }
        }
        return scheduler;
    }
    
    private static LinkedBlockingQueue<PersonRequest> requestQueue;
    private static boolean busy;
    private static boolean end;
    
    public static void setEnd()
    {
        Scheduler.end = true;
    }
    
    public synchronized PersonRequest get()
    {
        while (busy)
        {
            try
            {
                if (end)
                {
                    System.exit(0);
                }
                wait(7500);
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption!");
                System.exit(0);
            }
        }
        busy = true;
        notifyAll();
        return requestQueue.poll();
    }
    
    public synchronized void put(PersonRequest request)
    {
        while (!busy)
        {
            try
            {
                System.out.println("we are trapped in put!");
                wait();
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption!");
                System.exit(0);
            }
        }
        busy = false;
        requestQueue.add(request);
        notifyAll();
    }
}
