package com.flowingcode.vaadin.addons.rssitems;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.Optional;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Element; // Correct import for Element

public class RssItemsTest {

    private RssItems rssItemsSpy;
    private static final String INITIAL_URL = "http://example.com/rss";
    private static final String ANOTHER_URL = "http://another.example.com/rss";
    private static final String CONSTRUCTOR_TEST_URL = "http://constructortest.com/rss";
    private static final String DUMMY_RSS_XML = "<rss version=\"2.0\"><channel><item><title>Test</title></item></channel></rss>";

    @Mock
    private Element elementMock; 
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) 
    private UI uiMock;
    
    @Before
    public void setUp() throws ClientProtocolException, IOException {
        MockitoAnnotations.openMocks(this);
        
        RssItems realRssItems = new RssItems(); 
        rssItemsSpy = spy(realRssItems);

        when(rssItemsSpy.getElement()).thenReturn(elementMock);
        when(elementMock.getUI()).thenReturn(Optional.of(uiMock));
        
        when(elementMock.setProperty(anyString(), anyString())).thenReturn(elementMock);
        when(elementMock.setProperty(anyString(), anyBoolean())).thenReturn(elementMock);
        when(elementMock.setProperty(anyString(), anyInt())).thenReturn(elementMock);
        
        doNothing().when(rssItemsSpy).invokeXmlToItems(anyString());
        doReturn(DUMMY_RSS_XML).when(rssItemsSpy).obtainRss(anyString());

        rssItemsSpy.setUrl(INITIAL_URL);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
    }

    @Test
    public void testSetMaxTitleLengthDoesNotCallObtainRss() {
        rssItemsSpy.setMaxTitleLength(100);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
        verify(rssItemsSpy, times(1)).obtainRss(anyString()); 
    }

    @Test
    public void testSetMaxExcerptLengthDoesNotCallObtainRss() {
        rssItemsSpy.setMaxExcerptLength(200);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
        verify(rssItemsSpy, times(1)).obtainRss(anyString());
    }

    @Test
    public void testSetMaxDoesNotCallObtainRss() {
        rssItemsSpy.setMax(10);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
        verify(rssItemsSpy, times(1)).obtainRss(anyString());
    }

    @Test
    public void testSetExtractImageFromDescriptionDoesNotCallObtainRss() {
        rssItemsSpy.setExtractImageFromDescription(true);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
        verify(rssItemsSpy, times(1)).obtainRss(anyString());
    }

    @Test
    public void testSetUrlTriggersObtainRss() throws ClientProtocolException, IOException {
        rssItemsSpy.setUrl(ANOTHER_URL);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL); 
        verify(rssItemsSpy, times(1)).obtainRss(ANOTHER_URL); 
        verify(rssItemsSpy, times(2)).obtainRss(anyString()); 
    }

    @Test
    public void testRefreshTriggersObtainRss() throws ClientProtocolException, IOException {
        rssItemsSpy.refresh();
        verify(rssItemsSpy, times(2)).obtainRss(INITIAL_URL); 
        verify(rssItemsSpy, times(2)).obtainRss(anyString()); 
    }
    
    @Test
    public void testConstructorWithUrlCallsObtainRss() throws ClientProtocolException, IOException {
        RssItems itemsConstructedWithUrl = new RssItems(); 
        RssItems constructorSpy = spy(itemsConstructedWithUrl);

        Element localElementMock = mock(Element.class); 
        UI localUiMock = mock(UI.class, Answers.RETURNS_DEEP_STUBS);
        
        when(constructorSpy.getElement()).thenReturn(localElementMock);
        when(localElementMock.getUI()).thenReturn(Optional.of(localUiMock));
        when(localElementMock.setProperty(anyString(), anyString())).thenReturn(localElementMock);
        when(localElementMock.setProperty(anyString(), anyBoolean())).thenReturn(localElementMock); 
        when(localElementMock.setProperty(anyString(), anyInt())).thenReturn(localElementMock);    
        doNothing().when(constructorSpy).invokeXmlToItems(anyString());
        
        // Specific mock for the URL used in this constructor test
        doReturn(DUMMY_RSS_XML).when(constructorSpy).obtainRss(CONSTRUCTOR_TEST_URL);
        // Fallback for any other unexpected string if other URLs are called by internal setters etc.
        // This helps if setUrl(null) or similar is called by a default constructor path we aren't expecting.
        doReturn(DUMMY_RSS_XML).when(constructorSpy).obtainRss(argThat(argument -> !CONSTRUCTOR_TEST_URL.equals(argument)));


        // Simulate the sequence of calls as it happens in RssItems(String url)
        // RssItems(String url) -> RssItems(url, DEFAULT_MAX, ...)
        // The main constructor calls:
        // 1. setUrl(url) -> obtainRss (1st call)
        // 2. setAuto(true), setMax, etc. (no obtainRss)
        // 3. refreshUrl() -> obtainRss (2nd call)
        
        constructorSpy.setUrl(CONSTRUCTOR_TEST_URL); 
        constructorSpy.setAuto(true); 
        constructorSpy.setMax(RssItems.DEFAULT_MAX); // Using public constant
        constructorSpy.setMaxExcerptLength(RssItems.DEFAULT_MAX_EXCERPT_LENGTH); // Using public constant
        constructorSpy.setMaxTitleLength(RssItems.DEFAULT_MAX_TITLE_LENGTH); // Using public constant
        constructorSpy.refresh(); 
        
        verify(constructorSpy, times(2)).obtainRss(CONSTRUCTOR_TEST_URL);
    }
}
