package com.flowingcode.vaadin.addons.rssitems;

import com.github.appreciated.card.Card;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.Route;

/**
 * @author Martin Lopez / Flowing Code
 */
@Route("")
@SuppressWarnings("serial")
public class DemoView extends FlexLayout {
	
	public DemoView() {
		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		Card card = new Card(new RssItems("https://www.flowingcode.com/feeds/posts/default?alt=rss",6,100,100,true));
		card.setWidth("90vw");
		card.getContent().setPadding(true);
		card.getContent().getStyle().set("height", "90vh");
		card.getContent().getStyle().set("overflow-y", "scroll");
		add(card);
		setAlignSelf(Alignment.CENTER, card);
	}
	
	
}
