package a2;

import java.io.File;
import java.util.Vector;

public class Repeatation {
	private static Vector<MyFile> fl = new Vector<MyFile>();

	public static boolean isRepate(MyFile file) {
		String fName = new String();
		int pos = file.getName().lastIndexOf('.');
		String fileName = (file.getName().substring(0, pos));
		for (MyFile f : fl) {
			pos = f.getName().lastIndexOf('.');
			fName = (f.getName().substring(0, pos));
			if (fName.equals(fileName) && f.getSize() == file.getSize() && f.getPath() != file.getPath()) {
				return true;
			}
		}
		return false;
	}

	public static void AllFiles(File dir) throws Exception {
		File[] fs = dir.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (!fs[i].isDirectory())
				fl.add(new MyFile(fs[i].getAbsolutePath(), fs[i].getName()));
			if (fs[i].isDirectory()) {
				AllFiles(fs[i]);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		File root = new File(args[0]);
		boolean rep = false;
		AllFiles(root);
		for (MyFile f : fl) {
			if (isRepate(f)) {
				rep = true;
				System.out.println(f.getPath() + " " + f.getName());
			}
		}
		if (!rep)
			System.out.println("没有重复文件");
	}

}
