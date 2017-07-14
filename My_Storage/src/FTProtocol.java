import java.net.Socket;
import java.io.*;
import java.util.*;
import java.math.*;

//主服务器与客户端之间的协议
public class FTProtocol implements IOStrategy {

	int storage_num = 3; // 初始的结点数量
	HashSet<String> files_stored = new HashSet<String>(); // 保存结点上文件名的字符容器
	My_Server[] storage_servers = new My_Server[storage_num]; // 保存（存储结点服务器类）的一个数组

	// 功能：将实际容积换算到合适的单位
	// 参数：以B为单位的字符串
	// 返回值：以KB或MB或GB为单位的字符串
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

	// 功能：将指定的字符串类型的容积转为float类型
	// 参数：指定的当前容积
	// 返回值：float类型的当前容积(以B为单位)

	float ChangeVolumeToFloat(String volume) {
		float capacity = 0;
		char c = '\0';
		boolean flag = false; // 小数点之后是true，小数点之前是false
		float count = 10.0f;
		for (int i = 0; i < volume.length(); i++) {
			c = volume.charAt(i);
			if (c <= '9' && c >= '0' && flag == false) // 整数部分
			{
				capacity = capacity * 10 + (c - '0');
			} else if (c <= '9' && c >= '0' && flag == true) // 小数部分
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
		// 统一化成字节
		if (c == 'G')
			capacity *= (1024 * 1024 * 1024);
		else if (c == 'M')
			capacity *= (1024 * 1024);
		else if (c == 'K')
			capacity *= 1024;
		return capacity;
	}

	// 功能：判断当前容积是否能存下指定的大小的文件
	// volume是存储服务器的可用容积(单位待读取)，len是文件的长度(B)
	// 参数：存储节点的当前容积和文件的大小，
	// 返回值：一个数，如果是正的，表示够存，并且这个数是存完后的余下容积
	// 如果返回负数，则表示不够存

	float SpaceEnough(String volume, long len) {
		float capacity = ChangeVolumeToFloat(volume);
		return capacity - len;
	}

	// 功能：比较两个String类型的容积
	// 输入 ：两个string类型的容积
	// 输出 ：前者大就是true，反置就是false
	boolean VolumeCompare(String volume1, String volume2) {
		float v1 = ChangeVolumeToFloat(volume1);
		float v2 = ChangeVolumeToFloat(volume2);
		if (v1 - v2 >= 0)
			return true;
		else
			return false;
	}

	// 功能：获得指定编号的存储节点的容积(String类型)
	// 参数：指定存储节点的编号
	// 返回值 ：指定存储节点的容积
	String VolumeOf(int index) {
		if (index < 0)
			return "" + 0;
		else
			return storage_servers[index].volume;
	}

	// 功能：采用负载平衡选择合适的结点
	// 参数：一个一定不需要的存储服务器的编号，和待存文件的大小
	// 返回值:可以使用的存储服务器的编号
	int ChooseStorageServer(int exclusion, long file_len) {
		int pre = -1;
		for (int i = 0; i < storage_num; i++) {
			// 当前下标不等于exclusion并且该节点可用并且结点空间够用并且该节点是所有结点中可用容积最大的(负载平衡)
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

	// 功能： 文件上传后，对指定存储节点的容积进行减小
	// 参数： 存储节点的编号，文件的长度(B)
	// 返回值：无
	void StorageDecrease(int index, long file_len) {
		float remainder = SpaceEnough(storage_servers[index].volume, file_len); // 存储文件后的剩余空间
		BigDecimal bd = new BigDecimal(remainder);
		bd.setScale(2, BigDecimal.ROUND_HALF_UP);// 小数点后两位，四舍五入
		storage_servers[index].volume = Normalize("" + bd + "B");
	}

	// 功能： 文件下载后，对指定存储节点的容积进行增加
	// 参数： 存储节点的编号，文件的长度(B)
	// 返回值：无
	void StorageIncrease(int index, long file_len) {
		float capacity = ChangeVolumeToFloat(storage_servers[index].volume);
		capacity += file_len; // 删除文件后多出来的空间
		BigDecimal bd = new BigDecimal(capacity);
		bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		storage_servers[index].volume = Normalize("" + bd + "B");
	}

	// 功能：对存储节点上的文件进行重命名
	// 参数：存储节点编号，文件名，文件uuid，文件的新名字
	// 返回值：true改名成功，false改名失败
	boolean rename(int index_server, String target_file_name, String target_file_uuid, String new_name)
			throws Exception {
		// 建立连接，并获得流
		Socket s = new Socket(storage_servers[index_server].ip, storage_servers[index_server].port);
		DataOutputStream dos_storage = new DataOutputStream(s.getOutputStream());
		DataInputStream dis_storage = new DataInputStream(s.getInputStream());
		dos_storage.writeInt(4); // 告诉存储结点即将改名
		dos_storage.writeUTF(target_file_uuid + target_file_name); // 传输uuid文件名
		dos_storage.writeUTF(new_name); // 传输新的文件名
		dos_storage.writeUTF(target_file_uuid); // 传输uuid
		dos_storage.flush(); // 清空缓存
		int signal = dis_storage.readInt(); // 判断是不是改名成功
		s.close();
		if (signal < 0)
			return false;
		else
			return true;
	}

	// 功能：删除存储结点上的指定文件
	// 参数：文件的编号，文件的名字，文件uuid
	// 返回值：无
	void remove(int index_server, String target_file_name, String target_file_uuid) throws Exception {
		// 创建链接并获得流
		Socket s = new Socket(storage_servers[index_server].ip, storage_servers[index_server].port);
		DataOutputStream dos_storage = new DataOutputStream(s.getOutputStream());
		DataInputStream dis_storage = new DataInputStream(s.getInputStream());
		dos_storage.writeInt(3); // 告诉存储节点要进行删除操作
		dos_storage.writeUTF(target_file_uuid + target_file_name); // 传输 uuis文件名
		s.close();
	}

	// 功能：设置存储节点属性文件的volume属性
	// 参数 ：存储结点的编号
	// 返回值：无
	void SetPropertiesVolume(int index_server) throws Exception {

		Properties p = new Properties();
		FileInputStream fis = new FileInputStream("storage" + index_server + ".properties"); // 载入属性文件
		p.load(fis);
		fis.close();
		p.setProperty("volume", storage_servers[index_server].volume); // 修改属性文件
		FileOutputStream fos = new FileOutputStream("storage" + index_server + ".properties"); // 重新写回属性文件
		p.store(fos, "Update");
		fos.close();
	}

	// 功能：设置存储节点属性文件的usable属性
	// 参数 ：存储结点的编号
	// 返回值：无
	void SetPropertiesUsable(int index_server) throws Exception {

		Properties p = new Properties();
		FileInputStream fis = new FileInputStream("/storage" + index_server + ".properties"); // 载入属性文件
		p.load(fis);
		fis.close();
		p.setProperty("usable", "" + storage_servers[index_server].usable); // 修改属性文件
		FileOutputStream fos = new FileOutputStream("/storage" + index_server + ".properties"); // 重新写回属性文件
		p.store(fos, "Update");
		fos.close();
	}

	// 功能：上传文件到存储节点
	// 参数：服务器编号，指定文件的引用，临时文件
	// 返回值：无
	void upload(int index_server, My_File my_file, File temp_file) throws Exception {
		// 创建链接并获得流
		Socket s = new Socket(storage_servers[index_server].ip, storage_servers[index_server].port);
		DataOutputStream dos_storage = new DataOutputStream(s.getOutputStream());
		DataInputStream dis_storage = new DataInputStream(s.getInputStream());
		dos_storage.writeInt(1);
		dos_storage.writeUTF(my_file.uuid.toString() + my_file.name); // 传输uuid文件名
		dos_storage.writeLong(my_file.len); // 传输文件长度
		FileInputStream temp_fis = new FileInputStream(temp_file);
		BufferedInputStream temp_bis = new BufferedInputStream(temp_fis);// 用于读取本地文件的流
		int r = 0;
		int rr = 0;
		byte[] buffer = new byte[4096];
		try {
			while ((rr = temp_bis.read(buffer)) != -1) { // 从本地
				dos_storage.write(buffer, 0, rr); // 写给存储节点
				dos_storage.flush();
				r = r + rr;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			temp_bis.close();
		}
	}

	// 提供协议服务
	public void service(Socket socket) {
		String client = socket.getInetAddress().getHostName() + "(" + socket.getInetAddress().getHostAddress() + ")";
		String file_uuid;
		File target_file = null;
		Iterator it;
		String path1, path2;
		My_File my_file = new My_File(); // 自定义的用于描述文件的类
		try {

			Properties p = new Properties();
			for (int i = 0; i < storage_num; i++) // 开始时主服务器读取有关各个存储节点的属性
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
			String[] files = (FTServer.share).list();// 获得存储在节点上的文件的名称列表
			for (int i = 0; i < files.length; i++) {
				files_stored.add(files[i]);
			}
			System.out.println("Storage is connected successfully");

			// 主服务器和客户端交互的流
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			DataOutputStream dos = new DataOutputStream(os);

			int index_server1, index_server2; // 两个存储节点编号

			// 主服务器和存储节点交互的流
			DataOutputStream dos_storage1 = null;
			DataInputStream dis_storage1 = null;
			DataOutputStream dos_storage2 = null;
			DataInputStream dis_storage2 = null;
			String filename = null;
			long len = 0;

			byte[] buffer = new byte[4096]; // 缓冲

			long r = 0;
			int rr = 0;

			int command = dis.readInt(); // 获取来自客户端的指令
			switch (command) {
			case 1: // file upload 文件上传
				my_file.name = filename = dis.readUTF(); // 文件名
				my_file.len = len = dis.readLong(); // 文件大小
				my_file.uuid = UUID.randomUUID(); // 产生uuid
				index_server1 = ChooseStorageServer(-1, len); // 选择节点1
				index_server2 = ChooseStorageServer(index_server1, len); // 选择节点2

				if (index_server1 == -1 && index_server2 == -1) // 没找到可用的节点
				{
					dos.writeInt(-1);
					break;
				}
				// 如果找到了，那么index_server1肯定有，index_server2需要进一步判断

				// 在FileServer处生成临时文件
				dos.writeInt(1);
				File temp_file = new File(FTServer.share, my_file.uuid.toString() + "temp");
				FileOutputStream temp_fos = new FileOutputStream(temp_file);
				BufferedOutputStream temp_bos = new BufferedOutputStream(temp_fos);
				r = 0;
				rr = 0;
				try {
					while (r < len) {
						if (len - r >= buffer.length) { // 从用户那里读入
							rr = dis.read(buffer, 0, buffer.length);
						} else {
							rr = dis.read(buffer, 0, (int) (len - r));
						}
						r = r + rr;
						temp_bos.write(buffer, 0, rr); // Server写到指定的位置
					}
					temp_bos.close();
				} catch (Exception e) // 考虑网络异常，用户连接中断
				{
					temp_bos.close();
					temp_file.delete();
					System.out.println("the client is offline");
					break;
				}

				try {
					upload(index_server1, my_file, temp_file); // 上传
				} catch (Exception e) // 如果传输中断
				{
					System.out.println("server1 exception handling");
					storage_servers[index_server1].usable = false;
					SetPropertiesUsable(index_server1);
					index_server1 = ChooseStorageServer(index_server2, len);
					if (index_server1 == -1 && index_server2 == -1) // 找不到替代1的结点并且2号结点也不可用。传输失败
					{
						dos.writeInt(-1);
						break;
					}
					if (index_server1 != -1) // 找到了替代产生异常的结点，继续传输
					{
						upload(index_server1, my_file, temp_file);
					}
				}

				if (index_server2 != -1) // 找到了备份存储结点2
				{
					try {
						upload(index_server2, my_file, temp_file);
					} catch (Exception e) // 如果传输中断
					{
						System.out.println("server2 exception handling");
						storage_servers[index_server2].usable = false;
						SetPropertiesUsable(index_server2);
						index_server2 = ChooseStorageServer(index_server1, len); // 找一个和server1不一样的结点
						if (index_server1 == -1 && index_server2 == -1) // 没找到合适的节点，传输失败
						{
							dos.writeInt(-1);
							break;
						}
						upload(index_server2, my_file, temp_file); // 否则，可以继续传输
					}

				}
				dos.writeInt(1); // 文件上传成功
				dos.writeUTF(my_file.uuid.toString());

				if (index_server1 != -1) // 更新存储空间，并保存最新的信息到属性文件中
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

				// 保存文件信息到FileServer目录下
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

				files_stored.add(my_file.uuid.toString()); // 添加新上传的文件
				temp_file.delete(); // 删除临时文件

				break;
			case 2: // file download 文件下载
				target_file = null;
				file_uuid = dis.readUTF();// uuid
				it = files_stored.iterator();
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (temp.equals(file_uuid)) {
						target_file = new File(FTServer.share, file_uuid); // 找到了要下载的文件
					}
				}
				if (target_file == null) // 没有找到要下载的文件
				{
					dos.writeInt(-1);
					dos.flush();
				} else {
					dos.writeInt(1);
					dos.flush();

					// 服务器读取有关目标文件的信息
					BufferedReader br = new BufferedReader(new FileReader(target_file));
					String target_file_name = br.readLine();// name
					String target_file_uuid = br.readLine();// uuid
					long target_file_len = Integer.parseInt(br.readLine());
					path1 = br.readLine();
					path2 = br.readLine();

					index_server1 = Integer.parseInt(br.readLine());
					index_server2 = Integer.parseInt(br.readLine());

					dos.writeUTF(target_file_name); // 向用户回显
					dos.flush();

					dos.writeLong(target_file_len); // 向用户显示文件的长度
					dos.flush();
					// 实现选择存储服务器
					// 两个结点都不可用的情况
					if (storage_servers[index_server1].usable == false
							&& storage_servers[index_server2].usable == false)
						dos.writeInt(-1);
					else if (storage_servers[index_server1].usable == true) // 优先用第一个
					{
						dos.writeInt(1); // 有节点可用
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
									rr = dis_storage1.read(buffer, 0, buffer.length); // 读来自存储服务器的内容
								} else {
									rr = dis_storage1.read(buffer, 0, (int) (target_file_len - r));
								}
								dos.write(buffer, 0, rr); // 写给用户
								dos.flush(); // 刷新
								r = r + rr;
							}
							dos.writeInt(1); // 成功下载
							s.close();
						} catch (Exception e) {
							System.out.println("handling the download interruption exception");
							// 要考虑2号服务器也宕机
							if (storage_servers[index_server2].usable == true) // 断点重传
							{
								Socket s = new Socket(storage_servers[index_server2].ip,
										storage_servers[index_server2].port);
								dos_storage2 = new DataOutputStream(s.getOutputStream());
								dis_storage2 = new DataInputStream(s.getInputStream());
								dos_storage2.writeInt(5); // 断点重传
								dos_storage2.writeUTF(target_file_uuid + target_file_name);
								dos_storage2.writeLong(r); // 断点位置
								while (r < target_file_len) {
									if (target_file_len - r >= buffer.length) {
										rr = dis_storage2.read(buffer, 0, buffer.length); // 读来自存储服务器的内容
									} else {
										rr = dis_storage2.read(buffer, 0, (int) (target_file_len - r));
									}
									dos.write(buffer, 0, rr); // 写给用户
									dos.flush(); // 刷新
									r = r + rr;
								}
								dos.writeInt(1); // 成功下载
								s.close();
							}
						}

					} else if (storage_servers[index_server2].usable == true) // 1不能用而2能用
					{
						dos.writeInt(1); // 告知客户端有节点可用
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
									rr = dis_storage2.read(buffer, 0, buffer.length); // 读来自存储结点的内容
								} else {
									rr = dis_storage2.read(buffer, 0, (int) (target_file_len - r));
								}

								r = r + rr;
								dos.write(buffer, 0, rr); // 写给用户
								dos.flush(); // 刷新
							}
							s.close();
						} catch (Exception e) {
							dos.writeInt(-1); // 下载失败
						}

					}
				}
				break;
			case 3: // remove 删除节点上的文件
				target_file = null;
				file_uuid = dis.readUTF();
				// 找到对应文件在FileServer中的记录
				it = files_stored.iterator();
				while (it.hasNext()) {
					String temp = (String) it.next();
					if (temp.equals(file_uuid)) {
						target_file = new File(FTServer.share, file_uuid);
					}
				}
				if (target_file == null) // 没找到
				{
					dos.writeInt(-1); // 告知客户端没有找到
					dos.flush();
				} else // 找到了
				{
					dos.writeInt(1); // 告知客户端找到了
					dos.flush();

					// 读取存储文件信息的文件的流
					BufferedReader br = new BufferedReader(new FileReader(target_file));
					String target_file_name = br.readLine();// name
					String target_file_uuid = br.readLine();// uuid
					long target_file_len = Integer.parseInt(br.readLine());

					path1 = br.readLine();
					path2 = br.readLine();

					index_server1 = Integer.parseInt(br.readLine());
					index_server2 = Integer.parseInt(br.readLine());

					br.close();

					// 两个存储结点都在线，直接删除
					if (index_server1 != -1 && storage_servers[index_server1].usable == true && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						remove(index_server1, target_file_name, target_file_uuid);
						remove(index_server2, target_file_name, target_file_uuid);
					}
					// 文件在之前并没有完成备份存储的,只有一个节点保有数据，对其进行删除
					else if (index_server2 == -1 && index_server1 != -1
							&& storage_servers[index_server1].usable == true) {
						remove(index_server1, target_file_name, target_file_uuid);
					}
					// 文件在之前并没有完成备份存储的,只有一个节点保有数据，对其进行删除
					else if (index_server1 == -1 && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						remove(index_server2, target_file_name, target_file_uuid);
					} else // 两个存储结点不同时在线，不允许删除
					{
						dos.writeInt(-1);
						break;
					}
					dos.writeInt(1); // 告知客户端成功删除
					dos.writeUTF(target_file_name); // 将被删除文件的文件名告知客户端
					target_file.delete();
					files_stored.remove(target_file_uuid); // 删除保存在主服务器里的记录
					if (index_server1 != -1) // 同步属性文件
					{
						StorageIncrease(index_server1, target_file_len);
						SetPropertiesVolume(index_server1);
					}
					if (index_server2 != -1) // 同步属性文件
					{
						StorageIncrease(index_server2, target_file_len);
						SetPropertiesVolume(index_server2);
					}
				}

