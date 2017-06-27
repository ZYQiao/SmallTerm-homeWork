// 13/17小数点后第100位的数字是几？
public class FractionalDigit {

	public static void main(String[] args) {
		int d = 13;
		int q = 17;
		int a = 0;
		for(int i = 0; i < 100; i++){
			a = d*10/q;
			d = (d*10)%q;
		}
		
		double b = 13.0/17.0;
		System.out.println(b);
		System.out.println(a);

	}

}
