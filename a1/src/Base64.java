import java.nio.file.StandardCopyOption;
import java.util.Vector;

public class Base64 {

	private static final byte base[] = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F',
			(byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
			(byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',
			(byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
			(byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p',
			(byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y',
			(byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) '+', (byte) '/' };

	private static int getPos(char c) {
		for (int i = 0; i < base.length; i++) {
			if (base[i] == (byte) c) {
				return i;
			}
		}
		return -1;
	}

	private static byte firstByte(byte b) {
		int r = b & 0xff;
		r = r >>> 2;
		return (byte) (r & 0x3f);
	}

	private static byte secondByte(byte last_b, byte next_b) {
		int r1 = last_b & 0xff;
		int r2 = next_b & 0xff;
		r1 = r1 << 6;
		r1 = r1 >>> 2;
		r2 = r2 >>> 4;
		return (byte) ((r1 | r2) & 0x3f);
	}

	private static byte thirdByte(byte last_b, byte next_b) {
		int r1 = last_b & 0xff;
		int r2 = next_b & 0xff;
		r1 = r1 << 4;
		r1 = r1 >>> 2;
		r2 = r2 >>> 6;
		return (byte) ((r1 | r2) & 0x3f);
	}

	private static byte fourthByte(byte b) {
		int r = b & 0xff;
		r = r << 2;
		r = r >>> 2;
		return (byte) (r & 0x3f);
	}

	private static byte lastOneByte(byte b, int move) {
		int r = b & 0xff;
		r = r << move;
		r = r >>> 2;
		return (byte) (r & 0x3f);
	}

	public static String encode(byte[] b) {
		StringBuffer sb = new StringBuffer();
		int len = b.length;
		int more_len = len % 3;
		int use_len = len - more_len;
		byte[] bytes = new byte[4];
		for (int i = 0; i < use_len; i += 3) {
			bytes[0] = base[firstByte(b[i])];
			bytes[1] = base[secondByte(b[i], b[i + 1])];
			bytes[2] = base[thirdByte(b[i + 1], b[i + 2])];
			bytes[3] = base[fourthByte(b[i + 2])];
			sb.append(new String(bytes));
		}
		if (more_len == 1) {
			byte b_2[] = new byte[2];
			b_2[0] = base[firstByte(b[len - 1])];
			b_2[1] = base[lastOneByte(b[len - 1], 6)];
			sb.append(new String(b_2));
			return sb.append("==").toString();
		} else if (more_len == 2) {
			byte b_3[] = new byte[3];
			b_3[0] = base[firstByte(b[len - 2])];
			b_3[1] = base[secondByte(b[len - 2], b[len - 1])];
			b_3[2] = base[lastOneByte(b[len - 1], 4)];
			sb.append(new String(b_3));
			return sb.append("=").toString();
		}
		return sb.toString();
	}

	private static byte backLastOne(byte last_b, byte next_b, int move_l, int move_b) {
		int r1 = last_b & 0xff;
		int r2 = next_b & 0xff;
		r1 = r1 << move_l;
		r2 = r2 << move_l;
		r2 = r2 >>> move_b;
		return (byte) ((r1 | r2) & 0xff);
	}

	private static byte backFirst(byte b, byte c) {
		int r_f = b & 0xff;
		int r_s = c & 0xff;
		r_f = r_f << 2;
		r_s = r_s >>> 4;
		return (byte) ((r_f | r_s) & 0xff);
	}

	private static byte backSecond(byte b, byte c) {
		int r_s = b & 0xff;
		int r_t = c & 0xff;
		r_s = r_s << 4;
		r_t = r_t >>> 2;
		return (byte) ((r_s | r_t) & 0xff);
	}

	private static byte backThird(byte b, byte c) {
		int r_t = b & 0xff;
		int r_f = c & 0xff;
		r_t = r_t << 6;
		return (byte) ((r_t | r_f) & 0xff);
	}

	public static byte[] decode(String s) {
		int lenth = 0;
		Vector<Byte> list = new Vector<Byte>();
		for (int j = 0; j < s.length(); j++) {
			if (s.charAt(j) != '=') {
				lenth++;
			}
		}
		int more_len = lenth % 4;
		for (int i = 0; i <= lenth - 3; i += 4) {
			list.add(backFirst((byte) getPos(s.charAt(i)), (byte) getPos(s.charAt(i + 1))));
			list.add(backSecond((byte) getPos(s.charAt(i + 1)), (byte) getPos(s.charAt(i + 2))));
			list.add(backThird((byte) getPos(s.charAt(i + 2)), (byte) getPos(s.charAt(i + 3))));
		}
		if (more_len == 2) {
			byte b_1[] = new byte[1];
			b_1[0] = backLastOne((byte) getPos(s.charAt(lenth - 2)), (byte) getPos(s.charAt(lenth - 1)), 2, 6);
			list.add(b_1[0]);
		}
		if (more_len == 3) {
			byte b_2[] = new byte[2];
			b_2[0] = backFirst((byte) getPos(s.charAt(lenth - 3)), (byte) getPos(s.charAt(lenth - 2)));
			b_2[1] = backSecond((byte) getPos(s.charAt(lenth - 2)), (byte) getPos(s.charAt(lenth - 1)));
			list.add(b_2[0]);
			list.add(b_2[1]);
		}
		int k = 0;
		byte bytes[] = new byte[list.size()];
		for (byte bt : list) {
			bytes[k++] = bt;
		}
		
		return bytes;
	}

	public static void main(String[] args) {
		byte[] a = {1,2,3, -7,-9,110};
		String s = encode(a);
		System.out.println(s);
		byte[] b = decode(s);
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i] + " ");
		}
		System.out.println();
	}

}
