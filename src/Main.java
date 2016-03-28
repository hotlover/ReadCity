import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

class City {
	int number;
	String name;
	String pinyin;
	int parent;
	int level;

	public City(int number, String name, String pinyin, int parent, int level) {
		super();
		this.number = number;
		this.name = name;
		this.parent = parent;
		this.level = level;
		this.pinyin = pinyin;
	}
}

public class Main {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("./asset/provice.txt"));
		String line;
		ArrayList<City> list = new ArrayList<City>();
		ArrayList<String> numlist = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			String number = line.substring(0, 6);
			int num = Integer.parseInt(number);
			String str = line.substring(6).replace((char) 12288, ' ').trim().replaceAll("\\s*", "");
			String pinyin = PinYinUtil.formatHanziToPinyin(str);
			if (num % 10000 == 0) {
				list.add(new City(num, str, pinyin, 0, 0));
				// System.out.println("省市"+num+"&"+str);
			} else if (num % 100 == 0) {
				// 市
				int provicenum = num / 10000 * 10000;
				int index = numlist.indexOf(provicenum + "") + 1;
				City city = new City(num, str, pinyin, index, 1);
				list.add(city);
			} else {
				// 区
				int citynum = num / 100 * 100;
				int index = numlist.indexOf(citynum + "") + 1;
				list.add(new City(num, str, pinyin, index, 2));
			}
			numlist.add(num + "");
		}
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
