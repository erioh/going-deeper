public class MyApp {
	public static void main(String[] args) throws Exception {
		for (String str : args) {
			System.out.println("Hello " + str);
		}
	}

	public static void getHiddenValue() throws Exception {
		System.out.println(Integer.class.getField("hiddenValue").getName());
	}
}