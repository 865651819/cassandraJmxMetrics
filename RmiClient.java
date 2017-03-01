import java.net.InetAddress;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.service.StorageServiceMBean;

public class RmiClient {
private JMXServiceURL jmxUrl;
private JMXConnector connector;
private MBeanServerConnection mBeanServerconnection;

public RmiClient() throws Exception {
createJMXRmiConnector();
createMBeanServerConnection();
}

public static void main(String[] args) throws Exception {
String sStr = "testbucket000000011/obj000004672";
new RmiClient().test(sStr);
String addM = ComputeMD5.MD5Encode(sStr);
System.out.println("key=> " + sStr + " token=> " + addM);

}

private void createJMXRmiConnector() throws Exception {
// 1.The JMXConnectorServer protocol
String serverProtocol = "rmi";

// 2.The RMI server's host
// this is actually ignored by JSR 160
// String serverHost = "localhost";
String serverHost = "193.50.51.1:7199";

// 3.The host, port and path where the rmiregistry runs.
String namingHost = "193.50.51.1";
int namingPort = 1099;
String jndiPath = "/jmxconnector";

// 4. connector server url
// jmxUrl = new JMXServiceURL("service:jmx:" +
// serverProtocol + "://" + serverHost +
// "/jndi/rmi://" + namingHost + ":" +
// namingPort + jndiPath);

jmxUrl = new JMXServiceURL(
"service:jmx:rmi:///jndi/rmi://193.50.51.1:7199/jmxrmi");

// 5. 生成 JMXConnector,连接到url一端
// Connect a JSR 160 JMXConnector to the server side
connector = JMXConnectorFactory.connect(jmxUrl);

}

private void createMBeanServerConnection() throws Exception {
mBeanServerconnection = connector.getMBeanServerConnection();
}

public void test(String sStr) throws Exception {

ObjectName oName = new ObjectName(
"org.apache.cassandra.db:type=StorageService");
// 获取代理对象
Object proxy = MBeanServerInvocationHandler.newProxyInstance(
mBeanServerconnection, oName, StorageServiceMBean.class, true);

// 获取测试MBean,并执行它的(暴露出来被管理监控的)方法
StorageServiceMBean helloMBean = (StorageServiceMBean) proxy;

// public List getNaturalEndpoints(String table, String cf,
// String key);
// public List getNaturalEndpoints(String table, ByteBuffer
// key);

byte[] bytearray = sStr.getBytes();

List naturalEndpoints = helloMBean.getNaturalEndpoints(
"OBS", bytearray);
for (InetAddress add : naturalEndpoints) {
System.out.println(add.toString());
}
}

}
