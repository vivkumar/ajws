package com.thebuzzmedia.sjxp;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import com.thebuzzmedia.sjxp.XMLParser;
import com.thebuzzmedia.sjxp.rule.DefaultRule;
import com.thebuzzmedia.sjxp.rule.IRule;
import com.thebuzzmedia.sjxp.rule.IRule.Type;

/*
 * Author: Vivek Kumar
 */

@SuppressWarnings({ "rawtypes", "unchecked" })
public class XMLFeed {

	private IRule[] _rule;
	private int _count_rule;
	private int _max_rule;
	private String _locationPath;
	private Type _type;	
	private int _estFileSize;
	private  String _filename;

	public XMLFeed(int totalRules, int estFileSize, String filename) {
		this._rule = new IRule[totalRules];
		this._count_rule = 0;
		this._max_rule = totalRules;
		this._estFileSize = estFileSize;
		this._filename = filename;
	}

	public XMLFeed(int totalRules, int estFileSize, String filename, IRule[] rule) {
		this(totalRules, estFileSize, filename);
	}

	public void addRule(IRule[] rule) {
		for(int i=0; i<_max_rule; i++) {
			this._rule[_count_rule++] = rule[i];
		}
	}

	public void launchParser() throws IOException {
		InputStream in = loadFile(_estFileSize, _filename);
		XMLParser parser = new XMLParser(_rule);
		int size = in.available();
		long startTime = System.currentTimeMillis();

		parser.parse(in);
		System.out.println("Processed " + size + " bytes, parsed " + parser.get_total_parsing_count()
				+ " XML elements in "
				+ (System.currentTimeMillis() - startTime) + "ms");
	}

	/**
	 * Used to load the file completely off-disk and into memory to avoid
	 * introducing unpredictable (and unequal) latency into the parse timing.
	 */

	private InputStream loadFile(int estFileSize, String filename)
			throws IOException {
		int bytesRead = 0;
		int totalBytesRead = 0;
		byte[] buffer = new byte[8192];
		byte[] result = new byte[estFileSize];

		FileInputStream fin=new FileInputStream(filename);
		//BufferedInputStream in = new BufferedInputStream(XMLFeed.class.getResourceAsStream(filename));
		BufferedInputStream in = new BufferedInputStream(fin);
		
		while ((bytesRead = in.read(buffer)) > 0) {
			System.arraycopy(buffer, 0, result, totalBytesRead, bytesRead);
			totalBytesRead += bytesRead;
		}

		return new ByteArrayInputStream(result, 0, totalBytesRead);
	}
}
