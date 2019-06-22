package Derivation;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DealTerm
{
    private BigInteger coeff;
    private BigInteger degree;
    
    BigInteger getcoeff()
    {
        return coeff;
    }
    
    BigInteger getdegree()
    {
        return degree;
    }
    
    void addcoeff(BigInteger coeff1)
    {
        coeff = coeff.add(coeff1);
    }
    
    DealTerm(String sterm)
    {
        BigInteger a = new BigInteger("0");
        BigInteger b = new BigInteger("1");
        
        String r1 = "^[+-]?\\d+$";
        String r2 = "^[+-]?x$";
        String r3 = "^[+-]?\\d+\\*x$";
        String r4 = "^[+-]?x\\^[+-]?\\d+$";
        String r5 = "^[+-]?\\d+\\*x\\^[+-]?\\d+$";
        Pattern p1 = Pattern.compile(r1);
        Pattern p2 = Pattern.compile(r2);
        Pattern p3 = Pattern.compile(r3);
        Pattern p4 = Pattern.compile(r4);
        Pattern p5 = Pattern.compile(r5);
        Matcher m1 = p1.matcher(sterm);
        Matcher m2 = p2.matcher(sterm);
        Matcher m3 = p3.matcher(sterm);
        Matcher m4 = p4.matcher(sterm);
        Matcher m5 = p5.matcher(sterm);
        
        if (m1.find())
        {
            coeff = new BigInteger("0");
            degree = new BigInteger("0");
        }
        else if (m2.find())
        {
            degree = new BigInteger("0");
            if (sterm.charAt(0) == '-')
            {
                coeff = new BigInteger("-1");
            }
            else
            {
                coeff = new BigInteger("1");
            }
        }
        else if (m3.find())
        {
            String str3 = m3.group();
            String regex3 = "[+-]?\\d+";
            Pattern pattern3 = Pattern.compile(regex3);
            Matcher matcher3 = pattern3.matcher(str3);
            matcher3.find();
            coeff = new BigInteger(matcher3.group());
            degree = new BigInteger("0");
        }
        else if (m4.find())
        {
            String str4 = m4.group();
            String regex4 = "[+-]?\\d+";
            Pattern pattern4 = Pattern.compile(regex4);
            Matcher matcher4 = pattern4.matcher(str4);
            matcher4.find();
            if (str4.charAt(0) == '-')
            {
                coeff = new BigInteger(matcher4.group());
                coeff = coeff.negate();
            }
            else
            {
                coeff = new BigInteger(matcher4.group());
            }
            degree = new BigInteger(matcher4.group());
            degree = degree.subtract(b);
        }
        else if (m5.find())
        {
            String str5 = m5.group();
            String regex5 = "[+-]?\\d+";
            Pattern pattern5 = Pattern.compile(regex5);
            Matcher matcher5 = pattern5.matcher(str5);
            matcher5.find();
            coeff = new BigInteger(matcher5.group());
            matcher5.find();
            degree = new BigInteger(matcher5.group());
            coeff = coeff.multiply(degree);
            if (degree.compareTo(a) == 0)
            {
                coeff = new BigInteger("0");
            }
            else
            {
                degree = degree.subtract(b);
            }
        }
    }
}
