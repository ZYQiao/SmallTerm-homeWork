import java.net.Socket;
import java.io.*;
import java.util.*;
//���������ʹ洢�ڵ�֮�����ݴ����Э��
public class FSProtocol implements IOStrategy{
	
	public void service (Socket socket)
	{
		System.out.println(socket);
		String client = socket.getInetAddress().getHostName() + "(" + socket.getInetAddress().getHostAddress() + ")";
		File target_file;
		try{
			//��FTServer���������������
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
			case 1:		//�ϴ��ļ�
				file_name = dis.readUTF();	//�ļ���	
				len = dis.readLong();		//�ļ���С
				FileOutputStream fos = new FileOutputStream(new File(
						FileStorage.share, file_name));//��shareĿ¼�´����ļ�
				
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				
				r = 0;
				rr = 0;
				while (r < len) {
					//���û��������
					if (len - r >= buffer.length) {
						rr = dis.read(buffer, 0, buffer.length);
					} else {
						rr = dis.read(buffer, 0, (int) (len - r));
					}

					r = r + rr;
					bos.write(buffer, 0, rr);//Serverд��ָ����λ��
				}
				System.out.println("File was uploaded successfully");
				bos.close();
				fos.close();
				break;
			case 2:		//�����ļ�
				file_name = dis.readUTF();	//�����Ҫ���ص��ļ����ļ���
				target_file = new File(FileStorage.share, file_name);
				//�洢�ڵ㱾�ص�����������
				FileInputStream fis = new FileInputStream(target_file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				while ((rr = bis.read(buffer)) != -1) {//��ȡ���ݲ�д������
					dos.write(buffer, 0, rr);
					dos.flush();
				}
				System.out.println("File was downloaded successfully");
				bis.close();
				fis.close();
				break;
			case 3:		//ɾ���ļ�
				file_name = dis.readUTF();
				target_file = new File(FileStorage.share, file_name);//��ȡĿ���ļ����ļ�����
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
			case 4:		//������
				file_name = dis.readUTF();		//����ļ��ڴ洢�ڵ��Ψһ��(uuid+�ļ���)
				String new_name= dis.readUTF();	//���ָ���ļ����µ�����
				String file_uuid = dis.readUTF();	//���ָ���ļ���uuid
				target_file = new File(FileStorage.share, file_name);
				
				if(target_file.renameTo((new File(FileStorage.share,file_uuid+new_name))))//�ɹ�����
				{
					System.out.println("File was renamed successfully");
					dos.writeInt(1);
				}
				else	//����ʧ��
				{
					System.out.println("File was renamed unsuccessfully");
					dos.writeInt(-1);;
				}
				break;
			case 5:		//�ϵ��ش�
				System.out.println("point retransition start!");
				file_name = dis.readUTF();	//����ļ���
				long offset = dis.readLong();		//�����ǰ�Ѿ������ƫ����
				target_file = new File(FileStorage.share, file_name);		
				RandomAccessFile raf = new RandomAccessFile(target_file,"r");
				raf.seek(offset);	//�ҵ��ϵ�
				while ((rr = raf.read(buffer)) != -1) {		//�����ļ�����
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
