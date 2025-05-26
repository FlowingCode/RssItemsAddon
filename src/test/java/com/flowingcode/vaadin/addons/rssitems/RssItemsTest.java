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
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.Optional;
import org.apache.http.client.ClientProtocolException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.internal.StateTree;

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

    @Captor
    private ArgumentCaptor<SerializableConsumer<UI>> consumerCaptor;

    @Mock
    private StateTree.ExecutionRegistration mockExecutionRegistration;
    
    private MockedStatic<UI> staticUiMock;

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

        // Stub beforeClientResponse for the main uiMock.
        // This will be used by the rssItemsSpy instance managed by most tests.
        // Note: if a test needs to verify .remove() on a specific registration,
        // it might need to set up its own uiMock.beforeClientResponse interaction.
        when(uiMock.beforeClientResponse(any(RssItems.class), consumerCaptor.capture()))
            .thenReturn(mockExecutionRegistration);
    }
    
    private void setupStaticUiMock() {
        if (staticUiMock == null || staticUiMock.isClosed()) { 
            staticUiMock = Mockito.mockStatic(UI.class);
        }
        staticUiMock.when(UI::getCurrent).thenReturn(uiMock);
    }

    @After
    public void tearDown() {
        if (staticUiMock != null && !staticUiMock.isClosed()) {
            staticUiMock.close();
        }
    }
    
    private void runLastScheduledRefresh(ArgumentCaptor<SerializableConsumer<UI>> captor) {
        if (!captor.getAllValues().isEmpty()) {
             captor.getValue().accept(uiMock); 
        }
    }

    private void prepareForTestWithInitialUrl(String url) throws ClientProtocolException, IOException {
        setupStaticUiMock();
        // Set an initial URL for the spy to ensure obtainRss is called with a predictable URL if refresh happens
        rssItemsSpy.setUrl(url); // This will call scheduleRefresh, consumerCaptor will get it.
        runLastScheduledRefresh(consumerCaptor); // Run this initial refresh.
        
        Mockito.clearInvocations(rssItemsSpy, elementMock, uiMock, mockExecutionRegistration); 
        
        // Re-stub beforeClientResponse for the actual test part, ensuring the captor is fresh for the setter
        when(uiMock.beforeClientResponse(any(RssItems.class), consumerCaptor.capture())).thenReturn(mockExecutionRegistration);
        // Re-stub obtainRss for the specific URL expected in the test
        doReturn(DUMMY_RSS_XML).when(rssItemsSpy).obtainRss(url);
    }

    @Test
    public void testSetMaxTitleLengthSchedulesRefreshButDoesNotRunImmediately() throws ClientProtocolException, IOException {
        prepareForTestWithInitialUrl(INITIAL_URL);
        rssItemsSpy.setMaxTitleLength(100);
        verify(rssItemsSpy, never()).obtainRss(anyString());
        runLastScheduledRefresh(consumerCaptor);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
    }

    @Test
    public void testSetMaxExcerptLengthSchedulesRefreshButDoesNotRunImmediately() throws ClientProtocolException, IOException {
        prepareForTestWithInitialUrl(INITIAL_URL);
        rssItemsSpy.setMaxExcerptLength(200);
        verify(rssItemsSpy, never()).obtainRss(anyString());
        runLastScheduledRefresh(consumerCaptor);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
    }

    @Test
    public void testSetMaxSchedulesRefreshButDoesNotRunImmediately() throws ClientProtocolException, IOException {
        prepareForTestWithInitialUrl(INITIAL_URL);
        rssItemsSpy.setMax(10);
        verify(rssItemsSpy, never()).obtainRss(anyString());
        runLastScheduledRefresh(consumerCaptor);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
    }

    @Test
    public void testSetExtractImageFromDescriptionSchedulesRefreshButDoesNotRunImmediately() throws ClientProtocolException, IOException {
        prepareForTestWithInitialUrl(INITIAL_URL);
        rssItemsSpy.setExtractImageFromDescription(true);
        verify(rssItemsSpy, never()).obtainRss(anyString());
        runLastScheduledRefresh(consumerCaptor);
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
    }
    
    @Test
    public void testMultipleSettersScheduleOnlyOneRefresh() throws ClientProtocolException, IOException {
        prepareForTestWithInitialUrl(INITIAL_URL); 

        rssItemsSpy.setMax(10); 
        verify(uiMock, times(1)).beforeClientResponse(any(RssItems.class), consumerCaptor.capture());
        
        rssItemsSpy.setMaxTitleLength(100); 
        verify(uiMock, times(2)).beforeClientResponse(any(RssItems.class), consumerCaptor.capture());
        // The first registration (from setMax) should have been removed by the second call (setMaxTitleLength)
        verify(mockExecutionRegistration, times(1)).remove(); 
        
        verify(rssItemsSpy, never()).obtainRss(anyString()); 
        runLastScheduledRefresh(consumerCaptor); 
        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL); 
    }

    @Test
    public void testSetUrlSchedulesRefresh() throws ClientProtocolException, IOException {
        setupStaticUiMock();
        Mockito.clearInvocations(rssItemsSpy, uiMock, mockExecutionRegistration); 
        when(uiMock.beforeClientResponse(any(RssItems.class), consumerCaptor.capture())).thenReturn(mockExecutionRegistration);
        doReturn(DUMMY_RSS_XML).when(rssItemsSpy).obtainRss(ANOTHER_URL);

        rssItemsSpy.setUrl(ANOTHER_URL);
        verify(rssItemsSpy, never()).obtainRss(ANOTHER_URL); 
        runLastScheduledRefresh(consumerCaptor);
        verify(rssItemsSpy, times(1)).obtainRss(ANOTHER_URL); 
    }

    @Test
    public void testPublicRefreshCancelsScheduledAndRefreshesImmediately() throws ClientProtocolException, IOException {
        setupStaticUiMock();
        rssItemsSpy.setUrl(INITIAL_URL); 
        runLastScheduledRefresh(consumerCaptor); // Run refresh from initial setUrl
        Mockito.clearInvocations(rssItemsSpy, uiMock, mockExecutionRegistration); 
        
        ExecutionRegistration localSpecificMockRegistration = mock(StateTree.ExecutionRegistration.class);
        when(uiMock.beforeClientResponse(any(RssItems.class), consumerCaptor.capture()))
            .thenReturn(localSpecificMockRegistration); // Use a specific registration for this test
        doReturn(DUMMY_RSS_XML).when(rssItemsSpy).obtainRss(INITIAL_URL);

        rssItemsSpy.setMax(10); // Schedules a refresh, consumer captured by class-level captor
        verify(uiMock, times(1)).beforeClientResponse(any(RssItems.class), any());
        verify(rssItemsSpy, never()).obtainRss(anyString()); 

        rssItemsSpy.refresh(); // Public, immediate refresh

        verify(rssItemsSpy, times(1)).obtainRss(INITIAL_URL);
        verify(localSpecificMockRegistration, times(1)).remove(); 
        
        Mockito.clearInvocations(rssItemsSpy); 
        runLastScheduledRefresh(consumerCaptor); 
        verify(rssItemsSpy, never()).obtainRss(anyString());
    }
    
    @Test
    public void testConstructorWithUrlSchedulesRefresh() throws ClientProtocolException, IOException {
        try (MockedStatic<UI> staticUiForCtor = Mockito.mockStatic(UI.class)) {
            UI localConstructorUiMock = mock(UI.class, Answers.RETURNS_DEEP_STUBS);
            staticUiForCtor.when(UI::getCurrent).thenReturn(localConstructorUiMock);

            ArgumentCaptor<SerializableConsumer<UI>> ctorConsumerCaptor = ArgumentCaptor.forClass(SerializableConsumer.class);
            StateTree.ExecutionRegistration ctorMockRegistration = mock(StateTree.ExecutionRegistration.class);
            when(localConstructorUiMock.beforeClientResponse(any(RssItems.class), ctorConsumerCaptor.capture()))
                .thenReturn(ctorMockRegistration);

            RssItems items = new RssItems(); 
            RssItems constructorTestSpy = spy(items);

            Element localElementMock = mock(Element.class);
            when(constructorTestSpy.getElement()).thenReturn(localElementMock);
            when(localElementMock.getUI()).thenReturn(Optional.of(localConstructorUiMock)); 
            
            when(localElementMock.setProperty(anyString(), anyString())).thenReturn(localElementMock);
            when(localElementMock.setProperty(anyString(), anyBoolean())).thenReturn(localElementMock);
            when(localElementMock.setProperty(anyString(), anyInt())).thenReturn(localElementMock);
            doNothing().when(constructorTestSpy).invokeXmlToItems(anyString());
            doReturn(DUMMY_RSS_XML).when(constructorTestSpy).obtainRss(CONSTRUCTOR_TEST_URL);

            // Simulate the sequence of calls made by RssItems(CONSTRUCTOR_TEST_URL)
            // This constructor calls this(url, DEFAULT_MAX, ...) which calls setters.
            // Each of these (setUrl, setMax, etc.) calls scheduleRefresh.
            // The *last* call to scheduleRefresh is the one that persists.
            constructorTestSpy.setUrl(CONSTRUCTOR_TEST_URL); 
            constructorTestSpy.setAuto(true); 
            constructorTestSpy.setMax(RssItems.DEFAULT_MAX); 
            constructorTestSpy.setMaxExcerptLength(RssItems.DEFAULT_MAX_EXCERPT_LENGTH); 
            constructorTestSpy.setMaxTitleLength(RssItems.DEFAULT_MAX_TITLE_LENGTH); 

            verify(constructorTestSpy, never()).obtainRss(anyString()); 
            
            if (!ctorConsumerCaptor.getAllValues().isEmpty()) {
                ctorConsumerCaptor.getValue().accept(localConstructorUiMock);
            }
            
            verify(constructorTestSpy, times(1)).obtainRss(CONSTRUCTOR_TEST_URL);
        }
    }
}
