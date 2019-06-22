package homeworkthree;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            Scanner in = new Scanner(System.in);
            String input = in.nextLine();
            CheckInput checkedinput = new CheckInput();
            checkedinput.setInput(input);
            checkedinput.CheckAll();
            BasicTerm line = BuildTree.BuildBody(checkedinput.getInput());
            if (!line.isOverstep())
            {
                System.out.print("WRONG FORMAT!");
                System.exit(0);
            }
            System.out.println(line.derivation().toString());
        } catch (NoSuchElementException e)
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
        
    }
}

