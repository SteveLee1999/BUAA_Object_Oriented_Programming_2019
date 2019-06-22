import com.oocourse.uml1.interact.AppRunner;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        AppRunner runner = AppRunner.newInstance(
                MyUmlInteraction.class);
        runner.run(args);
    }
}
