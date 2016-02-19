package com.thebuzzmedia.sjxp.parallel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.thebuzzmedia.sjxp.XMLParser;
import com.thebuzzmedia.sjxp.rule.DefaultRule;
import com.thebuzzmedia.sjxp.rule.IRule;
import com.thebuzzmedia.sjxp.rule.IRule.Type;

/*
 * Author: Vivek Kumar
 */

public class ParallelXMLParser {
	@Atomicset(P);
	@Atomic(P) private volatile LinkedList<XMLFeed> xmlFeedList;
	@Atomic(P) private volatile boolean exit_processing;

	public ParallelXMLParser() {
		xmlFeedList = new LinkedList<XMLFeed>();
		exit_processing = false;
	}

	public void add_new_XMLFeed(int totalRules, int estFileSize, String filename, IRule[] rule) {
		XMLFeed feed = new XMLFeed(totalRules, estFileSize, filename);
		feed.addRule(rule);
		//synchronized(this) {
			xmlFeedList.add(feed);
		//}
	}

	public void terminate() {
		//synchronized(this) {
			exit_processing = true;
		//}
	}

	private XMLFeed getNewFeed() {
		//synchronized(this) {
			try {
				XMLFeed feed = xmlFeedList.removeLast();
				return feed;
			} catch(NoSuchElementException e) {
			}
		//}
		return null;
	}

	private boolean shouldTerminate() {
		//synchronized(this) {
		if(exit_processing && xmlFeedList.size() == 0) return true;
		else return false;
		//}
	}

	private void doParse(XMLFeed feed) {
		if(feed != null) {
			try {
				feed.launchParser();
			} catch(IOException ioe) {
				System.out.println("ERROR: File not found");
				System.exit(-1);
			}	
		}
	}
	
	public void start() {
		//synchronized(this) {
		exit_processing = false;
		//}
		finish {
			async {
				for(int i=0; i<org.jikesrvm.scheduler.WS.wsProcs; i++) {
					while(true) {
						doParse(getNewFeed());
						if(shouldTerminate()) break;
					}
				}
			}
		}
	}
}
