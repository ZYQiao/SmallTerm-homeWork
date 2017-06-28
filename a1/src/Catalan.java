
public class Catalan {

	public static int answers = 0;

	// 请实现go函数
	public static void go(Deque from, Deque to, Stack s) {

		if (from.size() == 0) {
			if (s.empty()) {
				answers++;
				return;
			} else {
				to.addLast(s.pop());
				go(from, to, s);
			}
		} else {
			if (!s.empty()) {
				Deque f = new Deque();
				f = from;
				Deque t = new Deque();
				t = from;
				Stack st = new Stack();
				st = s;
				t.addLast(st.pop());
				go(f, t, st);
				if (f.size() == 0)
					if (s.empty()){
						answers++;
						return;
					}
			}
			s.push(from.getLast());
			from.removeLast();
			go(from, to, s);
		}
//		System.out.println();
	}

	public static void main(String[] args) {
		Deque from = new Deque();
		Deque to = new Deque();
		Stack s = new Stack();

		for (int i = 1; i <= 7; i++) {
			from.addLast(i);
		}

		go(from, to, s);

		System.out.println(answers);

	}

}
