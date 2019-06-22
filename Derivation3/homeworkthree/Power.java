package homeworkthree;

import java.math.BigInteger;
import java.util.ArrayList;

public class Power extends BasicTerm
{
    private BigInteger degree;
    
    public Power(String s)
    {
        if (s.length() == 1)
        {
            this.degree = new BigInteger("1");
        }
        else
        {
            this.degree = new BigInteger(s.substring(2));
        }
    }
    
    public Power(BigInteger newnum)
    {
        this.degree = newnum;
    }

    @Override
    public BasicTerm clone()
    {
        return new Power(this.degree);
    }
    
    @Override
    public String toString()
    {
        if (degree.compareTo(BigInteger.ZERO) == 0)
        {
            return "1";
        }
        else if (degree.compareTo(BigInteger.ONE) == 0)
        {
            return "x";
        }
        else
        {
            return "x^" + degree.toString();
        }
    }
    
    @Override
    public BasicTerm derivation()
    {
        if (degree.compareTo(BigInteger.ZERO) == 0)
        {
            return new Constant("0");
        }
        else if (degree.compareTo(BigInteger.ONE) == 0)
        {
            return new Constant("1");
        }
        else
        {
            ArrayList<BasicTerm> derived = new ArrayList<>();
            derived.add(new Constant(degree));
            derived.add(new Power(degree.subtract(BigInteger.ONE)));
            return new Product(derived);
        }
    }
    
    public boolean isOverstep()
    {
        return (degree.compareTo(new BigInteger("10000")) <= 0);
    }
}
