import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

import com.thebuzzmedia.sjxp.XMLParser;
import com.thebuzzmedia.sjxp.rule.DefaultRule;
import com.thebuzzmedia.sjxp.rule.IRule;
import com.thebuzzmedia.sjxp.rule.IRule.Type;
import com.thebuzzmedia.sjxp.XMLFeed;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Benchmark {
	public static IRule getRule(Type type, String locationPath, String... attributeNames) {
		switch(type) {
		case CHARACTER: {
			return new DefaultRule(type, locationPath) {
				@Override
				public void handleParsedCharacters(XMLParser parser,
						String text, Object userObject) {
					//_parsing_count++;	//TODO: Do something productive
				}
			};
		}
		case ATTRIBUTE: {
			return new DefaultRule(type, locationPath,
					attributeNames) {
				@Override
				public void handleParsedAttribute(XMLParser parser, int index,
						String value, Object userObject) {
					//_parsing_count++;	//TODO: Do something productive
				}
			};
		}
		case TAG: {
			//TODO
			break;
		}
		}
		return null;
	}

	public static void parse() throws IOException {
		// Hacker News
		IRule[] rule_0 = new IRule[2];
		rule_0[0] = getRule(Type.CHARACTER, "/rss/channel/item/title");
		rule_0[1] = getRule(Type.CHARACTER, "/rss/channel/item/link");
		String file_0 = System.getProperty("user.dir") + "/rss-news.ycombinator.com.xml";
		XMLFeed feed0 = new XMLFeed(2, 15000, file_0);
		feed0.addRule(rule_0);
		feed0.launchParser();

		// Bugzilla
		IRule[] rule_1 = new IRule[2];
		rule_1[0] = getRule(Type.ATTRIBUTE, "/bugzilla/bug/long_desc/who", "name");
		rule_1[1] = getRule(Type.CHARACTER, "/bugzilla/bug/long_desc/thetext");
		String file_1 = System.getProperty("user.dir") + "/bugzilla-bug-feed.xml"; 
		XMLFeed feed1 = new XMLFeed(2, 135000, file_1);
		feed1.addRule(rule_1);
		feed1.launchParser();

		// Craiglist
		IRule[] rule_2 = new IRule[2];
		rule_2[0] = getRule(Type.ATTRIBUTE, 
				"/[http://www.w3.org/1999/02/22-rdf-syntax-ns#]RDF/[http://purl.org/rss/1.0/]item", 
				"[http://www.w3.org/1999/02/22-rdf-syntax-ns#]about");
		rule_2[1] = getRule(Type.CHARACTER, "/[http://www.w3.org/1999/02/22-rdf-syntax-ns#]RDF/[http://purl.org/rss/1.0/]item/[http://purl.org/rss/1.0/]description");
		String file_2 = System.getProperty("user.dir") + "/rdf-newyork.craigslist.org.xml";
		XMLFeed feed2 = new XMLFeed(2, 300000, file_2);
		feed2.addRule(rule_2);
		feed2.launchParser();

		// TechCrunch
		IRule[] rule_3 = new IRule[2];
		rule_3[0] = getRule(Type.CHARACTER, "/rss/channel/item/title");
		rule_3[1] = getRule(Type.CHARACTER, "/rss/channel/item/link");
		String file_3 = System.getProperty("user.dir") + "/rss-techcrunch.com.xml";
		XMLFeed feed3 = new XMLFeed(2, 305000, file_3);
		feed3.addRule(rule_3);
		feed3.launchParser();

		// Samsung
		IRule[] rule_4 = new IRule[3];
		rule_4[0] = getRule(Type.CHARACTER, "/rss/channel/item/title");
		rule_4[1] = getRule(Type.CHARACTER, "/rss/channel/item/link");
		rule_4[2] = getRule(Type.CHARACTER, "/rss/channel/item/description");
		String file_4 = System.getProperty("user.dir") + "/rss-news.samsung.com.xml";
		XMLFeed feed4 = new XMLFeed(3, 750000, file_4);
		feed4.addRule(rule_4);
		feed4.launchParser();

		// Eclipse XML Stress
		IRule[] rule_5 = new IRule[1];
		rule_5[0] = getRule(Type.CHARACTER, "/motorcarrierfreightdetails/motorcarrierfreightdetail/additionallineitems/additionallineitem/quantityandweight");
		String file_5 = System.getProperty("user.dir") + "/eclipse-xml-stress-test.xml";
		XMLFeed feed5 = new XMLFeed(1, 1650000, file_5);
		feed5.addRule(rule_5);
		feed5.launchParser();

		// Dictionary
		IRule[] rule_6 = new IRule[1];
		rule_6[0] = getRule(Type.CHARACTER, "/dictionary/e/ss/s/qp/q/w");
		String file_6 = System.getProperty("user.dir") + "/dictionary.xml";
		XMLFeed feed6 = new XMLFeed(1, 10650000, file_6);
		feed6.addRule(rule_6);
		feed6.launchParser();
	}

	public final static int  total_copies_of_7_feeds = 8;

	public static void main(String[] args) throws IOException {
		final long startIn = System.nanoTime();
		for(int k=0; k<total_copies_of_7_feeds; k++) {
			parse();
		}
		final double durationIn = (((double)(System.nanoTime() - startIn))/((double)(1.0E9))) * 1000;
		System.out.printf("Time = %d msec\n",(int)durationIn);
	}
}
