import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

class City2 {
	int id;
	int number;
	String name;
	String pinyin;
	int parent;
	int level;

	public City2(int id, int number, String name, String pinyin, int parent, int level) {
		super();
		this.id = id;
		this.number = number;
		this.name = name;
		this.parent = parent;
		this.level = level;
		this.pinyin = pinyin;
	}
}

public class CustomMain {

	public static void main(String[] args) throws Exception {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Connection connectionSrc = DriverManager.getConnection("jdbc:sqlite:" + "./asset/region_src.db");
		Statement srcStmt = connectionSrc.createStatement();
		String[] specialCity = new String[] { "北京市", "天津市", "上海市", "重庆市" };
		List<String> specialList = Arrays.asList(specialCity);
		BufferedReader br = new BufferedReader(new FileReader("./asset/provice.txt"));
		String line;
		ArrayList<City2> list = new ArrayList<City2>();
		HashMap<Integer, Integer> numberToId = new HashMap<Integer, Integer>();
		HashMap<Integer, String> numberToName = new HashMap<Integer, String>();
		HashMap<Integer, Integer> shengxiaxian = new HashMap<Integer, Integer>();
		int count = 1;
		while ((line = br.readLine()) != null) {
			String number = line.substring(0, 6);
			int num = Integer.parseInt(number);
			String pinyin = "";
			String str = line.substring(6).replace((char) 12288, ' ').trim().replaceAll("\\s*", "");
			ResultSet resultSet = srcStmt
					.executeQuery("SELECT REGION_NAME_EN FROM REGION WHERE REGION_NAME = '" + str + "'");
			if (resultSet.next()) {
				pinyin = resultSet.getString(1).replaceAll("\\s*", "");
			} else {
				pinyin = PinYinUtil.formatHanziToPinyin(str);
			}

			if (num % 10000 == 0) {
				if (specialList.contains(str)) {
					list.add(new City2(count, num, str, pinyin, 0, 1));
				} else {
					list.add(new City2(count, num, str, pinyin, 0, 0));
				}
				// System.out.println("省市"+num+"&"+str);
			} else if (num % 100 == 0) {
				// 市/省直辖县级行政区划
				int provicenum = num / 10000 * 10000;
				if (str.equals("省直辖县级行政区划") || str.equals("自治区直辖县级行政区划")) {
					shengxiaxian.put(num, provicenum);
					continue;
				}
				if (specialList.contains(numberToName.get(provicenum))) {
					continue;
				}
				int index = numberToId.get(provicenum);
				City2 city = new City2(count, num, str, pinyin, index, 1);
				list.add(city);
			} else {
				// 区
				int citynum = num / 100 * 100;
				int provicenum = num / 10000 * 10000;
				int index;
				if (str.equals("市辖区")) {
					continue;
				}
				if (specialList.contains(numberToName.get(provicenum))) {
					index = numberToId.get(provicenum);
					list.add(new City2(count, num, str, pinyin, index, 2));
				} else if (shengxiaxian.containsKey(citynum)) {
					index = numberToId.get(provicenum);
					list.add(new City2(count, num, str, pinyin, index, 1));
				} else {
					index = numberToId.get(citynum);
					list.add(new City2(count, num, str, pinyin, index, 2));
				}
			}
			numberToName.put(num, str);
			int temp = count;
			numberToId.put(num, temp);
			count++;
		}
		srcStmt.close();
		connectionSrc.close();
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + "./asset/region.db");
		connection.setAutoCommit(false);// 设置不自动提交，不然一条条插入很慢
		// 开启一个事物
		Statement statement = connection.createStatement();
		statement.execute("delete from region");
		System.out.println(list.size());
		for (int i = 0, len = list.size(); i < len; i++) {
			String sql = "insert into region values(?,?,?,?,?,?)";
			PreparedStatement prepareStatement = connection.prepareStatement(sql);
			prepareStatement.setInt(1, i + 1);
			prepareStatement.setString(2, list.get(i).number + "");
			prepareStatement.setString(3, list.get(i).name);
			prepareStatement.setInt(4, list.get(i).parent);
			prepareStatement.setInt(5, list.get(i).level);
			prepareStatement.setString(6, list.get(i).pinyin);
			prepareStatement.execute();
			System.out.println("insert " + (i + 1));
		}
		// 插入结束，提交
		connection.commit();
		connection.close();
		System.out.println("over");
	}

}
