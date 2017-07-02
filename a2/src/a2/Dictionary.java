package a2;

import java.util.Scanner;
import java.io.IOException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {
	private static FileWriter fileWriter;
	private static Map<String, String> map = new HashMap<String, String>();
	private static String path = "/Users/yuqiao/workspace/a2/src/a2/Dictionary.txt";

	public static void add(String word, String meanning) throws IOException {
		fileWriter = new FileWriter(path);
		if (map.equals(word)) {
			map.replace(word, meanning);
		} else {
			map.put(word, meanning);
		}
		fileWriter.write("");
		for (Object k : map.keySet()) {
			fileWriter.write(k + " " + map.get(k));
		}
		fileWriter.close();
	}

	public static String find(String word) {
		return map.get(word);
	}


	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		while (true) {
			System.out.println("1.添加单词");
			System.out.println("2.查找单词");
			System.out.println("0.退出");
			System.out.println("请输入操作符:");
			Scanner p;
			p = new Scanner(System.in);
			int pos = Integer.valueOf(p.nextLine());
			switch (pos) {
			case 1:
				Scanner word1;
				Scanner meaning;
				System.out.println("请输入单词：");
				word1 = new Scanner(System.in);
				String words1 = word1.nextLine();
				System.out.println("请输入解释：");
				meaning = new Scanner(System.in);
				String meanings = meaning.nextLine();
				add(words1, meanings);
				break;
			case 2:
				Scanner word2;
				System.out.println("请输入单词：");
				word2 = new Scanner(System.in);
				String words2 = word2.nextLine();
				if (find(words2) == null)
					System.out.println("查找不到");
				else
					System.out.println(find(words2));
				break;
			case 0:
				return;
			default:
				Runtime.getRuntime().exec("cls");
				System.out.println("不好意思，无法识别操作符!");
				break;
			}
		}
	}
}
