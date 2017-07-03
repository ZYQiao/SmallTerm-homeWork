package a2;

import java.util.Vector;

public class PrimeTest {

	public static int Prime_Test() {
		Vector<String> prime = new Vector<String>();
		int ans = 0;
		for (int i = 0; i < 10000; i++)
			if (TestPrim(i))
				prime.add(String.valueOf(i));
		for (String num : prime) {
			if (Integer.valueOf(num) >= 10) {
				for (int i = 1; i < num.length(); i++) {
					String a1, a2 = new String();
					a1 = num.substring(0, i);
					a2 = num.substring(i);
					if (a2.charAt(0) == '0')
						continue;
					if (TestPrim(Integer.valueOf(a1)) && TestPrim(Integer.valueOf(a2))) {
						System.out.println(num);
						ans++;
						break;
					}
				}
			}
		}
		return ans;
	}

	public static boolean TestPrim(int n) {
		if (n == 1)
			return false;
		if (n == 2)
			return true;
		if (n % 2 == 0)
			return false;
		for (int i = 3; i <= (int) Math.sqrt((double) n); i += 2)
			if (n % i == 0)
				return false;
		return true;
	}

	public static void main(String argv[]) {
		System.out.println("The answer : " + Prime_Test());
	}
}
