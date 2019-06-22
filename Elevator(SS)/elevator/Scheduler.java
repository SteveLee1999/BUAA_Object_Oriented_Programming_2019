package elevator;

import java.util.ArrayList;
import java.util.HashSet;

public class Scheduler
{
    private static volatile Scheduler scheduler;
    
    private Scheduler()
    {
        Scheduler.arrayA = new ArrayList<>();
        Scheduler.arrayB = new ArrayList<>();
        Scheduler.arrayC = new ArrayList<>();
        Scheduler.arraywait = new ArrayList<>();
    }
    
    public static Scheduler getInstance()
    {
        if (scheduler == null)
        {
            synchronized (Scheduler.class)
            {
                if (scheduler == null)
                {
                    scheduler = new Scheduler();
                }
            }
        }
        return scheduler;
    }
    
    private static ArrayList<Request> arrayA;
    private static ArrayList<Request> arrayB;
    private static ArrayList<Request> arrayC;
    private static ArrayList<Request> arraywait;
    private static Boolean[] end = {false,false,false,false};
    
    public synchronized Request get(char id,HashSet<Integer> stay,int floor) {
        ArrayList<Request> temparray1;
        if (id == 'A') {
            temparray1 = arrayA;
        }
        else if (id == 'B') {
            temparray1 = arrayB;
        }
        else {
            temparray1 = arrayC;
        }
        while (temparray1.isEmpty()) {
            try {
                if (id == 'C') {
                    for (Request ele:arrayB) {
                        if (stay.contains(ele.getFrom()) &&
                                stay.contains(ele.getTo())) {
                            arrayB.remove(ele);
                            end[id - 'A' + 1] = false;
                            return ele;
                        }
                    }
                }
                if (end[0] && arraywait.isEmpty()) {
                    end[id - 'A' + 1] = true;
                }
                if (end[1] && end[2] && end[3]) {
                    System.exit(0);
                }
                wait();
            }
            catch (InterruptedException e) {
                System.exit(0);
            }
        }
        end[id - 'A' + 1] = false;
        Request result = getNear(temparray1,floor);
        temparray1.remove(result);
        return result;
    }
    
    private synchronized Request getNear(ArrayList<Request> array,int floor)
    {
        int i;
        int index;
        int disi;
        int disindex;
        for (i = 1,index = 0;i < array.size();i++)
        {
            disi = Math.abs(array.get(i).getFrom() - floor);
            disindex = Math.abs(array.get(index).getFrom() - floor);
            if (disi < disindex)
            {
                index = i;
            }
        }
        return array.get(index);
    }
    
    public synchronized ArrayList<Request> gethitch(
            char id,int floor,boolean ori,HashSet<Integer> stay,int space) {
        ArrayList<Request> hitcharray = new ArrayList<>();
        ArrayList<Request> temparray = new ArrayList<>();
        ArrayList<Request> temparray1;
        ArrayList<Request> temparray2;
        ArrayList<Request> temparray3;
        int from;
        int to;
        int i;
        int j;
        int cnt;
        if (id == 'A') {
            temparray1 = arrayA;
            temparray2 = arrayC;
            temparray3 = arrayB;
        }
        else if (id == 'B') {
            temparray1 = arrayB;
            temparray2 = arrayC;
            temparray3 = arrayA;
        }
        else {
            temparray1 = arrayC;
            temparray2 = arrayB;
            temparray3 = arrayA;
        }
        for (cnt = 1,j = 0;cnt <= 3 && j < space;cnt++) {
            if (cnt == 1) {
                temparray = temparray1;
            }
            else if (cnt == 2) {
                temparray = temparray2;
            }
            else if (cnt == 3) {
                temparray = temparray3;
            }
            for (i = 0;i < temparray.size();i++) {
                from = temparray.get(i).getFrom();
                to = temparray.get(i).getTo();
                if (((ori && to > from) || (!ori && to < from))
                        && stay.contains(to) && floor == from) {
                    hitcharray.add(temparray.get(i));
                    temparray.remove(i);
                    i--;
                    j++;
                }
                if (j >= space) {
                    break;
                }
            }
            notifyAll();
            if (j >= space) {
                break;
            }
        }
        //System.out.println(String.format("%c......",id));
        //System.out.println(String.format("%d",hitcharray.size()));
        return hitcharray;
    }
    
    public synchronized void release(ArrayList<Request> outarray) // 电梯发信号释放中转者
    {
        for (Request r:outarray)
        {
            if (r.getTransfer() >= 1 || r.getTransfer() <= 3)
            {
                for (Request wait:arraywait)
                {
                    if (r.getId() == wait.getId())
                    { // 中转后电梯序号是存在转前的指令里的！
                        put(r.getTransfer(),wait);
                        arraywait.remove(wait);
                        break; // id 唯一
                    }
                }
            }
            else if (r.getTransfer() != 4)
            {
                System.out.println("Error in release!");
                System.exit(0);
            }
        }
        notifyAll();
    }
    
    public synchronized void put(char elevatorid,Request request)
    {
        //System.out.println(String.format("put %c",elevatorid));
        ArrayList<Request> temparray = new ArrayList<>();
        if (elevatorid == 'A')
        {
            temparray = arrayA;
        }
        else if (elevatorid == 'B')
        {
            temparray = arrayB;
        }
        else if (elevatorid == 'C')
        {
            temparray = arrayC;
        }
        else if (elevatorid == 'W') // 等待队列
        {
            temparray = arraywait;
        }
        else
        {
            System.out.println("Error in put!");
            System.exit(0);
        }
        temparray.add(request);
        notifyAll();
    }
    
    public synchronized void setEnd()
    {
        end[0] = true;
        notifyAll();
    }
}
