import java.net.*;
import java.io.*;
import java.util.*;
//�ͻ���
public class FTClient 
{

	Socket s = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	String path = "/Users/yuqiao/Desktop/";//�����û��洢��·��
	String[] args = null;

	public void start(int port) throws Exception 
	{
		s = establish(port);	//�û�Socket
		dis = new DataInputStream(s.getInputStream());
		dos = new DataOutputStream(s.getOutputStream());

		if (args[0].equals("download")) 
		{		//����
			
			download(args[1]);
			s.close();
			System.exit(0);
		} 
		else if (args[0].equals("upload")) 
		{  //�ļ��ϴ�
			File f = new File(path,args[1]);		//����ļ�������
			if (f.isFile()) {	
				upload(args[1]);		//�ϴ�
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
			remove(args[1]);		//�ļ�ɾ��
			s.close();
			System.exit(0);
		}
		else if(args[0].equals("rename"))
		{
			rename(args[1],args[2]);	//�ļ�������
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
	
	//��������
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
	
	//�ļ��ϴ�
	public void upload(String filename) throws Exception 
	{
		File f = new File(path,filename);

		if (!f.exists() || !f.isFile())
		{	//�ļ������ڻ��߲���һ���ļ�
			System.out
					.println("it's wrong, maybe it is not a file or not exists");
			System.exit(-6);
		}

		byte[] buffer = new byte[4096];
		int rr = 0;

		dos.writeInt(1);		//�������дһ��1����ʾҪ�ϴ�
		dos.writeUTF(f.getName());		//�������д��ʵ���ļ���
		dos.writeLong(f.length());		//�������д�ļ��ĳ���
		dos.flush();				//��ջ���
		
		if(dis.readInt()==-1)		//�Ƿ��ҵ����㹻�ռ��storage server
		{
			System.out.println("no enough space in storage");
		}
		else
		{
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream bis = new BufferedInputStream(fis);

			while ((rr = bis.read(buffer)) != -1) {			//���ļ��ж�������
				dos.write(buffer, 0, rr);					//�������д
				dos.flush();
			}
			bis.close();
			fis.close();
			
			if(dis.readInt()==-1)	//�ϴ��ļ�ʧ��
			{
				System.out.println("file uploading failed");
			}
			else		//�ϴ��ļ��ɹ�
			{
				System.out.println("file was uploaded successfully");//���uuid
				System.out.println(dis.readUTF());//���uuid
			}		
		}
	}
	
	//����
	public void download(String uuid) throws Exception 
	{
		dos.writeInt(2);	//�������д2��ʾ����
		dos.writeUTF(uuid);	//�������д��Ҫ���ص��ļ�uuid
		dos.flush();			//��ջ���
		
		if(dis.readInt()==-1)		//Ҫ���ص��ļ�������
		{
			System.out.println("no such file");
			System.exit(0);
		}
		else		//Ҫ���ص��ļ�����
		{
			String filename = dis.readUTF();	//����ʵ���ļ���
			long len = dis.readLong();	//�����ļ��ĳ���
			if(dis.readInt()==-1)		//�洢��㲻����
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
						rr = dis.read(buffer, 0, buffer.length);	//�����Է����������� 
					} else {
						rr = dis.read(buffer, 0, (int) (len - r));
					}

					r = r + rr;
					bos.write(buffer, 0, rr);	//д�������ļ���
				}
				
				if(dis.readInt()==-1)		//����ʧ��
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
	
	//ɾ���洢��������ָ���ļ�
	public void remove(String uuid) throws Exception {
		dos.writeInt(3);	//�������д3����ʾҪɾ��
		dos.writeUTF(uuid);	//�������д��Ҫɾ�����ļ�uuid
		dos.flush();			//��ջ���

	
		if(dis.readInt()==-1)	//û�и��ļ�
		{
			System.out.println("no such file");
		}	
		else	//���ڸ��ļ�
		{
			if(dis.readInt()==-1)
			{
				System.out.println("fail to delete");
			}
			else
			{
				String filename = dis.readUTF();	//����ʵ���ļ���
				System.out.println(filename + " was deleted successfully");
			}
		}
	}
	
	//�洢�������ϵ��ļ������� (���Ϻ�׺��)
	public void rename(String uuid,String new_name) throws Exception {
		dos.writeInt(4);	//�������д4����ʾҪ������
		dos.writeUTF(uuid);	//�������д��Ҫ���������ļ�uuid
		dos.writeUTF(new_name);//�������д���µ�����
		dos.flush();			//��ջ���

	
		if(dis.readInt()==-1)	//û�и��ļ�
		{
			System.out.println("no such file");
		}	
		else	//���ڸ��ļ�
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
		if(args.length==0) {		//��֪�û�ʹ�õķ���
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
