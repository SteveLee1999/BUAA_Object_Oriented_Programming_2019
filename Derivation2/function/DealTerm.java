package function;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DealTerm
{
    private BigInteger coeff;
    private BigInteger xdegree;
    private BigInteger sindegree;
    private BigInteger cosdegree;
    
    void setcoeff(BigInteger coeff)
    {
        this.coeff = coeff;
    }
    
    void setxdegree(BigInteger xdegree)
    {
        this.xdegree = xdegree;
    }
    
    void setsindegree(BigInteger sindegree)
    {
        this.sindegree = sindegree;
    }
    
    void setcosdegree(BigInteger cosdegree)
    {
        this.cosdegree = cosdegree;
    }
    
    BigInteger getcoeff()
    {
        return coeff;
    }
    
    BigInteger getxdegree()
    {
        return xdegree;
    }
    
    BigInteger getsindegree()
    {
        return sindegree;
    }
    
    BigInteger getcosdegree()
    {
        return cosdegree;
    }
    
    DealTerm(String s)
    //构造器，从符合条件的一项（s）得到相应系数
    {
        BigInteger minusone = new BigInteger("-1");
        BigInteger one = new BigInteger("1");
        BigInteger c = one;
        BigInteger xd = new BigInteger("0");
        BigInteger sind = new BigInteger("0");
        BigInteger cosd = new BigInteger("0");
        BigInteger n1;
        BigInteger n2;
        BigInteger n5;
        BigInteger n7;
        String s1;
        String s2;
        String s3;
        String s4;
        String s5;
        String s6;
        String s7;
        StringBuffer sb = new StringBuffer(s);
        String num = "[+-]?\\d+";
        Pattern pnum = Pattern.compile(num);
        Matcher mnum;
        //匹配 + 记录 + 删除
        //先匹配sin/cos带次数，然后sin/cos，最后常数项，否则会混淆常数项和指数
        String r1 = "[+-]?sin\\(x\\)\\^[+-]?\\d+";
        Pattern p1 = Pattern.compile(r1);
        Matcher m1 = p1.matcher(sb);
        while (m1.find())
        {
            s1 = m1.group();
            mnum = pnum.matcher(s1);
            mnum.find();
            n1 = new BigInteger(mnum.group());
            sind = sind.add(n1);
            if (s1.charAt(0) == '-')
            {
                c = c.multiply(minusone);
            }
            sb.delete(m1.start(), m1.end());
            m1 = p1.matcher(sb);//sb发生改变，需要重新匹配
        }
        
        String r2 = "[+-]?cos\\(x\\)\\^[+-]?\\d+";
        Pattern p2 = Pattern.compile(r2);
        Matcher m2 = p2.matcher(sb);
        while (m2.find())
        {
            s2 = m2.group();
            mnum = pnum.matcher(s2);
            mnum.find();
            n2 = new BigInteger(mnum.group());
            cosd = cosd.add(n2);
            if (s2.charAt(0) == '-')
            {
                c = c.multiply(minusone);
            }
            sb.delete(m2.start(), m2.end());
            m2 = p2.matcher(sb);
        }
        
        String r3 = "[+-]?sin\\(x\\)";
        Pattern p3 = Pattern.compile(r3);
        Matcher m3 = p3.matcher(sb);
        while (m3.find())
        {
            s3 = m3.group();
            sind = sind.add(one);
            if (s3.charAt(0) == '-')
            {
                c = c.multiply(minusone);
            }
            sb.delete(m3.start(), m3.end());
            m3 = p3.matcher(sb);
        }
        
        String r4 = "[+-]?cos\\(x\\)";
        Pattern p4 = Pattern.compile(r4);
        Matcher m4 = p4.matcher(sb);
        while (m4.find())
        {
            s4 = m4.group();
            cosd = cosd.add(one);
            if (s4.charAt(0) == '-')
            {
                c = c.multiply(minusone);
            }
            sb.delete(m4.start(), m4.end());
            m4 = p4.matcher(sb);
        }
        
        String r5 = "[+-]?x\\^[+-]?\\d+";
        Pattern p5 = Pattern.compile(r5);
        Matcher m5 = p5.matcher(sb);
        while (m5.find())
        {
            s5 = m5.group();
            mnum = pnum.matcher(s5);
            mnum.find();
            n5 = new BigInteger(mnum.group());
            xd = xd.add(n5);
            if (s5.charAt(0) == '-')
            {
                c = c.multiply(minusone);
            }
            sb.delete(m5.start(), m5.end());
            m5 = p5.matcher(sb);
        }
        
        String r6 = "[+-]?x";
        Pattern p6 = Pattern.compile(r6);
        Matcher m6 = p6.matcher(sb);
        while (m6.find())
        {
            s6 = m6.group();
            xd = xd.add(one);
            if (s6.charAt(0) == '-')
            {
                c = c.multiply(minusone);
            }
            sb.delete(m6.start(), m6.end());
            m6 = p6.matcher(sb);
        }
        
        String r7 = "[+-]?\\d+";
        Pattern p7 = Pattern.compile(r7);
        Matcher m7 = p7.matcher(sb);
        while (m7.find())
        {
            s7 = m7.group();
            n7 = new BigInteger(s7);
            c = c.multiply(n7);
            sb.delete(m7.start(), m7.end());
            m7 = p7.matcher(sb);
        }
        
        coeff = c;
        xdegree = xd;
        sindegree = sind;
        cosdegree = cosd;
    }
}