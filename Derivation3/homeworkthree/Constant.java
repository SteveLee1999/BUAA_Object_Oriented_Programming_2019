package homeworkthree;

import java.math.BigInteger;

public class Constant extends BasicTerm
{
    private BigInteger value;
    
    public Constant(String s)
    {
        this.value = new BigInteger(s);
    }
    
    public Constant(BigInteger newvalue)
    {
        this.value = newvalue;
    }
    
    @Override
    public BasicTerm clone()
    {
        return new Constant(this.value);
    }
    
    @Override
    public String toString()
    {
        return value.toString();
    }
    
    @Override
    public BasicTerm derivation()
    {
        return new Constant("0");
    }
    
    public boolean isOverstep()
    {
        return true;
    }
}
