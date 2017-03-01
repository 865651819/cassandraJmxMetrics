package mycass;

import org.apache.cassandra.metrics.CassandraMetricsRegistry;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColumnFamilyStats1 {
    static String ColumnFamilyStatsString = "org.apache.cassandra.metrics:type=Storage,name=Load";

    public static void main(String[] args) throws IOException {
        String serviceURL = "service:jmx:rmi:///jndi/rmi://localhost:7199/jmxrmi";
        JMXServiceURL jmxUrl = new JMXServiceURL(serviceURL);
        Map<String, String[]> authenticationInfo = new HashMap<String, String[]>();
        JMXConnector jmxCon = JMXConnectorFactory.connect(jmxUrl, authenticationInfo);
        try {
            MBeanServerConnection catalogServerConnection = jmxCon.getMBeanServerConnection();
            CassandraMetricsRegistry.JmxMeterMBean cfstatMbean = locateMBean(new ObjectName(ColumnFamilyStatsString), CassandraMetricsRegistry.JmxMeterMBean.class, catalogServerConnection);
            System.out.println("Maximum Compaction Threshold >> " + cfstatMbean.getCount());
/*            System.out.println("Maximum Compaction Threshold >> " + cfstatMbean.getMeanRate());
            System.out.println("Maximum Compaction Threshold >> " + cfstatMbean.getOneMinuteRate());
            System.out.println("Maximum Compaction Threshold >> " + cfstatMbean.getFiveMinuteRate());
            System.out.println("Maximum Compaction Threshold >> " + cfstatMbean.getFifteenMinuteRate());
            System.out.println("Maximum Compaction Threshold >> " + cfstatMbean.getRateUnit());*/
        } catch (Exception e) {
            if (jmxCon != null) {
                jmxCon.close();
            }
            e.printStackTrace();
        }
    }

    private static <T> T locateMBean(ObjectName name, Class<T> mBeanClass, MBeanServerConnection catalogServerConnection) {
        return JMX.newMBeanProxy(catalogServerConnection, name, mBeanClass);
    }
}
