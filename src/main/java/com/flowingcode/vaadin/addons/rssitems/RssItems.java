/*-
 * #%L
 * RSS Items
 * %%
 * Copyright (C) 2019 - 2022 Flowing Code
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

package com.flowingcode.vaadin.addons.rssitems;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

/**
 * Simple RSS Reader component based on https://github.com/TherapyChat/rss-items
 * 
 * @author Martin Lopez / Flowing Code
 */
@Tag("rss-items")
@NpmPackage(value="@polymer/iron-ajax",version="3.0.1")
@NpmPackage(value="@polymer/iron-image",version="3.0.2")
@NpmPackage(value="x2js",version="3.4.0")
@JsModule("./rss-items.js")
@SuppressWarnings("serial")
public class RssItems extends Component implements HasSize, HasStyle {
	
	private String url;
	
	private boolean extractImageFromDescription;
	
	private static final String ERROR_RSS = "<rss>\r\n" + 
			"  <channel>\r\n" + 
			"    <item>\r\n" + 
			"      <title>Error Retrieving RSS</title>\r\n" + 
			"      <link>https://</link>\r\n" + 
			"      <description>There was an error retrieving the rss: %s</description>\r\n" + 
			"      <thumbnail url=\"https://\"></thumbnail>\r\n" + 
			"    </item>";
	
	private static final String IMAGE_METHOD = "    $0._getItemImageScr = function (item) {\r\n" + 
			"        var element = document.createElement('div');\r\n" +
			"        element.innerHTML = item.%%ATTRIBUTE_NAME%%;\r\n" + 
			"        var image = element.querySelector('img') || {};\r\n" + 
			"        return image.src || '';\r\n" + 
			"    }\r\n" + 
			"";
	

	private static final int DEFAULT_MAX = Integer.MAX_VALUE;

	private static final int DEFAULT_MAX_TITLE_LENGTH = 50;

	private static final int DEFAULT_MAX_EXCERPT_LENGTH = 100;
	
	/**
	 * @param url rss feed url
	 * @param max max number of items to show
	 */
	public RssItems(String url, int max, int maxTitleLength, int maxExcerptLength, boolean extractImageFromDescription) {
		this(url, max, maxTitleLength, maxExcerptLength, extractImageFromDescription, "description");
	}
	/**
	 * @param url rss feed url
	 * @param max max number of items to show
	 */
	public RssItems(String url, int max, int maxTitleLength, int maxExcerptLength, boolean extractImageFromDescription, String attributeName) {
		this.extractImageFromDescription = extractImageFromDescription;
		if (this.extractImageFromDescription) {
			this.getElement().executeJs(IMAGE_METHOD.replaceAll("%%ATTRIBUTE_NAME%%", attributeName), this);
		}
		
		this.setUrl(url);
		this.setAuto(true);
		this.setMax(max);
		this.setMaxExcerptLength(maxExcerptLength);
		this.setMaxTitleLength(maxTitleLength);
		addClassName("x-scope");
		addClassName("rss-items-0");		
		refreshUrl();
	}
	
	/**
	 * @param url rss feed url
	 */
	public RssItems(String url) {
		this(url,DEFAULT_MAX, DEFAULT_MAX_TITLE_LENGTH, DEFAULT_MAX_EXCERPT_LENGTH, false);
	}

	private void refreshUrl() {
		try {
			String rss = obtainRss(url);
			invokeXmlToItems(rss);
		} catch (Exception e) {
			e.printStackTrace();
			invokeXmlToItems(String.format(ERROR_RSS, e.toString()));
		}
	}

	private void invokeXmlToItems(String rss) {
		this.getElement().executeJs("this.xmlToItems($0)", rss);
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
		request.completed();
		return result;
	}
	
	/**
     * Sets the auto property to init the RSS request
     * @param auto
     */
	public void setAuto(boolean auto) {
	  this.getElement().setProperty("auto", auto);
	}
	
	/**
	 * Sets the max title length
	 * @param length
	 */
    public void setMaxTitleLength(int length) {
      this.getElement().setProperty("maxTitleLength", length);
      refreshUrl();
    }
	
	/**
	 * Sets the max title excerpt length
	 * @param length
	 */
    public void setMaxExcerptLength(int length) {
      this.getElement().setProperty("maxExcerptLength", length);
      refreshUrl();
    }
	
	/**
	 * Sets the maximun number of items to be shown
	 * @param max
	 */
    public void setMax(int max) {
      this.getElement().setProperty("max", max);
      refreshUrl();
    }
	
    public void setExtractImageFromDescription(boolean extractImageFromDescription) {
      this.extractImageFromDescription = extractImageFromDescription;
      refreshUrl();
    }

    /**
     * Sets the url of the RSS
     * @param url
     */
    public void setUrl(String url) {
      this.getElement().setProperty("url", url);
      this.url = url;
    }

}
