import java.net.*;
import java.io.*;
import java.util.*;
//存储结点启动
public class FileStorage {
	public static File share = null;
	public static void main(String [] args ) throws Exception
	{
		Properties p = new Properties();
		int port = 30000;	//存储结点默认端口
		int i = Integer.parseInt(args[0]);		//获得存储结点的编号
		p.load(FileStorage.class.getClassLoader().getResourceAsStream("storage"+i+".properties"));	//载入存储节点的属性文件
		System.out.println(p);		
		String root_folder = p.getProperty("root_folder");		//获得存储节点所存的位置
		share = new File(root_folder);
		if(!share.isDirectory())
		{
			System.out.println("share directory not exists or isn't a directory");
			System.exit(-4);
		}
		String server = p.getProperty("name");
		String ip = p.getProperty("ip");
		port =Integer.parseInt(p.getProperty(("port")));
		String volume = p.getProperty("volume");
		p.setProperty("usable", "true");							//修改属性文件
		FileOutputStream fos = new FileOutputStream ("src/storage"+i+".properties");		//重新写回属性文件
		p.store(fos, "Update");
		fos.close();
		FSProtocol protocol = new FSProtocol();			//提供协议服务
		AdvancedSupport as = new AdvancedSupport(protocol);		//提供线程池服务
		NwServer nw = new NwServer(as,port);			//提供监听和分配端口的服务
		
		
		
	}
}
