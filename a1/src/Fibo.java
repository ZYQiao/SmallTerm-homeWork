public class Fibo {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		Fibo f = new Fibo();
		for(int i = 0; i < 10000; i++){
		System.out.println(f.fibo1(9)); // 这两种方法哪种效率更高？循环效率高
//		System.out.println(f.fibo2(9));
		}
		long end = System.currentTimeMillis();   
        System.out.println(end - start + "ms"); 
	}
	
	public int fibo1(int n) { // 使用方法（函数）递归来实现
		if(n == 1)
			return 1;
		if(n == 0)
			return 0;
		return fibo1(n - 1) + fibo1(n - 2);
	}

	public int fibo2(int n) { // 使用循环来实现
		int a1 = 0, a2 = 1, tmp = 0;
		if(n < 2)
			return a2;
		for(int i = 1; i < n; i++){
			tmp = a1;
			a1 = a2;
			a2 = a1 + tmp;
		}
		return a2;
	}
}

	

