import java.net.*;
import java.io.*;
import java.util.*;
//以管理者的身份管理主服务器
public class FileManager {
	Socket s = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	String[] args = null;
	public Socket establish(int port) {
		try {
			Socket s = new Socket("127.0.0.1",port);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void start(int port) throws Exception {
		s = establish(port);	//用户Socket
		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());
		if(args[0].equals("file_num"))	//获取存储在结点上的文件数目
		{
			dos.writeInt(5);
			System.out.println("the number of files: " +dis.readInt());
		}
		else if(args[0].equals("storage_num"))		//获得可用结点的数量
		{
			dos.writeInt(6);
			System.out.println("the number of storages: " +dis.readInt());
		}
		else if(args[0].equals("list_file"))			//获得存储在结点上的文件的列表
		{
			dos.writeInt(7);
			int size = dis.readInt();
			System.out.println("files: ");
			for(int i=0;i<size;i++)
			{
				System.out.println(dis.readUTF());
			}
		}
		else if(args[0].equals("all_information"))			//获得存储结点的所有信息
		{
			dos.writeInt(8);
			int storage_num=dis.readInt();
			for(int i=0;i<storage_num;i++)
			{
				System.out.println("the storage"+ i+" : ");
				System.out.println("name : "+ dis.readUTF());
				System.out.println("ip : "+ dis.readUTF());
				System.out.println("port : "+ dis.readInt());
				System.out.println("total : "+ dis.readUTF());
				System.out.println("volume : " + dis.readUTF());
				System.out.println("usable : "+ dis.readBoolean());
				System.out.println("root_folder : "+dis.readUTF());
				System.out.println("");
			}
		}
		else if(args[0].equals("set_total"))
		{
			dos.writeInt(9);
			dos.writeInt(Integer.parseInt(args[1]));		//指明要更改总容量的结点编号
			dos.writeUTF(args[2]);							//指明新的总容量
			
			if(dis.readInt()==-1)
				System.out.println("failed to change total capcity, the new capacity must larger than the current volume");
			else
				System.out.println("the total capacity was set successfully");
		}
		else
		{
			System.out.println("input is wrong");
			System.out.println("Usage:");
			System.out.println("java FileManager file_num");
			System.out.println("java FileManager storage_num");
			System.out.println("java FileManager list_file");
			System.out.println("java FileManager all_information");
			System.out.println("java FileManager set_total index_server acapacity");
		}
		dis.close();
		dos.close();
		
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		if(args.length==0) {		//提示使用的方法
			System.out.println("Usage:");
			System.out.println("java FileManager file_num");
			System.out.println("java FileManager storage_num");
			System.out.println("java FileManager list_file");
			System.out.println("java FileManager all_information");
			System.out.println("java FileManager set_total index_server acapacity");
			System.exit(0);
		}
			FileManager fm = new FileManager();
			fm.args = args;
			fm.start(4321);
		
	}
	

}
