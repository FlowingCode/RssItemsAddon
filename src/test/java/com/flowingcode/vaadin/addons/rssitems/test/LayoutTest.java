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

package com.flowingcode.vaadin.addons.rssitems.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.flowingcode.vaadin.addons.DemoLayout;
import com.flowingcode.vaadin.addons.rssitems.RssitemsDemoView;
import com.vaadin.flow.router.Route;

public class LayoutTest {

	@Test
	public void testDemoLayout() {
		Route route = RssitemsDemoView.class.getAnnotation(Route.class);
		assertEquals("com.flowingcode.vaadin.addons.DemoLayout",DemoLayout.class.getName());
		assertEquals(DemoLayout.class, route.layout());
		assertNotEquals("", route.value());
	}
}
