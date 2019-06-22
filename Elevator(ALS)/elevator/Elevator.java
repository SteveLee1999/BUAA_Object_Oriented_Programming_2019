package elevator;

import com.oocourse.TimableOutput;
import com.oocourse.elevator2.PersonRequest;
import java.util.ArrayList;

public class Elevator extends Thread
{
    private static volatile Elevator elevator;
    
    private Elevator()
    {
        Elevator.floor = 1;
        mainstate = 0;
        hitcharray = new ArrayList<>();
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
    
    private static int floor;//电梯所在的楼层
    private static int mainstate;//主请求的人状态：0未进入电梯，1进入，2已出
    private static ArrayList<PersonRequest> hitcharray;//保存运行时的捎带者
    
    public void run()
    {
        PersonRequest mainrequest;
        ArrayList<PersonRequest> inarray = new ArrayList<>();
        ArrayList<PersonRequest> outarray = new ArrayList<>();//出去的人
        ArrayList<PersonRequest> leftarray = new ArrayList<>();
        boolean ori;//电梯运行方向，true表示向上
        int mainfrom;
        int mainto;
        TimableOutput.initStartTimestamp();
        while (true)
        {
            mainrequest = Scheduler.getInstance().get(floor);
            mainfrom = mainrequest.getFromFloor();
            mainto = mainrequest.getToFloor();
            ori = setOrientation(mainrequest);
            mainstate = 0;
            while (true)
            {
                inarray.clear();
                inarray = Scheduler.getInstance().gethitch(floor,ori);
                outarray.clear();
                outarray = new ArrayList<>(getOutArray());
    
                if (!inarray.isEmpty() || !outarray.isEmpty() ||
                        floor == mainfrom  && mainstate == 0 ||
                        floor == mainto && mainstate == 1)
                {
                    openTheDoor(mainrequest,inarray,outarray);
                }
                if (((ori && floor >= mainfrom && hitcharray.isEmpty()) ||
                    (!ori && floor <= mainto && hitcharray.isEmpty())) &&
                     mainstate == 1)
                {
                    ori = (mainto > mainfrom);
                }
                if (mainstate == 2 && hitcharray.isEmpty())
                {
                    break;
                }
                moveTheElevator(ori);
            }
        }
    }
    
    private boolean setOrientation(PersonRequest mainrequest)
    {
        int from = mainrequest.getFromFloor();
        int to = mainrequest.getToFloor();
        return ((from > floor) || (from == floor && to > from));
    }
    
    private void backTheElevator(boolean ori, ArrayList<PersonRequest> left)
    {
        moveTheElevator(!ori);
        TimableOutput.println(String.format("OPEN-%d",floor));
        int i;
        int id;
        for (i = 0;i < left.size();i++)
        {
            hitcharray.add(left.get(i));
            id = left.get(i).getPersonId();
            TimableOutput.println(String.format("IN-%d-%d",id,floor));
        }
        TimableOutput.println(String.format("CLOSE-%d",floor));
        moveTheElevator(ori);
    }
    
    private ArrayList<PersonRequest> getOutArray()
    {
        int i;
        ArrayList<PersonRequest> temparray = new ArrayList<>();
        for (i = 0;i < hitcharray.size();i++)
        {
            if (hitcharray.get(i).getToFloor() == Elevator.floor)
            {
                temparray.add(hitcharray.get(i));
                hitcharray.remove(i);
                i--;
            }
        }
        return temparray;
    }
    
    private void openTheDoor(PersonRequest mr,
                             ArrayList<PersonRequest> in,
                             ArrayList<PersonRequest> out)
    {
        int i;
        int id;
        TimableOutput.println(String.format("OPEN-%d",Elevator.floor));
        if (floor == mr.getFromFloor() && mainstate == 0)
        {
            id = mr.getPersonId();
            TimableOutput.println(String.format("IN-%d-%d",id,floor));
            mainstate = 1;
        }
        else if (floor == mr.getToFloor() && mainstate == 1)
        {
            id = mr.getPersonId();
            TimableOutput.println(String.format("OUT-%d-%d",id,floor));
            mainstate = 2;
        }
        for (i = 0;i < in.size();i++)
        {
            id = in.get(i).getPersonId();
            TimableOutput.println(String.format("IN-%d-%d",id,floor));
            hitcharray.add(in.get(i));
        }
        for (i = 0;i < out.size();i++)
        {
            id = out.get(i).getPersonId();
            TimableOutput.println(String.format("OUT-%d-%d",id,floor));
        }
        try
        {
            sleep(400);
        }
        catch (InterruptedException e)
        {
            System.out.println("InterruptedException!");
        }
        TimableOutput.println(String.format("CLOSE-%d",floor));
    }
    
    private void moveTheElevator(boolean ori)
    {
        try
        {
            sleep(400);
        }
        catch (InterruptedException e)
        {
            System.out.println("InterruptedException!");
        }
        if (ori && floor != -1)
        {
            Elevator.floor++;
        }
        else if (ori)
        {
            Elevator.floor = 1;
        }
        else if (floor != 1)
        {
            Elevator.floor--;
        }
        else
        {
            Elevator.floor = -1;
        }
        TimableOutput.println(String.format("ARRIVE-%d",Elevator.floor));
    }
}
