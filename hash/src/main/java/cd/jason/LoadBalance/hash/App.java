package cd.jason.LoadBalance.hash;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ConsistentHashingWithVirtualNode sh=new ConsistentHashingWithVirtualNode();
        List<String> address=new ArrayList<String>();
        for(int i=0;i<10;i++)
        {
            address.add("http://"+"192.168.3."+i+":55567");
        }
        sh.setServer(address);
       String srv= sh.getServer("192.168.56.55:6666");
       System.out.println(srv);
       sh.addServeNode("http://192.168.4.100:6789");
       srv= sh.getServer("192.168.3.44:5673");
       System.out.println(srv);
       sh.deleteServerNode("http://192.168.3.4:55567");
       srv= sh.getServer("192.168.3.44:5673");
       System.out.println(srv);
    }
}
