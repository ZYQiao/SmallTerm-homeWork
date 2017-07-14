import java.net.*;
import java.io.*;
import java.util.*;

//多线程线程池
public class AdvancedSupport implements IOStrategy {
	private ArrayList threads = new ArrayList();		//存储线程的容器
	private final int INIT_THREADS = 10;				//初始线程数
	private final int MAX_THREADS = 100;				//最大线程数
	private IOStrategy ios = null;

	public AdvancedSupport(IOStrategy ios) {
		this.ios = ios;

		for (int i = 0; i < INIT_THREADS; i++) {		//创建并添加线程
			IOThread t = new IOThread(ios);
			t.start();
			threads.add(t);
		}
		try {
			Thread.sleep(300);
		} catch (Exception e) {
		}
	}
	//线程池
	//实现接口功能
	public void service(Socket socket) {
		IOThread t = null;
		boolean found = false;
		for (int i = 0; i < threads.size(); i++) {		//找到一个空闲的线程
			t = (IOThread) threads.get(i);
			if (t.isIdle()) {
				found = true;
				break;
			}
		}
		if (!found) {									//没找到就创建一个新的									
			t = new IOThread(ios);
			t.start();
			try {
				Thread.sleep(30);
			} catch (Exception e) {
			}
			threads.add(t);
		}

		t.setSocket(socket);
	}
}

class IOThread extends Thread {							//新定义的线程类
	private Socket socket = null;
	private IOStrategy ios = null;

	public IOThread(IOStrategy ios) {
		this.ios = ios;
	}

	public boolean isIdle() {							//判断进程空闲
		return socket == null;
	}

	public synchronized void setSocket(Socket socket) {	//向进程添加Socket
		this.socket = socket;
		notify();
	}

	public synchronized void run() {					//重写run
		while (true) {
			try {
				wait();
				ios.service(socket);
				socket = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
};
