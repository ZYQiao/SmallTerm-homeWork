import java.net.*;
import java.io.*;
import java.util.*;
//客户端
public class FTClient 
{

	Socket s = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	String path = "/Users/yuqiao/Desktop/";//保存用户存储的路径
	String[] args = null;

	public void start(int port) throws Exception 
	{
		s = establish(port);	//用户Socket
		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());

		if (args[0].equals("download")) 
		{		//下载
			
			download(args[1]);
			s.close();
			System.exit(0);
		} 
		else if (args[0].equals("upload")) 
		{  //文件上传
			File f = new File(path,args[1]);		//获得文件的引用
			if (f.isFile()) {	
				upload(args[1]);		//上传
			} 
			else {
				s.close();
				System.out.println(args[1] + " not exists");
				System.exit(-7);
			}

			s.close();
			System.exit(0);

		}
		else if(args[0].equals("remove"))
		{
			remove(args[1]);		//文件删除
			s.close();
			System.exit(0);
		}
		else if(args[0].equals("rename"))
		{
			rename(args[1],args[2]);	//文件重命名
			s.close();
			System.exit(0);;
		}
		else
		{
			System.out.println("input is wrong");
			System.out.println("Usage:");
			System.out.println("java FTClient upload afilename");
			System.out.println("java FTClient download uuid");
			System.out.println("java FTClient remove uuid");
			System.out.println("java FTClient rename uuid anewname");
			System.exit(0);
		}
	}
	
	//建立连接
	public Socket establish(int port) 
	{	
		try {
			Socket s = new Socket("127.0.0.1",port);
			System.out.println(s);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	//文件上传
	public void upload(String filename) throws Exception 
	{
		File f = new File(path,filename);

		if (!f.exists() || !f.isFile())
		{	//文件不存在或者不是一个文件
			System.out
					.println("it's wrong, maybe it is not a file or not exists");
			System.exit(-6);
		}

		byte[] buffer = new byte[4096];
		int rr = 0;

		dos.writeInt(1);		//向服务器写一个1，表示要上传
		dos.writeUTF(f.getName());		//向服务器写真实的文件名
		dos.writeLong(f.length());		//向服务器写文件的长度
		dos.flush();				//清空缓存
		
		if(dis.readInt()==-1)		//是否找到了足够空间的storage server
		{
			System.out.println("no enough space in storage");
		}
		else
		{
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);

			while ((rr = bis.read(buffer)) != -1) {			//从文件中读出内容
				dos.write(buffer, 0, rr);					//向服务器写
				dos.flush();
			}
			bis.close();
			fis.close();
			
			if(dis.readInt()==-1)	//上传文件失败
			{
				System.out.println("file uploading failed");
			}
			else		//上传文件成功
			{
				System.out.println("file was uploaded successfully");//输出uuid
				System.out.println(dis.readUTF());//输出uuid
			}		
		}
	}
	
	//下载
	public void download(String uuid) throws Exception 
	{
		dos.writeInt(2);	//向服务器写2表示下载
		dos.writeUTF(uuid);	//向服务器写出要下载的文件uuid
		dos.flush();			//清空缓存
		
		if(dis.readInt()==-1)		//要下载的文件不存在
		{
			System.out.println("no such file");
			System.exit(0);
		}
		else		//要下载的文件存在
		{
			String filename = dis.readUTF();	//接受实际文件名
			long len = dis.readLong();	//接收文件的长度
			if(dis.readInt()==-1)		//存储结点不可用
			{
				System.out.println("no storage is usable");
				System.exit(0);
			}
			else
			{
				byte[] buffer = new byte[4096];
				long r = 0;
				int rr = 0;
				FileOutputStream fos = new FileOutputStream(new File(path,filename));
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				while (r < len)
				{
					if (len - r >= buffer.length) {
						rr = dis.read(buffer, 0, buffer.length);	//读来自服务器的内容 
					} else {
						rr = dis.read(buffer, 0, (int) (len - r));
					}

					r = r + rr;
					bos.write(buffer, 0, rr);	//写到本地文件中
				}
				
				if(dis.readInt()==-1)		//下载失败
				{
					System.out.println("failed to download");
					new File(path,filename).delete();
				}
				else
					System.out.println("downloaded successfully");
				bos.close();
				fos.close();
			}
		}
	}
	
	//删除存储服务器中指定文件
	public void remove(String uuid) throws Exception {
		dos.writeInt(3);	//向服务器写3，表示要删除
		dos.writeUTF(uuid);	//向服务器写出要删除的文件uuid
		dos.flush();			//清空缓存

	
		if(dis.readInt()==-1)	//没有该文件
		{
			System.out.println("no such file");
		}	
		else	//存在该文件
		{
			if(dis.readInt()==-1)
			{
				System.out.println("fail to delete");
			}
			else
			{
				String filename = dis.readUTF();	//接受实际文件名
				System.out.println(filename + " was deleted successfully");
			}
		}
	}
	
	//存储服务器上的文件重命名 (加上后缀名)
	public void rename(String uuid,String new_name) throws Exception {
		dos.writeInt(4);	//向服务器写4，表示要重命名
		dos.writeUTF(uuid);	//向服务器写出要重命名的文件uuid
		dos.writeUTF(new_name);//向服务器写出新的名字
		dos.flush();			//清空缓存

	
		if(dis.readInt()==-1)	//没有该文件
		{
			System.out.println("no such file");
		}	
		else	//存在该文件
		{
			if(dis.readInt()==-1)
			{
				System.out.println("fail to rename");
			}
			else
			{
				System.out.println(uuid + " was renamed successfully");
			}
		}

	}
	
	
	public static void main(String[] args) throws Exception {
		if(args.length==0) {		//告知用户使用的方法
			System.out.println("Usage:");
			System.out.println("java FTClient upload afilename");
			System.out.println("java FTClient download uuid");
			System.out.println("java FTClient remove uuid");
			System.out.println("java FTClient rename uuid anewname");
			System.exit(0);
			
		}
		FTClient ftc = new FTClient();
		ftc.args = args;
		ftc.start(4321);

	}
}