				break;
			case 4: // rename 重命名
				target_file = null;
				file_uuid = dis.readUTF();
				String new_name = dis.readUTF();
				it = files_stored.iterator();
				while (it.hasNext()) // 寻找是否有该uuid对应的文件
				{
					String temp = (String) it.next();
					if (temp.equals(file_uuid)) {
						target_file = new File(FTServer.share, file_uuid);
					}
				}
				if (target_file == null) // 没有该文件
				{
					dos.writeInt(-1); // 告知客户端未找到指定文件
					dos.flush();
				} else // 找到该文件
				{
					dos.writeInt(1); // 告知客户端找到了指定文件
					dos.flush();

					// 读取文件信息
					BufferedReader br = new BufferedReader(new FileReader(target_file));
					String target_file_name = br.readLine();// name
					String target_file_uuid = br.readLine();// uuid
					long target_file_len = Integer.parseInt(br.readLine());
					String target_file_path1 = br.readLine();
					String target_file_path2 = br.readLine();

					index_server1 = Integer.parseInt(br.readLine());
					index_server2 = Integer.parseInt(br.readLine());
					br.close();

					// 两个存储结点都在线，直接改名
					if (index_server1 != -1 && storage_servers[index_server1].usable == true && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						rename(index_server1, target_file_name, target_file_uuid, new_name);
						rename(index_server2, target_file_name, target_file_uuid, new_name);
					}
					// 文件在之前并没有完成备份存储的,对其进行改名
					else if (index_server2 == -1 && index_server1 != -1
							&& storage_servers[index_server1].usable == true) {
						rename(index_server1, target_file_name, target_file_uuid, new_name);
					}
					// 文件在之前并没有完成备份存储的,对其进行改名
					else if (index_server1 == -1 && index_server2 != -1
							&& storage_servers[index_server2].usable == true) {
						rename(index_server2, target_file_name, target_file_uuid, new_name);
					} else // 两个存储结点不同时在线，不允许改名
					{
						dos.writeInt(-1);
						break;
					}
					dos.writeInt(1);
					// 重写覆盖文件信息文件
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
			case 5: // 获得文件数
				dos.writeInt(files_stored.size());
				break;
			case 6: // 获得实际可用的结点数
				int count = 0;
				for (int i = 0; i < storage_num; i++) {
					if (storage_servers[i].usable == true)
						count++;
				}
				dos.writeInt(count);
				break;
			case 7: // 获得存储文件的列表
				dos.writeInt(files_stored.size());
				it = files_stored.iterator();
				while (it.hasNext()) {
					dos.writeUTF((String) it.next());
				}
				break;
			case 8: // 获得所有结点的所有信息
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
					FileInputStream fis = new FileInputStream("storage" + target_index + ".properties"); // 载入属性文件
					p.load(fis);
					fis.close();
					p.setProperty("total", "" + storage_servers[target_index].total); // 修改属性文件
					FileOutputStream fos = new FileOutputStream("storage" + target_index + ".properties"); // 重新写回属性文件
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
