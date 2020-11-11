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
package com.flowingcode.vaadin.addons.rssitems;

import com.flowingcode.vaadin.addons.DemoLayout;
import com.flowingcode.vaadin.addons.GithubLink;
import com.flowingcode.vaadin.addons.demo.impl.TabbedDemoImpl;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * @author Martin Lopez / Flowing Code
 */
@Route(value = "rss-items", layout = DemoLayout.class)
@SuppressWarnings("serial")
@GithubLink("https://github.com/FlowingCode/RssItemsAddon")
public class RssitemsDemoView extends VerticalLayout {

	private static final String RSS_DEMO = "Rss Items Demo";
	private static final String RSS_SOURCE = "https://github.com/FlowingCode/RssItemsAddon/blob/master/src/test/java/com/flowingcode/vaadin/addons/rssitems/RssitemsDemo.java";

	public RssitemsDemoView() {
		TabbedDemoImpl<RssitemsDemo> rssDemo = new TabbedDemoImpl<>(new RssitemsDemo(), RSS_DEMO,
				RSS_SOURCE);
		setSizeFull();
		add(rssDemo);
	}

}