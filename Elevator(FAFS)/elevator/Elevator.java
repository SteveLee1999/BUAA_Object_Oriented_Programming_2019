package elevator;

import com.oocourse.TimableOutput;
import com.oocourse.elevator1.PersonRequest;

public class Elevator extends Thread
{
    private static volatile Elevator elevator;
    
    private Elevator()
    {
        Elevator.floor = 1;
    }
    
    public static Elevator getInstance()
    {
        if (elevator == null)
        {
            synchronized (Elevator.class)
            {
                if (elevator == null)
                {
                    elevator = new Elevator();
                }
            }
        }
        return elevator;
    }
    
    private static int floor;
    
    public void run()
    {
        PersonRequest request;
        int id;
        int start;
        int from;
        int to;
        TimableOutput.initStartTimestamp();
        while (true)
        {
            try
            {
                request = Scheduler.getInstance().get();
                id = request.getPersonId();
                start = Elevator.floor;
                from = request.getFromFloor();
                to = request.getToFloor();
            
                sleep(500 * Math.abs(from - start));
                TimableOutput.println(String.format("OPEN-%d",from));
                TimableOutput.println(String.format("IN-%d-%d",id,from));
                sleep(500);
                TimableOutput.println(String.format("CLOSE-%d",from));
                sleep(500 * Math.abs(from - to));
                TimableOutput.println(String.format("OPEN-%d",to));
                TimableOutput.println(String.format("OUT-%d-%d",id,to));
                sleep(500);
                TimableOutput.println(String.format("CLOSE-%d",to));
                Elevator.floor = to;
            }
            catch (InterruptedException e)
            {
                System.out.println("Interruption!");
                System.exit(0);
            }
        }
    }
}
