package elevator;

import com.oocourse.elevator2.PersonRequest;
import java.util.ArrayList;

public class Scheduler
{
    private static volatile Scheduler scheduler;
    
    private Scheduler()
    {
        Scheduler.end = false;
        Scheduler.requestarray = new ArrayList<>();
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
    
    private static ArrayList<PersonRequest> requestarray;
    private static boolean end;
    
    public synchronized void setEnd()
    {
        Scheduler.end = true;
        notifyAll();
    }
    
    public synchronized PersonRequest get(int floor)
    {
        PersonRequest result;
        int i;
        int index;
        int disi;
        int disindex;
        while (requestarray.isEmpty())
        {
            try
            {
                if (end)
                {
                    System.exit(0);
                }
                System.out.println("I am sleeping!");
                wait();
                System.out.println("I am woken!");
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption!");
                System.exit(0);
            }
        }
        for (i = 1,index = 0;i < requestarray.size();i++)
        {
            disi = Math.abs(requestarray.get(i).getFromFloor() - floor);
            disindex = Math.abs(requestarray.get(index).getFromFloor() - floor);
            if (disi < disindex)
            {
                index = i;
            }
        }
        result = requestarray.get(index);
        requestarray.remove(index);
        return result;
    }
    
    public synchronized ArrayList<PersonRequest> gethitch(int floor,boolean ori)
    {
        ArrayList<PersonRequest> hitcharray = new ArrayList<>();
        int from;
        int to;
        int i;
        for (i = 0;i < requestarray.size();i++)
        {
            from = requestarray.get(i).getFromFloor();
            to = requestarray.get(i).getToFloor();
            if (((ori && to > from) || (!ori && to < from)) && floor == from)
            {
                hitcharray.add(requestarray.get(i));
                requestarray.remove(i);
                i--;
            }
        }
        return hitcharray;
    }
    
    public synchronized ArrayList<PersonRequest> getleft(int floor,boolean ori)
    {
        ArrayList<PersonRequest> leftarray = new ArrayList<>();
        int from;
        int to;
        int i;
        for (i = 0;i < requestarray.size();i++)
        {
            from = requestarray.get(i).getFromFloor();
            to = requestarray.get(i).getToFloor();
            if (ori && to > from &&
                    (floor == from + 1 && floor != 1 || from == -1))
            {
                leftarray.add(requestarray.get(i));
                requestarray.remove(i);
                i--;
            }
            else if (!ori && to < from &&
                    (floor == from - 1 && floor != -1 || from == 1))
            {
                leftarray.add(requestarray.get(i));
                requestarray.remove(i);
                i--;
            }
        }
        return leftarray;
    }
    
    public synchronized void put(PersonRequest request)
    {
        requestarray.add(request);
        notifyAll();
    }
}
