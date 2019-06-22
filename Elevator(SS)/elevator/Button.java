package elevator;

import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;

import java.util.HashSet;

public class Button extends Thread
{
    private static volatile Button button;
    
    public static Button getInstance(
            HashSet<Integer> a,HashSet<Integer> b,HashSet<Integer> c)
    {
        if (button == null)
        {
            synchronized (Elevator.class)
            {
                if (button == null)
                {
                    button = new Button(a,b,c);//默认构造器
                }
            }
        }
        return button;
    }
    
    private Button(HashSet<Integer> a,HashSet<Integer> b,HashSet<Integer> c)
    {
        this.stayA = a;
        this.stayB = b;
        this.stayC = c;
    }
    
    private final HashSet<Integer> stayA;
    private final HashSet<Integer> stayB;
    private final HashSet<Integer> stayC;
    
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
                person2Request(request);
            }
        }
    }
    
    private void person2Request(PersonRequest pr)
    {
        HashSet<Character> fromset = checkInStay(pr.getFromFloor());
        HashSet<Character> toset = checkInStay(pr.getToFloor());
        
        if (fromset.contains('A') && toset.contains('A'))
        {
            putOneArray('A',pr);
        }
        else if (fromset.contains('B') && toset.contains('B'))
        {
            putOneArray('B',pr);
        }
        else if (fromset.contains('C') && toset.contains('C'))
        {
            putOneArray('C',pr);
        }
        else if (fromset.contains('A') && toset.contains('B'))
        {
            putTwoArray('A','B',pr,stayA,stayB,4,5);
        }
        else if (fromset.contains('B') && toset.contains('A'))
        {
            putTwoArray('B','A',pr,stayB,stayA,5,4);
        }
        else if (fromset.contains('A') && toset.contains('C'))
        {
            putTwoArray('A','C',pr,stayA,stayC,4,6);
        }
        else if (fromset.contains('C') && toset.contains('A'))
        {
            putTwoArray('C','A',pr,stayC,stayA,6,4);
        }
        else if (fromset.contains('B') && toset.contains('C'))
        {
            putTwoArray('B','C',pr,stayB,stayC,5,6);
        }
        else if (fromset.contains('C') && toset.contains('B'))
        {
            putTwoArray('C','B',pr,stayC,stayB,6,5);
        }
        else
        {
            System.out.println("Error in person2Request!");
            System.exit(0);
        }
    }
    
    private void putOneArray(char elevatorid,PersonRequest pr)
    {
        int from = pr.getFromFloor();
        int to = pr.getToFloor();
        int id = pr.getPersonId();
        Request r = new Request(from,to,id,'D');
        Scheduler.getInstance().put(elevatorid,r);
    }
    
    private void putTwoArray(char fromid,char toid, PersonRequest pr,
                             HashSet<Integer> stay1,HashSet<Integer> stay2,
                             int speed1,int speed2)
    {
        int from = pr.getFromFloor();
        int to = pr.getToFloor();
        int id = pr.getPersonId();
        int transfer = -5;
        int d1;
        int d2;
        boolean isfirst = true;
        for (int elem:stay1)
        {
            if (stay2.contains(elem))
            {
                d1 = speed1 * Math.abs(transfer - from)
                        + speed2 * Math.abs(transfer - to);
                d2 = speed1 * Math.abs(elem - from)
                        + speed2 * Math.abs(elem - to);
                if (isfirst || (d2 < d1))
                {
                    isfirst = false;
                    transfer = elem;
                }
            }
        }
        Request r1 = new Request(from,transfer,id,toid);
        Request r2 = new Request(transfer,to,id,'D');
        Scheduler.getInstance().put(fromid,r1);
        Scheduler.getInstance().put('W',r2); //W：表示进入等待队列
    }
    
    private HashSet<Character> checkInStay(int floor)
    {
        HashSet<Character> result = new HashSet<>();
        for (int elem:stayA)
        {
            if (elem == floor)
            {
                result.add('A');
                break;
            }
        }
        for (int elem:stayB)
        {
            if (elem == floor)
            {
                result.add('B');
                break;
            }
        }
        for (int elem:stayC)
        {
            if (elem == floor)
            {
                result.add('C');
                break;
            }
        }
        return result;
    }
}
