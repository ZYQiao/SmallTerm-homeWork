import java.net.Socket;
import java.io.*;
import java.util.*;
import java.math.*;

//����������ͻ���֮���Э��
public class FTProtocol implements IOStrategy {

	int storage_num = 3; // ��ʼ�Ľ������
	HashSet<String> files_stored = new HashSet<String>(); // ���������ļ������ַ�����
	My_Server[] storage_servers = new My_Server[storage_num]; // ���棨�洢���������ࣩ��һ������

	// ���ܣ���ʵ���ݻ����㵽���ʵĵ�λ
	// ��������BΪ��λ���ַ���
	// ����ֵ����KB��MB��GBΪ��λ���ַ���
	String Normalize(String volume) {
		float capacity = 0;
		int count = 0;
		capacity = ChangeVolumeToFloat(volume);
		while (capacity >= 1024) {
			capacity /= 1024;
			count++;
		}
		if (count == 0)
			return "" + capacity + "B";
		else if (count == 1)
			return "" + capacity + "KB";
		else if (count == 2)
			return "" + capacity + "MB";
		else
			return "" + capacity + "GB";
	}

	// ���ܣ���ָ�����ַ������͵��ݻ�תΪfloat����
	// ������ָ���ĵ�ǰ�ݻ�
	// ����ֵ��float���͵ĵ�ǰ�ݻ�(��BΪ��λ)

	float ChangeVolumeToFloat(String volume) {
		float capacity = 0;
		char c = '\0';
		boolean flag = false; // С����֮����true��С����֮ǰ��false
		float count = 10.0f;
		for (int i = 0; i < volume.length(); i++) {
			c = volume.charAt(i);
			if (c <= '9' && c >= '0' && flag == false) // ��������
			{
				capacity = capacity * 10 + (c - '0');
			} else if (c <= '9' && c >= '0' && flag == true) // С������
			{
				capacity = capacity + (c - '0') / count;
				count *= 10;
			} else if (c == '.') {
				flag = true;
				continue;
			}

			else
				break;
		}
		// ͳһ�����ֽ�
		if (c == 'G')
			capacity *= (1024 * 1024 * 1024);
		else if (c == 'M')
			capacity *= (1024 * 1024);
		else if (c == 'K')
			capacity *= 1024;
		return capacity;
	}

	// ���ܣ��жϵ�ǰ�ݻ��Ƿ��ܴ���ָ���Ĵ�С���ļ�
	// volume�Ǵ洢�������Ŀ����ݻ�(��λ����ȡ)��len���ļ��ĳ���(B)
	// �������洢�ڵ�ĵ�ǰ�ݻ����ļ��Ĵ�С��
	// ����ֵ��һ��������������ģ���ʾ���棬����������Ǵ����������ݻ�
	// ������ظ��������ʾ������

	float SpaceEnough(String volume, long len) {
		float capacity = ChangeVolumeToFloat(volume);
		return capacity - len;
	}

	// ���ܣ��Ƚ�����String���͵��ݻ�
	// ���� ������string���͵��ݻ�
	// ��� ��ǰ�ߴ����true�����þ���false
	boolean VolumeCompare(String volume1, String volume2) {
		float v1 = ChangeVolumeToFloat(volume1);
		float v2 = ChangeVolumeToFloat(volume2);
		if (v1 - v2 >= 0)
			return true;
		else
			return false;
	}

	// ���ܣ����ָ����ŵĴ洢�ڵ���ݻ�(String����)
	// ������ָ���洢�ڵ�ı��
	// ����ֵ ��ָ���洢�ڵ���ݻ�
	String VolumeOf(int index) {
		if (index < 0)
			return "" + 0;
		else
			return storage_servers[index].volume;
	}

