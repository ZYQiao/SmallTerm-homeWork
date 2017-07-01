package a2;

import java.io.IOException;
import java.util.Vector;
import java.io.FileReader;

public class ReadFile {
	private static FileReader fileReader;
	public static String readTxtFile(String filePath) throws IOException {
		Vector<Character> f = new Vector<Character>();
		String s = new String();
		fileReader = new FileReader(filePath);
		int ch = 0;
		while ((ch = fileReader.read()) != -1) {
			if ((char) ch == '0' || (char) ch == '1')
				f.add((char) ch);
		}
		fileReader.close();
		for (char c : f)
			s += c;
		return s;
	}

	public static int getMost1(String filePath) throws IOException {
		String s = readTxtFile(filePath);
		int n = (int) Math.sqrt(s.length());
		int rowMax = 0, colMax = 0, parMax = 0;
		char tab[][] = new char[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tab[i][j] = s.charAt(i * n + j);
			}
		}
		for (int i = 0; i < n; i++) {
			int row = 0;
			for (int j = 0; j < n; j++) {
				if (tab[i][j] == '1')
					row++;
			}
			rowMax = Math.max(rowMax, row);
		}
		for (int i = 0; i < n; i++) {
			int col = 0;
			for (int j = 0; j < n; j++) {
				if (tab[j][i] == '1')
					col++;
			}
			colMax = Math.max(colMax, col);
		}
		int parMax1 = 0;
		for (int num = 0; num <= 2 * n; num++) {
			int par = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (tab[i][j] == '1' && i + j == num)
						par++;
				}
			}
			parMax1 = Math.max(parMax1, par);
		}
		int parMax2 = 0;
		for (int num = -n; num <= n; num++) {
			int par = 0;
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (tab[i][j] == '1' && i - j == num)
						par++;
				}
			}
			parMax2 = Math.max(parMax2, par);
		}
		parMax = Math.max(parMax1, parMax2);
		return Math.max(Math.max(rowMax, colMax), parMax);
	}

	public static void main(String argv[]) throws IOException {
		String filePath = "/Users/yuqiao/workspace/a2/src/a2/a.txt";
		// "res/";
		int t = getMost1(filePath);
		System.out.println(t);
	}

}
