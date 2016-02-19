/**   
 * Copyright 2011 The Buzz Media, LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thebuzzmedia.sjxp;

import org.junit.Test;

import com.thebuzzmedia.sjxp.rule.DefaultRule;

import static junit.framework.Assert.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StopTest extends AbstractTest {
	public static final String[] CATEGORIES = new String[] { "Action", "Comedy" };
	public static final int[] MOVIES = new int[] { 1234, 5678 };
	public static final String[] NAMES = new String[] { "Terminator 2",
			"Tommy Boy" };
	public static final int[] YEARS = new int[] { 1991, 1995 };
	public static final String[] CAST_REAL = new String[] {
			"Arnold Schwarzenegger", "Linda Hamilton", "Edward Furlong",
			"Chris Farley", "David Spade", "Brian Dennehy" };
	public static final String[] CAST_FAKE = new String[] { "The Terminator",
			"Sarah Connor", "John Connor", "Tommy", "Richard", "Big Tom" };

	private int count = 0;
	private int charCount = 0;

	private boolean[] OK_CATEGORIES = new boolean[CATEGORIES.length];
	private boolean[] OK_MOVIES = new boolean[MOVIES.length];
	private boolean[] OK_NAMES = new boolean[NAMES.length];
	private boolean[] OK_YEARS = new boolean[YEARS.length];
	private boolean[] OK_CAST_REAL = new boolean[CAST_REAL.length];
	private boolean[] OK_CAST_FAKE = new boolean[CAST_FAKE.length];

	@Test
	public void test() {
		XMLParser parser = new XMLParser(new CategoryRule(), new MovieRule(),
				new NameRule(), new YearRule(), new ActorRule());
		parser.parse(this.getClass()
				.getResourceAsStream("resources/simple.xml"));

		// Invert the isAllOK, we absolutely should have falses in here because
		// this should not complete.
		assertTrue(!isAllOK(OK_CATEGORIES, OK_MOVIES, OK_NAMES, OK_YEARS,
				OK_CAST_REAL, OK_CAST_FAKE));
	}

	class CategoryRule extends DefaultRule {
		public CategoryRule() {
			super(Type.ATTRIBUTE, "/imdb/category", "name");
		}

		@Override
		public void handleParsedAttribute(XMLParser parser, int index,
				String value, Object userObject) {
			// As soon as we parse the first category, stop the parser.
			if (count >= 1)
				parser.stop();

			assertEquals(CATEGORIES[count], value);
			OK_CATEGORIES[count] = true;
		}
	}

	class MovieRule extends DefaultRule {
		public MovieRule() {
			super(Type.ATTRIBUTE, "/imdb/category/movie", "id");
		}

		@Override
		public void handleParsedAttribute(XMLParser parser, int index,
				String value, Object userObject) {
			// We should have stopped in the previous Rule, we should never get
			// here.
			if (count >= 1)
				assertTrue(false);

			assertEquals(MOVIES[count], Integer.parseInt(value));
			OK_MOVIES[count] = true;
		}
	}

	class NameRule extends DefaultRule {
		public NameRule() {
			super(Type.CHARACTER, "/imdb/category/movie/name");
		}

		@Override
		public void handleParsedCharacters(XMLParser parser, String text,
				Object userObject) {
			assertEquals(NAMES[count], text);
			OK_NAMES[count] = true;
		}
	}

	class YearRule extends DefaultRule {
		public YearRule() {
			super(Type.CHARACTER, "/imdb/category/movie/year");
		}

		@Override
		public void handleParsedCharacters(XMLParser parser, String text,
				Object userObject) {
			assertEquals(YEARS[count], Integer.parseInt(text));
			OK_YEARS[count++] = true;
		}
	}

	class ActorRule extends DefaultRule {
		public ActorRule() {
			super(Type.ATTRIBUTE, "/imdb/category/movie/cast/actor",
					"realName", "charName");
		}

		@Override
		public void handleParsedAttribute(XMLParser parser, int index,
				String value, Object userObject) {
			switch (index) {
			case 0:
				assertEquals(CAST_REAL[charCount], value);
				OK_CAST_REAL[charCount] = true;
				break;

			case 1:
				assertEquals(CAST_FAKE[charCount], value);
				OK_CAST_FAKE[charCount++] = true;
				break;

			default:
				assertTrue(false); // shouldn't happen
				break;
			}
		}
	}
}