	// ���ܣ����ø���ƽ��ѡ����ʵĽ��
	// ������һ��һ������Ҫ�Ĵ洢�������ı�ţ��ʹ����ļ��Ĵ�С
	// ����ֵ:����ʹ�õĴ洢�������ı��
	int ChooseStorageServer(int exclusion, long file_len) {
		int pre = -1;
		for (int i = 0; i < storage_num; i++) {
			// ��ǰ�±겻����exclusion���Ҹýڵ���ò��ҽ��ռ乻�ò��Ҹýڵ������н���п����ݻ�����(����ƽ��)
			if (i != exclusion && storage_servers[i].usable == true
					&& SpaceEnough((storage_servers[i]).volume, file_len) >= 0
					&& VolumeCompare(VolumeOf(i), VolumeOf(pre))) {
				pre = i;
			}
		}
		if (pre == -1)
			System.out.println("no proper storage");
		else
			System.out.println("choose " + pre);
		return pre;
	}

	// ���ܣ� �ļ��ϴ��󣬶�ָ���洢�ڵ���ݻ����м�С
	// ������ �洢�ڵ�ı�ţ��ļ��ĳ���(B)
	// ����ֵ����
	void StorageDecrease(int index, long file_len) {
		float remainder = SpaceEnough(storage_servers[index].volume, file_len); // �洢�ļ����ʣ��ռ�
		BigDecimal bd = new BigDecimal(remainder);
		bd.setScale(2, BigDecimal.ROUND_HALF_UP);// С�������λ����������
		storage_servers[index].volume = Normalize("" + bd + "B");
	}

	// ���ܣ� �ļ����غ󣬶�ָ���洢�ڵ���ݻ���������
	// ������ �洢�ڵ�ı�ţ��ļ��ĳ���(B)
	// ����ֵ����
	void StorageIncrease(int index, long file_len) {
		float capacity = ChangeVolumeToFloat(storage_servers[index].volume);
		capacity += file_len; // ɾ���ļ��������Ŀռ�
		BigDecimal bd = new BigDecimal(capacity);
		bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		storage_servers[index].volume = Normalize("" + bd + "B");
	}

	// ���ܣ��Դ洢�ڵ��ϵ��ļ�����������
	// �������洢�ڵ��ţ��ļ������ļ�uuid���ļ���������
	// ����ֵ��true�����ɹ���false����ʧ��
	boolean rename(int index_server, String target_file_name, String target_file_uuid, String new_name)
			throws Exception {
		// �������ӣ��������
		Socket s = new Socket(storage_servers[index_server].ip, storage_servers[index_server].port);
		DataOutputStream dos_storage = new DataOutputStream(s.getOutputStream());
		DataInputStream dis_storage = new DataInputStream(s.getInputStream());
		dos_storage.writeInt(4); // ���ߴ洢��㼴������
		dos_storage.writeUTF(target_file_uuid + target_file_name); // ����uuid�ļ���
		dos_storage.writeUTF(new_name); // �����µ��ļ���
		dos_storage.writeUTF(target_file_uuid); // ����uuid
		dos_storage.flush(); // ��ջ���
		int signal = dis_storage.readInt(); // �ж��ǲ��Ǹ����ɹ�
		s.close();
		if (signal < 0)
			return false;
		else
			return true;
	}

	// ���ܣ�ɾ���洢����ϵ�ָ���ļ�
	// �������ļ��ı�ţ��ļ������֣��ļ�uuid
	// ����ֵ����
	void remove(int index_server, String target_file_name, String target_file_uuid) throws Exception {
		// �������Ӳ������
		Socket s = new Socket(storage_servers[index_server].ip, storage_servers[index_server].port);
		DataOutputStream dos_storage = new DataOutputStream(s.getOutputStream());
		DataInputStream dis_storage = new DataInputStream(s.getInputStream());
		dos_storage.writeInt(3); // ���ߴ洢�ڵ�Ҫ����ɾ������
		dos_storage.writeUTF(target_file_uuid + target_file_name); // ���� uuis�ļ���
		s.close();
	}

	// ���ܣ����ô洢�ڵ������ļ���volume����
	// ���� ���洢���ı��
	// ����ֵ����
	void SetPropertiesVolume(int index_server) throws Exception {

		Properties p = new Properties();
		FileInputStream fis = new FileInputStream("storage" + index_server + ".properties"); // ���������ļ�
		p.load(fis);
		fis.close();
		p.setProperty("volume", storage_servers[index_server].volume); // �޸������ļ�
		FileOutputStream fos = new FileOutputStream("storage" + index_server + ".properties"); // ����д�������ļ�
		p.store(fos, "Update");
		fos.close();
	}

