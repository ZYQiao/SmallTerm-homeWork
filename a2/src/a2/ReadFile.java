package a2;

import java.io.IOException;  
import java.io.FileReader;

public class ReadFile {
	/**
	 * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
	 * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
	 * 
	 * @param filePath
	 * @throws FileNotFoundException
	 */
	public static void readTxtFile(String filePath) throws IOException {
		FileReader file = new FileReader(filePath);
		int eight[][] = new int[8][8];
		for(int i = 0;i<8;i++ ){
			for(int j = 0;j < 8;j++){
					eight[i][j]=file.read();
				System.out.print((char)eight[i][j]);
				System.out.print(" ");
			}
		}
		
	}

	public static void main(String argv[]) throws IOException {
		String filePath = "/Users/yuqiao/workspace/a2/src/a2/a.txt";
		// "res/";
		readTxtFile(filePath);
		
	}

}
