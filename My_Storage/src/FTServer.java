import java.util.*;
import java.io.*;
import java.util.*;
import java.net.*;
//主服务器
public class FTServer {
	public static File share = null;		//主服务器的路径
	public static void main(String[] args) throws Exception  {
		int port = 4321;					//主服务器的监听端口号
		
		Properties p = new Properties();
		p.load(FTServer.class.getClassLoader().getResourceAsStream("server.properties"));	//载入属性文件
		port = Integer.parseInt(p.getProperty("port"));
		share = new File(p.getProperty("share"));
		FTProtocol protocol = new FTProtocol();						//主服务器和客户端之间的协议
		AdvancedSupport as = new AdvancedSupport(protocol);			//线程池线程分配
		NwServer nw = new NwServer(as,port);						//监听并分配端口
			
	}

}
