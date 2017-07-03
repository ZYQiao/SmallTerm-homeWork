package a2;

import java.io.*;
import java.util.*;

public class Primenum {
	public static void main(String[] args) {

		int n = 0, k = 0;
		for (int i = 2; i <= 1000; i++) {
			if (Prime(i)) {
				n++;
				// 判断是不是拼接素数
				if (CombinePrime(i)) {
					k++;
					System.out.println(i + "\t");
				}
			}
		}
		System.out.println("Done！");
		System.out.println("总共有" + n + "个素数。");
		System.out.println("总共有" + k + "个拼接素数。");

	}

	// 判断是否是素数
	static boolean Prime(int number) {
		if (number == 1 || number == 0)
			return false;
		for (int i = 2; i <= Math.sqrt(number); i++) {
			if (number % i == 0)
				return false;
		}
		return true;
	}

	// 判断是不是拼接素数
	static boolean CombinePrime(int number) {
		int j = 1, m = 0;
		int div = 10;
		j = number / div;
		while (j != 0) {
			m = number - j * div;
			boolean p1 = Prime(j);
			boolean p2 = Prime(m);
			div = div * 10;
			j = number / div;

			if (p1 && p2) {
				return true;
			}

		}
		return false;
	}
}