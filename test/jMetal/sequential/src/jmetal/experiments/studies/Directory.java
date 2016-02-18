package jmetal.experiments.studies;

import java.util.GregorianCalendar;
import java.util.Calendar;

public class Directory {
	private static final String top = System.getProperty("top.dir") + "/src/jmetal";
	private static final String outdir = System.getProperty("out.dir");// + "/" + getSuffix();
	public static String topDir() {
		return top;
	}
	public static String outDir() {
		return outdir;
	}
	public static String getSuffix() {
		String suffix = null;
		GregorianCalendar date = new GregorianCalendar();

		suffix = Integer.toString(date.get(Calendar.DAY_OF_MONTH));
		suffix += Integer.toString(date.get(Calendar.MONTH));
		suffix += Integer.toString(date.get(Calendar.YEAR));
		suffix += Integer.toString(date.get(Calendar.SECOND));
		suffix += Integer.toString(date.get(Calendar.MINUTE));
		suffix += Integer.toString(date.get(Calendar.HOUR));

		return suffix;
	}
}
