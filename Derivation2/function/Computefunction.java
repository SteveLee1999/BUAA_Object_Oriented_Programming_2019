package function;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;

public class Computefunction
{
    public static void main(String[] args)
    {
        try
        {
            Scanner input = new Scanner(System.in);
            String str = input.nextLine();
            StringBuffer strb = new StringBuffer(str);
            DealString term = new DealString(strb);
            str = strb.toString();
            //System.out.print(str);
            str = DealString.OptimizeString(str);
            //System.out.print(str);
            ArrayList<DealTerm> function = DealString.StringtoTerm(str);
            function = DealArrayList.DerivTerms(function);
            //System.out.println(function.get(1).getcoeff());
            //System.out.println(function.get(0).getxdegree());
            //System.out.println(function.get(0).getsindegree());
            //System.out.println(function.get(0).getcosdegree());
            DealArrayList.UniteTerms(function);
            DealArrayList result = new DealArrayList(function);
        } catch (NoSuchElementException e)
        {
            System.out.print("WRONG FORMAT!");
            System.exit(0);
        }
        
    }
}
