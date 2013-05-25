import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

public class CharsetIs
{
    public static void main(String[] args)
    {
            Charset default_cs = Charset.defaultCharset();
            System.out.println("Default=[" + default_cs.name() + "]");
            for (String a : default_cs.aliases()) {
                System.out.println("  Alias=[" + a + "]");
            }
            for (Map.Entry<String, Charset> e : 
                    Charset.availableCharsets().entrySet()) {
                System.out.println("StandarizedName=[" + e.getKey() + "]");
                for (String a : e.getValue().aliases()) {
                    System.out.println("  Alias=[" + a + "]");
                }
            }
    }
}
