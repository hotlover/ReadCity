import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
	private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();
	static {
		FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
	}
	public static String formatHanziToPinyin(String string) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			if (Character.isSpaceChar(string.charAt(i))) {
				continue;
			}
			if (string.charAt(i) >= 0 && string.charAt(i) < 127) {
				sb.append(string.charAt(i));
				continue;
			}
			try {
				// PinyinHelper.toHanyuPinyinStringArray 只能处理一个字符，格式参数可指定音调、大小写
				// 、v
				// 返回值是数组，因为有多音字
				String[] pys = PinyinHelper.toHanyuPinyinStringArray(
						string.charAt(i), FORMAT);
				// System.out.println(Arrays.asList(pys));
				if (pys != null && pys.length > 0) {
					sb.append(pys[0].substring(0, 1).toUpperCase()+pys[0].substring(1));
				} else {
					sb.append(" ");
				}
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
