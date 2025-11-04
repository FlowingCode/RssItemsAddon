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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.internal.StateTree.ExecutionRegistration;

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
	
	private ExecutionRegistration pendingRefreshRegistration;
	
	public static final String ERROR_RSS = "<rss>\r\n" + 
			"  <channel>\r\n" + 
			"    <item>\r\n" + 
			"      <title>Error Retrieving RSS</title>\r\n" + 
			"      <link>https://</link>\r\n" + 
			"      <description>There was an error retrieving the rss: %s</description>\r\n" + 
			"      <thumbnail url=\"https://\"></thumbnail>\r\n" + 
			"    </item>";
	
	public static final String IMAGE_METHOD = "    $0._getItemImageScr = function (item) {\r\n" + 
			"        var element = document.createElement('div');\r\n" +
			"        element.innerHTML = item.%%ATTRIBUTE_NAME%%;\r\n" + 
			"        var image = element.querySelector('img') || {};\r\n" + 
			"        return image.src || '';\r\n" + 
			"    }\r\n" + 
			"";
	

	public static final int DEFAULT_MAX = Integer.MAX_VALUE;

	public static final int DEFAULT_MAX_TITLE_LENGTH = 50;

	public static final int DEFAULT_MAX_EXCERPT_LENGTH = 100;
	
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
	}
	
	/**
	 * @param url rss feed url
	 */
	public RssItems(String url) {
		this(url,DEFAULT_MAX, DEFAULT_MAX_TITLE_LENGTH, DEFAULT_MAX_EXCERPT_LENGTH, false);
	}

	/**
	 * Constructor for testing purposes.
	 */
	protected RssItems() {
	}

	private void scheduleRefresh() {
	    UI ui = UI.getCurrent();
	    if (ui == null) {
	        // If no UI is available (e.g., background thread or testing without proper UI setup),
	        // consider an immediate refresh or log a warning.
	        // For now, let's do an immediate refresh as a fallback,
	        // though this might not be ideal in all detached scenarios.
	        // This matches the original behavior more closely if UI isn't available.
	        refreshUrl();
	        return;
	    }

	    // If there's a pending registration, remove it.
	    if (pendingRefreshRegistration != null) {
	        try {
	            pendingRefreshRegistration.remove();
	        } catch (Exception e) {
	            // Log or handle potential exceptions if .remove() fails, though typically it shouldn't.
	            // For example, if the registration is already inactive.
	            System.err.println("Error removing pending refresh registration: " + e.getMessage());
	        }
	        pendingRefreshRegistration = null;
	    }

	    // Schedule refreshUrl to be called before the client response.
	    // The lambda uiParam -> refreshUrl() is used because the consumer takes the UI as a parameter.
	    pendingRefreshRegistration = ui.beforeClientResponse(this, uiParam -> refreshUrl());
	}
	
	private void refreshUrl() {
		if (pendingRefreshRegistration != null) {
		    // If refreshUrl is called directly while a refresh was scheduled,
		    // the scheduled one is now effectively preempted or redundant.
		    // We nullify the registration to reflect this.
		    // Note: We don't call .remove() here as this method IS the execution (or a direct call).
		    pendingRefreshRegistration = null;
		}
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

	protected String obtainRss(String url) throws ClientProtocolException, IOException {
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
      scheduleRefresh();
    }
	
	/**
	 * Sets the max title excerpt length
	 * @param length
	 */
    public void setMaxExcerptLength(int length) {
      this.getElement().setProperty("maxExcerptLength", length);
      scheduleRefresh();
    }
	
	/**
	 * Sets the maximun number of items to be shown
	 * @param max
	 */
    public void setMax(int max) {
      this.getElement().setProperty("max", max);
      scheduleRefresh();
    }
	
    public void setExtractImageFromDescription(boolean extractImageFromDescription) {
      this.extractImageFromDescription = extractImageFromDescription;
      scheduleRefresh();
    }

    /**
     * Sets the url of the RSS
     * @param url
     */
    public void setUrl(String url) {
      this.getElement().setProperty("url", url);
      this.url = url;
      scheduleRefresh();
    }
    
    /**
     * Refreshes the RSS feed.
     */
    public void refresh() {
    	if (pendingRefreshRegistration != null) {
            try {
                pendingRefreshRegistration.remove();
            } catch (Exception e) {
                // Log or handle potential exceptions.
                System.err.println("Error removing pending refresh registration during manual refresh: " + e.getMessage());
            }
            pendingRefreshRegistration = null;
        }
        refreshUrl();
    }

}
