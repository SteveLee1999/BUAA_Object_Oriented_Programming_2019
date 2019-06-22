package homeworkthree;

import java.math.BigInteger;
import java.util.ArrayList;

public class Sinnest extends BasicTerm
{
    private BasicTerm term;//sin(term),thus term = (...)
    private BigInteger degree;
    
    public Sinnest(String s,BigInteger d)
    {
        this.term = BuildTree.BuildBody(s);
        this.degree = d;
    }
    
    public Sinnest(BasicTerm newterm,BigInteger d)
    {
        this.term = newterm;
        this.degree = d;
    }
    
    @Override
    public BasicTerm clone()
    {
        return new Sinnest(this.term,this.degree);
    }
    
    @Override
    public String toString()
    {
        String terms = new String("");
        if (degree.compareTo(BigInteger.ZERO) == 0)
        {
            return "1";
        }
        else if (degree.compareTo(BigInteger.ONE) == 0)
        {
            return "sin(" + this.term.toString() + ")";
        }
        else
        {
            return "sin(" + this.term.toString() + ")^" + degree.toString();
        }
    }
    
    @Override
    public BasicTerm derivation()
    {
        ArrayList<BasicTerm> derivedline = new ArrayList<>();
        derivedline.add(new Constant(degree));
        derivedline.add(new Sinnest(term,degree.subtract(BigInteger.ONE)));
        derivedline.add(new Cosnest(term,BigInteger.ONE));
        derivedline.add(term.derivation());
        return new Product(derivedline);
    }
    
    public boolean isOverstep()
    {
        return (degree.compareTo(new BigInteger("10000")) <= 0 && term.isOverstep());
    }
}
