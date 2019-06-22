package elevator;

import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

public class Button extends Thread
{
    private static volatile Button button;
    
    public static Button getInstance()
    {
        if (button == null)
        {
            synchronized (Elevator.class)
            {
                if (button == null)
                {
                    button = new Button();//默认构造器
                }
            }
        }
        return button;
    }
    
    private boolean isfirst = true;
    
    public void run()
    {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true)
        {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null)
            {
                Scheduler.getInstance().setEnd();
                break;
            }
            else
            {
                Scheduler.getInstance().put(request);
                if (isfirst)
                {
                    Elevator.getInstance().start();
                    isfirst = false;
                }
            }
        }
    }
}
