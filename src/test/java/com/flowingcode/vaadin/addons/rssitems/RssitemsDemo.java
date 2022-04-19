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

import com.flowingcode.vaadin.addons.demo.DemoSource;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.PageTitle;

@PageTitle("Rss Items Demo")
@DemoSource
@SuppressWarnings("serial")
public class RssitemsDemo extends FlexLayout {

	public RssitemsDemo() {
		setSizeFull();
		add(new RssItems("https://www.flowingcode.com/en/feed/", 6, 100, 100, true, "encoded"));
	}
}
