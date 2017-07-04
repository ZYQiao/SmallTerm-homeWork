package a2;

import java.util.Vector;

public class PrimeTest {
	public static boolean isSpPrime(String n) {
		if (Integer.valueOf(n) < 10)
			return false;
		String a, b, c, d = new String();
		for (int i = 1; i < n.length(); ++i) {
			a = n.substring(0, i);
			b = n.substring(i);
			if (isPrime(Integer.valueOf(a)) && isPrime(Integer.valueOf(b))) {
				if (b.charAt(0) == '0')
					continue;
				return true;
			}
			if (i == 1) {
				for (int j = 1; j < b.length(); j++) {
					c = b.substring(0, j);
					d = b.substring(j);
					if (isPrime(Integer.valueOf(a)) && isPrime(Integer.valueOf(c)) && isPrime(Integer.valueOf(d))) {
						if (c.charAt(0) == '0' || d.charAt(0) == '0')
							continue;
						return true;
					}
				}
			}
			if (i == 2) {
				for (int j = 1; j < b.length(); j++) {
					c = b.substring(0, j);
					d = b.substring(j);
					if (isPrime(Integer.valueOf(a)) && isPrime(Integer.valueOf(c)) && isPrime(Integer.valueOf(d))) {
						return true;
					}
				}
			}
			if (i == 3) {
				c = a.substring(1, 2);
				d = a.substring(2, 3);
				a = a.substring(0, 1);
				if (isPrime(Integer.valueOf(a)) && isPrime(Integer.valueOf(b)) && isPrime(Integer.valueOf(c))
						&& isPrime(Integer.valueOf(d))) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isPrime(int n) {
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
		Vector<String> prime = new Vector<String>();
		int ans = 0;
		for (int i = 0; i < 10000; i++)
			if (isPrime(i))
				prime.add(String.valueOf(i));
		for (String num : prime) {
			if (isSpPrime(num)) {
				System.out.println(num);
				ans++;
			}
		}
		System.out.println("The answer : " + ans);
	}
}
