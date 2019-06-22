package elevator;

import com.oocourse.TimableOutput;
import java.util.ArrayList;
import java.util.HashSet;

public class Elevator extends Thread
{
    private final char elevatorid;
    private final long speed;
    private final int volume;
    private final HashSet<Integer> stay;
    private Integer people; // 当前人数
    private int floor; // 当前楼层
    private int mainstate; // 当前主请求状态：0未进入电梯，1进入，2已出
    private ArrayList<Request> hitcharray; // 包括主线程！
    
    Elevator(char id,long speed,int volume,HashSet<Integer> stay)
    {
        this.elevatorid = id;
        this.speed = speed;
        this.volume = volume;
        this.stay = stay;
    
        this.people = 0;
        this.floor = 1;
        this.mainstate = 0;
        this.hitcharray = new ArrayList<>();
    }
    
    public void run()
    {
        Request mainrequest;
        ArrayList<Request> inarray;
        ArrayList<Request> outarray;
        boolean ori;
        int mainfrom;
        int mainto;
        while (true)
        {
            mainrequest = Scheduler.getInstance().get(elevatorid,stay,floor);
            people++; // 虽然此时主请求还未进入电梯，但要先留个位置
            if (mainrequest.getId() == -1)
            {
                break;
            }
            mainfrom = mainrequest.getFrom();
            mainto = mainrequest.getTo();
            ori = setOrientation(mainrequest);
            mainstate = 0;
            while (true)
            {   /* 无需考虑本层是否允许开门 */
                try
                {
                    sleep(30);
                }
                catch (InterruptedException e)
                {
                    System.out.println("InterruptedException!");
                }
                if (stay.contains(floor))
                {
                    outarray = new ArrayList<>(getOutArray(mainto));
                    inarray = new ArrayList<>(getInArray(mainrequest,ori));
                    if ((!inarray.isEmpty() || !outarray.isEmpty()))
                    {
                        openTheDoor(inarray,outarray);
                    }
                    Scheduler.getInstance().release(outarray);
                    /* 释放中转的人，放在这里是避免被此人出该电梯之前先进下一个 */
                }
                if (((ori && floor >= mainfrom) || (!ori && floor <= mainto))
                        && hitcharray.size() == 1 && mainstate == 1)
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
    
    private boolean setOrientation(Request mainrequest) // 设置初始方向
    {
        int from = mainrequest.getFrom();
        int to = mainrequest.getTo();
        return ((from > floor) || (from == floor && to > from));
    }
    
    private ArrayList<Request> getOutArray(int mainto) // 找到电梯里本层要下的人(包括主请求）
    {
        int i;
        ArrayList<Request> outarray = new ArrayList<>();
        for (i = 0;i < hitcharray.size();i++)
        {
            if (hitcharray.get(i).getTo() == floor)
            {
                outarray.add(hitcharray.get(i));
                hitcharray.remove(i);
                i--;
            }
        }
        if (floor == mainto && mainstate == 1)
        {
            mainstate = 2;
        }
        people -= outarray.size();
        return outarray;
    }
    
    private ArrayList<Request> getInArray(Request mainrequest,boolean ori)
    {
        ArrayList<Request> inarray = new ArrayList<>();
        ArrayList<Request> temp;
        if (floor == mainrequest.getFrom() && mainstate == 0)
        {
            inarray.add(mainrequest);
            hitcharray.add(mainrequest);
            /* 主请求已经算过，不必再加人数 */
            mainstate = 1;
        }
        temp = Scheduler.getInstance().gethitch(
                elevatorid,floor,ori,stay,volume - people);
        /* 主请求已经从Scheduler的队列中取出，因此主请求并不会被“捎带” */
        people += temp.size();
        inarray.addAll(temp);
        hitcharray.addAll(temp);
        return inarray;
    }
    
    private void openTheDoor(ArrayList<Request> in, ArrayList<Request> out)
    {
        int i;
        int id;
        TimableOutput.println(String.format("OPEN-%d-%c",floor,elevatorid));
        for (i = 0;i < out.size();i++)
        {
            id = out.get(i).getId();
            TimableOutput.println(
                    String.format("OUT-%d-%d-%c",id,floor,elevatorid));
        }
        for (i = 0;i < in.size();i++)
        {
            id = in.get(i).getId();
            TimableOutput.println(
                    String.format("IN-%d-%d-%c",id,floor,elevatorid));
        }
        try
        {
            sleep(400);
        }
        catch (InterruptedException e)
        {
            System.out.println("InterruptedException!");
        }
        TimableOutput.println(String.format("CLOSE-%d-%c",floor,elevatorid));
        //System.out.println(String.format("%d people left",people));
    }
    
    private void moveTheElevator(boolean ori)
    {
        try
        {
            sleep(speed);
        }
        catch (InterruptedException e)
        {
            System.out.println("InterruptedException!");
        }
        if (ori && floor != -1)
        {
            floor++;
        }
        else if (ori)
        {
            floor = 1;
        }
        else if (floor != 1)
        {
            floor--;
        }
        else
        {
            floor = -1;
        }
        TimableOutput.println(String.format("ARRIVE-%d-%c",floor,elevatorid));
    }
}
