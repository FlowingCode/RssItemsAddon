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

import com.vaadin.flow.templatemodel.TemplateModel;

/**
 * Template Model for RssItems component 
 * 
 * @author Martin Lopez / Flowing Code
 */
public interface RssItemsModel extends TemplateModel {

	public void setAuto(Boolean auto);
	public void setUrl(String url);
	public void setMaxTitleLength(int length);
	public void setMaxExcerptLength(int length);
	public void setMax(int max);
	
}
