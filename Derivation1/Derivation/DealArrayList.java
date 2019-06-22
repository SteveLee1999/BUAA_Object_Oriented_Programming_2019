package Derivation;

import java.math.BigInteger;
import java.util.ArrayList;

class DealArrayList
{
    static ArrayList<DealTerm> UniteTerms(ArrayList<DealTerm> polyArray)
    {
        int length;
        int i;
        int j;
        BigInteger degreei;
        BigInteger degreej;
        length = polyArray.size();
        for (i = 0;i < length - 1;i++)
        {
            degreei = polyArray.get(i).getdegree();
            for (j = i + 1;j < length;j++)
            {
                degreej = polyArray.get(j).getdegree();
                if (degreei.compareTo(degreej) == 0)
                {
                    polyArray.get(i).addcoeff(polyArray.get(j).getcoeff());
                    polyArray.remove(j);
                    j--;
                    length--;
                }
            }
        }
        return polyArray;
    }
    
    static void PrintPoly(ArrayList<DealTerm> polyarray)
    {
        int i;
        BigInteger a = new BigInteger("0");
        BigInteger b = new BigInteger("1");
        for (i = 0;i < polyarray.size();i++)
        {
            if (i >= 1 && polyarray.get(i).getcoeff().compareTo(a) > 0)
            {
                System.out.print("+");
            }
    
            if (i == 0 &&
                polyarray.get(i).getcoeff().compareTo(a) == 0 &&
                polyarray.size() == 1)
            {
                System.out.print("0");
            }
            else if (i >= 1 && polyarray.get(i).getcoeff().compareTo(a) == 0)
            {
                continue;
            }
            else if (polyarray.get(i).getdegree().compareTo(a) == 0)
            {
                System.out.print(polyarray.get(i).getcoeff());
            }
            else if (polyarray.get(i).getdegree().compareTo(b) == 0
                    && polyarray.get(i).getcoeff().compareTo(b) == 0)
            {
                System.out.print("x");
            }
            else if (polyarray.get(i).getdegree().compareTo(b) == 0)
            {
                System.out.print(polyarray.get(i).getcoeff());
                System.out.print("*x");
            }
            else if (polyarray.get(i).getcoeff().compareTo(b) == 0)
            {
                System.out.print("x^");
                System.out.print(polyarray.get(i).getdegree());
            }
            else
            {
                System.out.print(polyarray.get(i).getcoeff());
                System.out.print("*x^");
                System.out.print(polyarray.get(i).getdegree());
            }
        }
    }
}
