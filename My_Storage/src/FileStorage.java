import java.net.*;
import java.io.*;
import java.util.*;
//�洢�������
public class FileStorage {
	public static File share = null;
	public static void main(String [] args ) throws Exception
	{
		Properties p = new Properties();
		int port = 30000;	//�洢���Ĭ�϶˿�
		int i = Integer.parseInt(args[0]);		//��ô洢���ı��
		p.load(FileStorage.class.getClassLoader().getResourceAsStream("storage"+i+".properties"));	//����洢�ڵ�������ļ�
		System.out.println(p);		
		String root_folder = p.getProperty("root_folder");		//��ô洢�ڵ������λ��
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
		p.setProperty("usable", "true");							//�޸������ļ�
		FileOutputStream fos = new FileOutputStream ("src/storage"+i+".properties");		//����д�������ļ�
		p.store(fos, "Update");
		fos.close();
		FSProtocol protocol = new FSProtocol();			//�ṩЭ�����
		AdvancedSupport as = new AdvancedSupport(protocol);		//�ṩ�̳߳ط���
		NwServer nw = new NwServer(as,port);			//�ṩ�����ͷ���˿ڵķ���
		
		
		
	}
}
