package com.flowingcode.vaadin.addons.rssitems;

/*-
 * #%L
 * RSS Items
 * %%
 * Copyright (C) 2019 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;

/**
 * Simple RSS Reader component based on https://github.com/TherapyChat/rss-items
 * 
 * @author Martin Lopez / Flowing Code
 */
@Tag("rss-items")
@HtmlImport("bower_components/rss-items/rss-items.html")
@HtmlImport("frontend://styles/rss-items-styles.html")
@SuppressWarnings("serial")
public class RssItems extends PolymerTemplate<RssItemsModel> implements HasSize, HasStyle {
	
	private String url;
	
	private static final String errorRss = "<rss>\r\n" + 
			"  <channel>\r\n" + 
			"    <item>\r\n" + 
			"      <title>Error Retrieving RSS</title>\r\n" + 
			"      <link>https://</link>\r\n" + 
			"      <description>There was an error retrieving the rss: %s</description>\r\n" + 
			"      <thumbnail url=\"https://\"></thumbnail>\r\n" + 
			"    </item>";
	
	private static final String xmlStr2JS = "    (function _xmlStr2JS() {\r\n" + 
			"      // parse xml to json and get items\r\n" + 
			"      var conversor = new X2JS()\r\n" + 
			"      var json = conversor.xml_str2json($1)\r\n" + 
			"      var items = json.rss ? json.rss.channel.item : json.channel.item\r\n" + 
			"      // truncate with this.max and parse items\r\n" + 
			"      items = $0.max === undefined ? items : items.splice(0, $0.max)\r\n" + 
			"      $0.items = $0._parseItems(items)\r\n" + 
			"    })()";

	private static final int DEFAULT_MAX = Integer.MAX_VALUE;

	private static final int DEFAULT_MAX_TITLE_LENGTH = 50;

	private static final int DEFAULT_MAX_EXCERPT_LENGTH = 100;
	
	/**
	 * @param url rss feed url
	 * @param max max number of items to show
	 */
	public RssItems(String url, int max, int maxTitleLength, int maxExcerptLength) {
		getModel().setAuto(true);
		getModel().setMax(max);
		getModel().setMaxExcerptLength(maxExcerptLength);
		getModel().setMaxTitleLength(maxTitleLength);
		addClassName("x-scope");
		addClassName("rss-items-0");
		this.url = url;
		refreshUrl();
	}
	
	/**
	 * @param url rss feed url
	 */
	public RssItems(String url) {
		this(url,DEFAULT_MAX, DEFAULT_MAX_TITLE_LENGTH, DEFAULT_MAX_EXCERPT_LENGTH);
	}

	private void refreshUrl() {
		try {
			String rss = obtainRss(url);
			invokeXmlToItems(rss);
		} catch (Exception e) {
			e.printStackTrace();
			invokeXmlToItems(String.format(errorRss, e.toString()));
		}
	}

	private void invokeXmlToItems(String rss) {
		UI.getCurrent().getPage().executeJavaScript(xmlStr2JS, this.getElement(), rss);
	}

	private String obtainRss(String url) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(URI.create(url));
		request.addHeader("Content-Type", "application/xml");
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode()>200) {
			throw new RuntimeException("Problem reading the rss url: " + response.getStatusLine().getReasonPhrase());
		}
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		return result;
	}
	
	/**
	 * Sets the max title length
	 * @param length
	 */
	public void setMaxTitleLength(int length) {
		getModel().setMaxTitleLength(length);
		refreshUrl();
	}
	
	/**
	 * Sets the max title excerpt length
	 * @param length
	 */
	public void setMaxExcerptLength(int length) {
		getModel().setMaxExcerptLength(length);
		refreshUrl();
	}
	
	/**
	 * Sets the maximun number of items to be shown
	 * @param max
	 */
	public void setMax(int max) {
		getModel().setMax(max);
		refreshUrl();
	}


}
