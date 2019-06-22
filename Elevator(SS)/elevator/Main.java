package elevator;

import com.oocourse.TimableOutput;

import java.util.HashSet;

class Main
{
    Main()
    {
    }
    
    public static void main(String[] args)
    {
        HashSet<Integer> stayA = new HashSet<>();
        stayA.add(-3);
        stayA.add(-2);
        stayA.add(-1);
        stayA.add(1);
        stayA.add(15);
        stayA.add(16);
        stayA.add(17);
        stayA.add(18);
        stayA.add(19);
        stayA.add(20);
        HashSet<Integer> stayB = new HashSet<>();
        stayB.add(-2);
        stayB.add(-1);
        stayB.add(1);
        stayB.add(2);
        stayB.add(4);
        stayB.add(5);
        stayB.add(6);
        stayB.add(7);
        stayB.add(8);
        stayB.add(9);
        stayB.add(10);
        stayB.add(11);
        stayB.add(12);
        stayB.add(13);
        stayB.add(14);
        stayB.add(15);
        HashSet<Integer> stayC = new HashSet<>();
        stayC.add(1);
        stayC.add(3);
        stayC.add(5);
        stayC.add(7);
        stayC.add(9);
        stayC.add(11);
        stayC.add(13);
        stayC.add(15);
        Button.getInstance(stayA,stayB,stayC).start();
        TimableOutput.initStartTimestamp();
        new Elevator('A',400,6,stayA).start();
        new Elevator('B',500,8,stayB).start();
        new Elevator('C',600,7,stayC).start();
    }
}
