import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.thebuzzmedia.sjxp.XMLParser;
import com.thebuzzmedia.sjxp.rule.DefaultRule;
import com.thebuzzmedia.sjxp.rule.IRule;
import com.thebuzzmedia.sjxp.rule.IRule.Type;
import com.thebuzzmedia.sjxp.parallel.ParallelXMLParser;

class ParseXML {
	private final int  total_copies_of_7_feeds = 8;
	private final int  SECONDS = 100;
	
	private Thread listner;
	private final ParallelXMLParser pparser;
	
	public ParseXML() {
		this.pparser = new ParallelXMLParser();
		this.listner = new Thread () {
			@Override
			public void run() {
				System.out.println("INFO_Benchmark: Launching Listner Thread");
				for(int k=0; k<total_copies_of_7_feeds; k++) {
					Benchmark.provide_feed(pparser);
				}
				pparser.terminate();
				try {
					Thread.sleep(SECONDS*1000);
				} catch (InterruptedException e) { }
				System.out.println("INFO_Benchmark: Listner going to Terminate");
			}
		};
	}
	private void signal() {
		listner.interrupt();
	}
	public void launch() {
		listner.start();
		pparser.start();
	}
	public void stop() {
		signal();
		try {
			listner.join();
		} catch(InterruptedException e) { }
	}
}

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Benchmark {
	public static void provide_feed(final ParallelXMLParser pparser) {
		// Hacker News
		IRule[] rule_0 = new IRule[2];
		rule_0[0] = getRule(Type.CHARACTER, "/rss/channel/item/title");
		rule_0[1] = getRule(Type.CHARACTER, "/rss/channel/item/link");
		String file_0 = System.getProperty("user.dir") + "/rss-news.ycombinator.com.xml";
		pparser.add_new_XMLFeed(2, 15000, file_0, rule_0);

		// Bugzilla
		IRule[] rule_1 = new IRule[2];
		rule_1[0] = getRule(Type.ATTRIBUTE, "/bugzilla/bug/long_desc/who", "name");
		rule_1[1] = getRule(Type.CHARACTER, "/bugzilla/bug/long_desc/thetext");
		String file_1 = System.getProperty("user.dir") + "/bugzilla-bug-feed.xml"; 
		pparser.add_new_XMLFeed(2, 135000, file_1, rule_1);

		// Craiglist
		IRule[] rule_2 = new IRule[2];
		rule_2[0] = getRule(Type.ATTRIBUTE, 
				"/[http://www.w3.org/1999/02/22-rdf-syntax-ns#]RDF/[http://purl.org/rss/1.0/]item", 
				"[http://www.w3.org/1999/02/22-rdf-syntax-ns#]about");
		rule_2[1] = getRule(Type.CHARACTER, "/[http://www.w3.org/1999/02/22-rdf-syntax-ns#]RDF/[http://purl.org/rss/1.0/]item/[http://purl.org/rss/1.0/]description");
		String file_2 = System.getProperty("user.dir") + "/rdf-newyork.craigslist.org.xml";
		pparser.add_new_XMLFeed(2, 300000, file_2, rule_2);

		// TechCrunch
		IRule[] rule_3 = new IRule[2];
		rule_3[0] = getRule(Type.CHARACTER, "/rss/channel/item/title");
		rule_3[1] = getRule(Type.CHARACTER, "/rss/channel/item/link");
		String file_3 = System.getProperty("user.dir") + "/rss-techcrunch.com.xml";
		pparser.add_new_XMLFeed(2, 305000, file_3, rule_3);

		// Samsung
		IRule[] rule_4 = new IRule[3];
		rule_4[0] = getRule(Type.CHARACTER, "/rss/channel/item/title");
		rule_4[1] = getRule(Type.CHARACTER, "/rss/channel/item/link");
		rule_4[2] = getRule(Type.CHARACTER, "/rss/channel/item/description");
		String file_4 = System.getProperty("user.dir") + "/rss-news.samsung.com.xml";
		pparser.add_new_XMLFeed(3, 750000, file_4, rule_4);


		// Eclipse XML Stress
		IRule[] rule_5 = new IRule[1];
		rule_5[0] = getRule(Type.CHARACTER, "/motorcarrierfreightdetails/motorcarrierfreightdetail/additionallineitems/additionallineitem/quantityandweight");
		String file_5 = System.getProperty("user.dir") + "/eclipse-xml-stress-test.xml";
		pparser.add_new_XMLFeed(1, 1650000, file_5, rule_5);

		// Dictionary
		IRule[] rule_6 = new IRule[1];
		rule_6[0] = getRule(Type.CHARACTER, "/dictionary/e/ss/s/qp/q/w");
		String file_6 = System.getProperty("user.dir") + "/dictionary.xml";
		pparser.add_new_XMLFeed(1, 10650000, file_6, rule_6);	
	}
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
	public static void main(String[] args) throws IOException, InterruptedException {
		ParseXML parse_xml = new ParseXML();
		final long startIn = System.nanoTime();
		parse_xml.launch();
		final double durationIn = (((double)(System.nanoTime() - startIn))/((double)(1.0E9))) * 1000;
		System.out.printf("Time = %d msec\n",(int)durationIn);
		parse_xml.stop();
		org.jikesrvm.scheduler.WS.dumpWSStatistics();
	}
}
