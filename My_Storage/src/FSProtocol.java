import java.net.Socket;
import java.io.*;
import java.util.*;
//主服务器和存储节点之间数据传输的协议
public class FSProtocol implements IOStrategy{
	
	public void service (Socket socket)
	{
		System.out.println(socket);
		String client = socket.getInetAddress().getHostName() + "(" + socket.getInetAddress().getHostAddress() + ")";
		File target_file;
		try{
			//与FTServer交互的输入输出流
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			String file_name = null;
			long len = 0;
			
			byte[] buffer = new byte[4096];
			
			long r = 0;
			int rr = 0;
			int command = dis.readInt();
			System.out.println(command);
			switch(command)
			{
			case 1:		//上传文件
				file_name = dis.readUTF();	//文件名	
				len = dis.readLong();		//文件大小
				FileOutputStream fos = new FileOutputStream(new File(
						FileStorage.share, file_name));//在share目录下创建文件
				
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				
				r = 0;
				rr = 0;
				while (r < len) {
					//从用户那里读入
					if (len - r >= buffer.length) {
						rr = dis.read(buffer, 0, buffer.length);
					} else {
						rr = dis.read(buffer, 0, (int) (len - r));
					}

					r = r + rr;
					bos.write(buffer, 0, rr);//Server写到指定的位置
				}
				System.out.println("File was uploaded successfully");
				bos.close();
				fos.close();
				break;
			case 2:		//下载文件
				file_name = dis.readUTF();	//获得需要下载的文件的文件名
				target_file = new File(FileStorage.share, file_name);
				//存储节点本地的输入和输出流
				FileInputStream fis = new FileInputStream(target_file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				while ((rr = bis.read(buffer)) != -1) {//读取数据并写到本地
					dos.write(buffer, 0, rr);
					dos.flush();
				}
				System.out.println("File was downloaded successfully");
				bis.close();
				fis.close();
				break;
			case 3:		//删除文件
				file_name = dis.readUTF();
				target_file = new File(FileStorage.share, file_name);//获取目标文件的文件引用
				if(target_file.delete())		
				{
					System.out.println("File was deleted successfully");
					dos.writeInt(1);
				}
				else
				{
					dos.writeInt(-1);
				}
				break;
			case 4:		//重命名
				file_name = dis.readUTF();		//获得文件在存储节点的唯一名(uuid+文件名)
				String new_name= dis.readUTF();	//获得指定文件的新的名称
				String file_uuid = dis.readUTF();	//获得指定文件的uuid
				target_file = new File(FileStorage.share, file_name);
				
				if(target_file.renameTo((new File(FileStorage.share,file_uuid+new_name))))//成功改名
				{
					System.out.println("File was renamed successfully");
					dos.writeInt(1);
				}
				else	//改名失败
				{
					System.out.println("File was renamed unsuccessfully");
					dos.writeInt(-1);;
				}
				break;
			case 5:		//断点重传
				System.out.println("point retransition start!");
				file_name = dis.readUTF();	//获得文件名
				long offset = dis.readLong();		//获得先前已经读完的偏移量
				target_file = new File(FileStorage.share, file_name);		
				RandomAccessFile raf = new RandomAccessFile(target_file,"r");
				raf.seek(offset);	//找到断点
				while ((rr = raf.read(buffer)) != -1) {		//继续文件传输
					dos.write(buffer, 0, rr);
					dos.flush();
				}
				System.out.println("File was downloaded successfully");
				raf.close();
				break;
			}		
			
		}catch (Exception e) {
			if (e instanceof EOFException) {
				System.out.println(client + " disconnected");
			} else {
				e.printStackTrace();
			}

		}
		
		
	}
	
}