	// ���ܣ����ô洢�ڵ������ļ���usable����
	// ���� ���洢���ı��
	// ����ֵ����
	void SetPropertiesUsable(int index_server) throws Exception {

		Properties p = new Properties();
		FileInputStream fis = new FileInputStream("/storage" + index_server + ".properties"); // ���������ļ�
		p.load(fis);
		fis.close();
		p.setProperty("usable", "" + storage_servers[index_server].usable); // �޸������ļ�
		FileOutputStream fos = new FileOutputStream("/storage" + index_server + ".properties"); // ����д�������ļ�
		p.store(fos, "Update");
		fos.close();
	}

	// ���ܣ��ϴ��ļ����洢�ڵ�
	// ��������������ţ�ָ���ļ������ã���ʱ�ļ�
	// ����ֵ����
	void upload(int index_server, My_File my_file, File temp_file) throws Exception {
		// �������Ӳ������
		Socket s = new Socket(storage_servers[index_server].ip, storage_servers[index_server].port);
		DataOutputStream dos_storage = new DataOutputStream(s.getOutputStream());
		DataInputStream dis_storage = new DataInputStream(s.getInputStream());
		dos_storage.writeInt(1);
		dos_storage.writeUTF(my_file.uuid.toString() + my_file.name); // ����uuid�ļ���
		dos_storage.writeLong(my_file.len); // �����ļ�����
		FileInputStream temp_fis = new FileInputStream(temp_file);
		BufferedInputStream temp_bis = new BufferedInputStream(temp_fis);// ���ڶ�ȡ�����ļ�����
		int r = 0;
		int rr = 0;
		byte[] buffer = new byte[4096];
		try {
			while ((rr = temp_bis.read(buffer)) != -1) { // �ӱ���
				dos_storage.write(buffer, 0, rr); // д���洢�ڵ�
				dos_storage.flush();
				r = r + rr;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			temp_bis.close();
		}
	}

	// �ṩЭ�����
	public void service(Socket socket) {
		String client = socket.getInetAddress().getHostName() + "(" + socket.getInetAddress().getHostAddress() + ")";
		String file_uuid;
		File target_file = null;
		Iterator it;
		String path1, path2;
		My_File my_file = new My_File(); // �Զ�������������ļ�����
		try {

			Properties p = new Properties();
			for (int i = 0; i < storage_num; i++) // ��ʼʱ����������ȡ�йظ����洢�ڵ������
			{
				p.load(FileStorage.class.getClassLoader().getResourceAsStream("storage" + i + ".properties"));
				System.out.println(p);
				storage_servers[i] = new My_Server();
				storage_servers[i].name = p.getProperty("server");
				storage_servers[i].ip = p.getProperty("ip");
				storage_servers[i].port = Integer.parseInt(p.getProperty(("port")));
				storage_servers[i].volume = p.getProperty("volume");
				storage_servers[i].usable = (p.getProperty("usable")).equals("true") ? true : false;
				storage_servers[i].total = p.getProperty("total");
				storage_servers[i].root_folder = p.getProperty("root_folder");
			}
			String[] files = (FTServer.share).list();// ��ô洢�ڽڵ��ϵ��ļ��������б�
			for (int i = 0; i < files.length; i++) {
				files_stored.add(files[i]);
			}
			System.out.println("Storage is connected successfully");

			// ���������Ϳͻ��˽�������
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);

			int index_server1, index_server2; // �����洢�ڵ���

			// ���������ʹ洢�ڵ㽻������
			DataOutputStream dos_storage1 = null;
			DataInputStream dis_storage1 = null;
			DataOutputStream dos_storage2 = null;
			DataInputStream dis_storage2 = null;
			String filename = null;
			long len = 0;

			byte[] buffer = new byte[4096]; // ����

			long r = 0;
			int rr = 0;

			int command = dis.readInt(); // ��ȡ���Կͻ��˵�ָ��
			switch (command) {
			case 1: // file upload �ļ��ϴ�
				my_file.name = filename = dis.readUTF(); // �ļ���
				my_file.len = len = dis.readLong(); // �ļ���С
				my_file.uuid = UUID.randomUUID(); // ����uuid
				index_server1 = ChooseStorageServer(-1, len); // ѡ��ڵ�1
				index_server2 = ChooseStorageServer(index_server1, len); // ѡ��ڵ�2

				if (index_server1 == -1 && index_server2 == -1) // û�ҵ����õĽڵ�
				{
					dos.writeInt(-1);
					break;
				}
				// ����ҵ��ˣ���ôindex_server1�϶��У�index_server2��Ҫ��һ���ж�

				// ��FileServer��������ʱ�ļ�
				dos.writeInt(1);
				File temp_file = new File(FTServer.share, my_file.uuid.toString() + "temp");
				FileOutputStream temp_fos = new FileOutputStream(temp_file);
				BufferedOutputStream temp_bos = new BufferedOutputStream(temp_fos);
				r = 0;
				rr = 0;
				try {
					while (r < len) {
						if (len - r >= buffer.length) { // ���û��������
							rr = dis.read(buffer, 0, buffer.length);
						} else {
							rr = dis.read(buffer, 0, (int) (len - r));
						}
						r = r + rr;
						temp_bos.write(buffer, 0, rr); // Serverд��ָ����λ��
					}
					temp_bos.close();
				} catch (Exception e) // ���������쳣���û������ж�
				{
					temp_bos.close();
					temp_file.delete();
					System.out.println("the client is offline");
					break;
				}

				try {
					upload(index_server1, my_file, temp_file); // �ϴ�
				} catch (Exception e) // ��������ж�
				{
					System.out.println("server1 exception handling");
					storage_servers[index_server1].usable = false;
					SetPropertiesUsable(index_server1);
					index_server1 = ChooseStorageServer(index_server2, len);
					if (index_server1 == -1 && index_server2 == -1) // �Ҳ������1�Ľ�㲢��2�Ž��Ҳ�����á�����ʧ��
					{
						dos.writeInt(-1);
						break;
					}
					if (index_server1 != -1) // �ҵ�����������쳣�Ľ�㣬��������
					{
						upload(index_server1, my_file, temp_file);
					}
				}

				if (index_server2 != -1) // �ҵ��˱��ݴ洢���2
				{
					try {
						upload(index_server2, my_file, temp_file);
					} catch (Exception e) // ��������ж�
					{
						System.out.println("server2 exception handling");
						storage_servers[index_server2].usable = false;
						SetPropertiesUsable(index_server2);
						index_server2 = ChooseStorageServer(index_server1, len); // ��һ����server1��һ���Ľ��
						if (index_server1 == -1 && index_server2 == -1) // û�ҵ����ʵĽڵ㣬����ʧ��
						{
							dos.writeInt(-1);
							break;
						}
						upload(index_server2, my_file, temp_file); // ���򣬿��Լ�������
					}

				}
				dos.writeInt(1); // �ļ��ϴ��ɹ�
				dos.writeUTF(my_file.uuid.toString());

				if (index_server1 != -1) // ���´洢�ռ䣬���������µ���Ϣ�������ļ���
				{
					my_file.path1 = storage_servers[index_server1].root_folder;
					StorageDecrease(index_server1, len);
					SetPropertiesVolume(index_server1);
				}
				if (index_server2 != -1) {
					my_file.path2 = storage_servers[index_server2].root_folder;
					StorageDecrease(index_server2, len);
					SetPropertiesVolume(index_server2);
				}

				// �����ļ���Ϣ��FileServerĿ¼��
				File output_file = new File(FTServer.share, my_file.uuid.toString());
				PrintStream ps = new PrintStream(new FileOutputStream(output_file));
				ps.println("" + my_file.name);
				ps.println("" + my_file.uuid);
				ps.println("" + my_file.len);
				ps.println("" + my_file.path1);
				ps.println("" + my_file.path2);
				ps.println("" + index_server1);
				ps.println("" + index_server2);
				ps.flush();
				ps.close();

				files_stored.add(my_file.uuid.toString()); // ������ϴ����ļ�
				temp_file.delete(); // ɾ����ʱ�ļ�

				break;
			case 2: // file download �ļ�����
				target_file = null;
				file_uuid = dis.readUTF();// uuid
				it = files_stored.iterator();
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (temp.equals(file_uuid)) {
						target_file = new File(FTServer.share, file_uuid); // �ҵ���Ҫ���ص��ļ�
					}
				}
				if (target_file == null) // û���ҵ�Ҫ���ص��ļ�
				{
					dos.writeInt(-1);
					dos.flush();
				} else {
					dos.writeInt(1);
					dos.flush();

					// ��������ȡ�й�Ŀ���ļ�����Ϣ
					BufferedReader br = new BufferedReader(new FileReader(target_file));
					String target_file_name = br.readLine();// name
					String target_file_uuid = br.readLine();// uuid
					long target_file_len = Integer.parseInt(br.readLine());
					path1 = br.readLine();
					path2 = br.readLine();

					index_server1 = Integer.parseInt(br.readLine());
					index_server2 = Integer.parseInt(br.readLine());

					dos.writeUTF(target_file_name); // ���û�����
					dos.flush();

					dos.writeLong(target_file_len); // ���û���ʾ�ļ��ĳ���
					dos.flush();
					// ʵ��ѡ��洢������
					// ������㶼�����õ����
					if (storage_servers[index_server1].usable == false
							&& storage_servers[index_server2].usable == false)
						dos.writeInt(-1);
					else if (storage_servers[index_server1].usable == true) // �����õ�һ��
					{
						dos.writeInt(1); // �нڵ����
						try {
							r = rr = 0;
							Socket s = new Socket(storage_servers[index_server1].ip,
									storage_servers[index_server1].port);
							dos_storage1 = new DataOutputStream(s.getOutputStream());
							dis_storage1 = new DataInputStream(s.getInputStream());
							dos_storage1.writeInt(2);
							dos_storage1.writeUTF(target_file_uuid + target_file_name);
							while (r < target_file_len) {
								if (target_file_len - r >= buffer.length) {
									rr = dis_storage1.read(buffer, 0, buffer.length); // �����Դ洢������������
								} else {
									rr = dis_storage1.read(buffer, 0, (int) (target_file_len - r));
								}
								dos.write(buffer, 0, rr); // д���û�
								dos.flush(); // ˢ��
								r = r + rr;
							}
							dos.writeInt(1); // �ɹ�����
							s.close();
						} catch (Exception e) {
							System.out.println("handling the download interruption exception");
							// Ҫ����2�ŷ�����Ҳ崻�
							if (storage_servers[index_server2].usable == true) // �ϵ��ش�
							{
								Socket s = new Socket(storage_servers[index_server2].ip,
										storage_servers[index_server2].port);
								dos_storage2 = new DataOutputStream(s.getOutputStream());
								dis_storage2 = new DataInputStream(s.getInputStream());
								dos_storage2.writeInt(5); // �ϵ��ش�
								dos_storage2.writeUTF(target_file_uuid + target_file_name);
								dos_storage2.writeLong(r); // �ϵ�λ��
								while (r < target_file_len) {
									if (target_file_len - r >= buffer.length) {
										rr = dis_storage2.read(buffer, 0, buffer.length); // �����Դ洢������������
									} else {
										rr = dis_storage2.read(buffer, 0, (int) (target_file_len - r));
									}
									dos.write(buffer, 0, rr); // д���û�
									dos.flush(); // ˢ��
									r = r + rr;
								}
								dos.writeInt(1); // �ɹ�����
								s.close();
							}
						}

					} else if (storage_servers[index_server2].usable == true) // 1�����ö�2����
					{
						dos.writeInt(1); // ��֪�ͻ����нڵ����
						try {
							r = rr = 0;
							Socket s = new Socket(storage_servers[index_server2].ip,
									storage_servers[index_server2].port);
							dos_storage2 = new DataOutputStream(s.getOutputStream());
							dis_storage2 = new DataInputStream(s.getInputStream());
							dos_storage2.writeInt(2);
							dos_storage2.writeUTF(target_file_uuid + target_file_name);
							while (r < target_file_len) {
								if (target_file_len - r >= buffer.length) {
									rr = dis_storage2.read(buffer, 0, buffer.length); // �����Դ洢��������
								} else {
									rr = dis_storage2.read(buffer, 0, (int) (target_file_len - r));
								}

								r = r + rr;
								dos.write(buffer, 0, rr); // д���û�
								dos.flush(); // ˢ��
							}
							s.close();
						} catch (Exception e) {
							dos.writeInt(-1); // ����ʧ��
						}

					}
				}
				break;
			case 3: // remove ɾ���ڵ��ϵ��ļ�
				target_file = null;
				file_uuid = dis.readUTF();
				// �ҵ���Ӧ�ļ���FileServer�еļ�¼
				it = files_stored.iterator();
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (temp.equals(file_uuid)) {
						target_file = new File(FTServer.share, file_uuid);
					}
				}
				if (target_file == null) // û�ҵ�
				{
					dos.writeInt(-1); // ��֪�ͻ���û���ҵ�
					dos.flush();
				} else // �ҵ���
				{
					dos.writeInt(1); // ��֪�ͻ����ҵ���
					dos.flush();

					// ��ȡ�洢�ļ���Ϣ���ļ�����
					BufferedReader br = new BufferedReader(new FileReader(target_file));
					String target_file_name = br.readLine();// name
					String target_file_uuid = br.readLine();// uuid
					long target_file_len = Integer.parseInt(br.readLine());

					path1 = br.readLine();
					path2 = br.readLine();

					index_server1 = Integer.parseInt(br.readLine());
					index_server2 = Integer.parseInt(br.readLine());

					br.close();

					// �����洢��㶼���ߣ�ֱ��ɾ��
					if (index_server1 != -1 && storage_servers[index_server1].usable == true && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						remove(index_server1, target_file_name, target_file_uuid);
						remove(index_server2, target_file_name, target_file_uuid);
					}
					// �ļ���֮ǰ��û����ɱ��ݴ洢��,ֻ��һ���ڵ㱣�����ݣ��������ɾ��
					else if (index_server2 == -1 && index_server1 != -1
							&& storage_servers[index_server1].usable == true) {
						remove(index_server1, target_file_name, target_file_uuid);
					}
					// �ļ���֮ǰ��û����ɱ��ݴ洢��,ֻ��һ���ڵ㱣�����ݣ��������ɾ��
					else if (index_server1 == -1 && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						remove(index_server2, target_file_name, target_file_uuid);
					} else // �����洢��㲻ͬʱ���ߣ�������ɾ��
					{
						dos.writeInt(-1);
						break;
					}
					dos.writeInt(1); // ��֪�ͻ��˳ɹ�ɾ��
					dos.writeUTF(target_file_name); // ����ɾ���ļ����ļ�����֪�ͻ���
					target_file.delete();
					files_stored.remove(target_file_uuid); // ɾ������������������ļ�¼
					if (index_server1 != -1) // ͬ�������ļ�
					{
						StorageIncrease(index_server1, target_file_len);
						SetPropertiesVolume(index_server1);
					}
					if (index_server2 != -1) // ͬ�������ļ�
					{
						StorageIncrease(index_server2, target_file_len);
						SetPropertiesVolume(index_server2);
					}
				}

				break;
			case 4: // rename ������
				target_file = null;
				file_uuid = dis.readUTF();
				String new_name = dis.readUTF();
				it = files_stored.iterator();
				while (it.hasNext()) // Ѱ���Ƿ��и�uuid��Ӧ���ļ�
				{
					String temp = (String) it.next();
					if (temp.equals(file_uuid)) {
						target_file = new File(FTServer.share, file_uuid);
					}
				}
				if (target_file == null) // û�и��ļ�
				{
					dos.writeInt(-1); // ��֪�ͻ���δ�ҵ�ָ���ļ�
					dos.flush();
				} else // �ҵ����ļ�
				{
					dos.writeInt(1); // ��֪�ͻ����ҵ���ָ���ļ�
					dos.flush();

					// ��ȡ�ļ���Ϣ
					BufferedReader br = new BufferedReader(new FileReader(target_file));
					String target_file_name = br.readLine();// name
					String target_file_uuid = br.readLine();// uuid
					long target_file_len = Integer.parseInt(br.readLine());
					String target_file_path1 = br.readLine();
					String target_file_path2 = br.readLine();

					index_server1 = Integer.parseInt(br.readLine());
					index_server2 = Integer.parseInt(br.readLine());
					br.close();

					// �����洢��㶼���ߣ�ֱ�Ӹ���
					if (index_server1 != -1 && storage_servers[index_server1].usable == true && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						rename(index_server1, target_file_name, target_file_uuid, new_name);
						rename(index_server2, target_file_name, target_file_uuid, new_name);
					}
					// �ļ���֮ǰ��û����ɱ��ݴ洢��,������и���
					else if (index_server2 == -1 && index_server1 != -1
							&& storage_servers[index_server1].usable == true) {
						rename(index_server1, target_file_name, target_file_uuid, new_name);
					}
					// �ļ���֮ǰ��û����ɱ��ݴ洢��,������и���
					else if (index_server1 == -1 && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						rename(index_server2, target_file_name, target_file_uuid, new_name);
					} else // �����洢��㲻ͬʱ���ߣ����������
					{
						dos.writeInt(-1);
						break;
					}
					dos.writeInt(1);
					// ��д�����ļ���Ϣ�ļ�
					ps = new PrintStream(new FileOutputStream(new File(FTServer.share, file_uuid)));
					ps.println(new_name);
					ps.println(file_uuid);
					ps.println(target_file_len);
					ps.println(target_file_path1);
					ps.println(target_file_path2);
					ps.println(index_server1);
					ps.println(index_server2);
					ps.flush();
					ps.close();
				}
				break;
			case 5: // ����ļ���
				dos.writeInt(files_stored.size());
				break;
			case 6: // ���ʵ�ʿ��õĽ����
				int count = 0;
				for (int i = 0; i < storage_num; i++) {
					if (storage_servers[i].usable == true)
						count++;
				}
				dos.writeInt(count);
				break;
			case 7: // ��ô洢�ļ����б�
				dos.writeInt(files_stored.size());
				it = files_stored.iterator();
				while (it.hasNext()) {
					dos.writeUTF((String) it.next());
				}
				break;
			case 8: // ������н���������Ϣ
				dos.writeInt(storage_num);
				for (int i = 0; i < storage_num; i++) {
					dos.writeUTF(storage_servers[i].name);
					dos.writeUTF(storage_servers[i].ip);
					dos.writeInt(storage_servers[i].port);
					dos.writeUTF(storage_servers[i].total);
					dos.writeUTF(storage_servers[i].volume);
					dos.writeBoolean(storage_servers[i].usable);
					dos.writeUTF(storage_servers[i].root_folder);
				}
				break;
			case 9:
				int target_index = dis.readInt();
				String new_total = dis.readUTF();
				if (VolumeCompare(new_total, storage_servers[target_index].volume)) {
					storage_servers[target_index].total = new_total;
					FileInputStream fis = new FileInputStream("storage" + target_index + ".properties"); // ���������ļ�
					p.load(fis);
					fis.close();
					p.setProperty("total", "" + storage_servers[target_index].total); // �޸������ļ�
					FileOutputStream fos = new FileOutputStream("storage" + target_index + ".properties"); // ����д�������ļ�
					p.store(fos, "Update");
					fos.close();
					dos.writeInt(1);
				} else
					dos.writeInt(-1);
				;

			}

		} catch (Exception e) {
			if (e instanceof EOFException) {
				System.out.println(client + " disconnected");
			} else {
				e.printStackTrace();
			}

		}
	}
}
