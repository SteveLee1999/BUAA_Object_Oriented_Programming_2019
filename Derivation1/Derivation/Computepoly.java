package Derivation;

import java.util.Scanner;
import java.util.ArrayList;

public class Computepoly
{
    public static void main(String[] args)
    {
        Scanner input = new Scanner(System.in);
        String str = input.nextLine();
        str = DealString.InitString(str);
        //System.out.print(str);
        ArrayList<DealTerm> polyarray = DealString.StringtoTerm(str);
        //System.out.print(polyarray.get(0).getcoeff());
        //System.out.print(polyarray.get(0).getdegree());
        DealArrayList.UniteTerms(polyarray);
        DealArrayList.PrintPoly(polyarray);
    }
}
