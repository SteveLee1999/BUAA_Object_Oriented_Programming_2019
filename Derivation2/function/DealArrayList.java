package function;

import java.math.BigInteger;
import java.util.ArrayList;

class DealArrayList
{
    static ArrayList<DealTerm> DerivTerms(ArrayList<DealTerm> functArray)
    {
        ArrayList<DealTerm> derivedArray = new ArrayList<>();
        int i;
        BigInteger c;
        BigInteger d1;
        BigInteger d2;
        BigInteger d3;
        BigInteger one = new BigInteger("1");
        BigInteger zero = new BigInteger("0");
        //求导+除去=0的项
        for (i = 0;i < functArray.size();i++)
        {
            c = functArray.get(i).getcoeff();
            d1 = functArray.get(i).getxdegree();
            d2 = functArray.get(i).getsindegree();
            d3 = functArray.get(i).getcosdegree();
            
            DealTerm term1 = new DealTerm("");
            term1.setcoeff(c.multiply(d1));
            term1.setxdegree(d1.subtract(one));
            term1.setsindegree(d2);
            term1.setcosdegree(d3);
            if (!term1.getcoeff().equals(zero))
            {
                derivedArray.add(term1);
            }
            
            DealTerm term2 = new DealTerm("");
            term2.setcoeff(c.multiply(d2));
            term2.setxdegree(d1);
            term2.setsindegree(d2.subtract(one));
            term2.setcosdegree(d3.add(one));
            if (!term2.getcoeff().equals(zero))
            {
                derivedArray.add(term2);
            }
            
            DealTerm term3 = new DealTerm("");
            term3.setcoeff(c.multiply(d3).negate());
            term3.setxdegree(d1);
            term3.setsindegree(d2.add(one));
            term3.setcosdegree(d3.subtract(one));
            if (!term3.getcoeff().equals(zero))
            {
                derivedArray.add(term3);
            }
        }
        return derivedArray;
    }
    
    static void UniteTerms(ArrayList<DealTerm> functArray)
    {
        int length;
        int i;
        int j;
        BigInteger zero = new BigInteger("0");
        BigInteger ci;
        BigInteger xdi;
        BigInteger sindi;
        BigInteger cosdi;
        BigInteger cj;
        BigInteger xdj;
        BigInteger sindj;
        BigInteger cosdj;
        length = functArray.size();
        for (i = 0;i < length - 1;i++)
        {
            xdi = functArray.get(i).getxdegree();
            sindi = functArray.get(i).getsindegree();
            cosdi = functArray.get(i).getcosdegree();
            for (j = i + 1;j < length;j++)
            {
                xdj = functArray.get(j).getxdegree();
                sindj = functArray.get(j).getsindegree();
                cosdj = functArray.get(j).getcosdegree();
                if ((xdi.compareTo(xdj) == 0)
                        && (sindi.compareTo(sindj) == 0)
                        && (cosdi.compareTo(cosdj) == 0))
                {
                    ci = functArray.get(i).getcoeff();
                    cj = functArray.get(j).getcoeff();
                    functArray.get(i).setcoeff(ci.add(cj));
                    functArray.remove(j);
                    j--;
                    length--;
                }
            }
        }
        for (i = 0;i < length;i++)
        {
            if (functArray.get(i).getcoeff().compareTo(zero) == 0)
            {
                functArray.remove(i);
                i--;
                length--;
            }
        }
    }
    
    DealArrayList(ArrayList<DealTerm> functArray)
    {
        int i;
        int isfirst;
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        BigInteger minusone = new BigInteger("-1");
        BigInteger c;
        BigInteger d1;
        BigInteger d2;
        BigInteger d3;
        
        if (functArray.size() == 0)
        {
            System.out.print("0");
        }
        for (i = 0;i < functArray.size();i++)
        {
            c = functArray.get(i).getcoeff();
            d1 = functArray.get(i).getxdegree();
            d2 = functArray.get(i).getsindegree();
            d3 = functArray.get(i).getcosdegree();
            if (i >= 1 && c.compareTo(zero) > 0)//负数本身带有符号，因此不必管
            {
                System.out.print("+");
            }
            
            if (((c.compareTo(one) == 0) || (c.compareTo(minusone) == 0))
                    && (d1.compareTo(zero) == 0)
                    && (d2.compareTo(zero) == 0)
                    && (d3.compareTo(zero) == 0))
            {
                System.out.print(c);
            }
            else //至少一项次数！=0或者系数不是+-1
            {
                isfirst = 1;
                if ((c.compareTo(one) != 0) && (c.compareTo(minusone) != 0))
                {
                    System.out.print(c);
                    isfirst = 0;
                }
                else if (c.compareTo(minusone) == 0)
                {
                    System.out.print("-");
                }
                
                if (d1.compareTo(one) == 0 && isfirst == 0)
                {
                    System.out.print("*x");
                }
                else if (d1.compareTo(one) == 0 && isfirst == 1)
                {
                    System.out.print("x");
                    isfirst = 0;
                }
                else if (d1.compareTo(zero) != 0 && isfirst == 0)
                {
                    System.out.print("*x^");
                    System.out.print(d1);
                }
                else if (d1.compareTo(zero) != 0 && isfirst == 1)
                {
                    System.out.print("x^");
                    System.out.print(d1);
                    isfirst = 0;
                }
                
                if (d2.compareTo(one) == 0 && isfirst == 0)
                {
                    System.out.print("*sin(x)");
                }
                else if (d2.compareTo(one) == 0 && isfirst == 1)
                {
                    System.out.print("sin(x)");
                    isfirst = 0;
                }
                else if (d2.compareTo(zero) != 0 && isfirst == 0)
                {
                    System.out.print("*sin(x)^");
                    System.out.print(d2);
                }
                else if (d2.compareTo(zero) != 0 && isfirst == 1)
                {
                    System.out.print("sin(x)^");
                    System.out.print(d2);
                    isfirst = 0;
                }
                
                if (d3.compareTo(one) == 0 && isfirst == 0)
                {
                    System.out.print("*cos(x)");
                }
                else if (d3.compareTo(one) == 0 && isfirst == 1)
                {
                    System.out.print("cos(x)");
                }
                else if (d3.compareTo(zero) != 0 && isfirst == 0)
                {
                    System.out.print("*cos(x)^");
                    System.out.print(d3);
                }
                else if (d3.compareTo(zero) != 0 && isfirst == 1)
                {
                    System.out.print("cos(x)^");
                    System.out.print(d3);
                }
            }
        }
    }
}

