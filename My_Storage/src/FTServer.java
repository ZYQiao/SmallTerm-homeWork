import java.util.*;
import java.io.*;
import java.util.*;
import java.net.*;
//��������
public class FTServer {
	public static File share = null;		//����������·��
	public static void main(String[] args) throws Exception  {
		int port = 4321;					//���������ļ����˿ں�
		
		Properties p = new Properties();
		p.load(FTServer.class.getClassLoader().getResourceAsStream("server.properties"));	//���������ļ�
		port = Integer.parseInt(p.getProperty("port"));
		share = new File(p.getProperty("share"));
		FTProtocol protocol = new FTProtocol();						//���������Ϳͻ���֮���Э��
		AdvancedSupport as = new AdvancedSupport(protocol);			//�̳߳��̷߳���
		NwServer nw = new NwServer(as,port);						//����������˿�
			
	}

}
