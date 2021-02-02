package net.minecraft.launcher.ui.tabs.website;

import javafx.embed.swing.*;
import javafx.scene.web.*;
import javafx.scene.*;
import javafx.concurrent.*;
import javafx.beans.value.*;
import org.apache.commons.lang3.*;
import java.net.*;
import com.mojang.launcher.*;
import org.w3c.dom.Node;
import org.w3c.dom.events.*;
import org.w3c.dom.*;
import javafx.application.*;
import java.awt.*;
import org.apache.logging.log4j.*;
import org.w3c.dom.events.Event;

public class JFXBrowser implements Browser
{
    private static final Logger LOGGER;
    private final Object lock;
    private final JFXPanel fxPanel;
    private String urlToBrowseTo;
    private Dimension size;
    private WebView browser;
    private WebEngine webEngine;
    
    public JFXBrowser() {
        this.lock = new Object();
        this.fxPanel = new JFXPanel();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final Group root = new Group();
                final Scene scene = new Scene(root);
                JFXBrowser.this.fxPanel.setScene(scene);
                synchronized (JFXBrowser.this.lock) {
                    JFXBrowser.this.browser = new WebView();
                    JFXBrowser.this.browser.setContextMenuEnabled(false);
                    if (JFXBrowser.this.size != null) {
                        JFXBrowser.this.resize(JFXBrowser.this.size);
                    }
                    JFXBrowser.this.webEngine = JFXBrowser.this.browser.getEngine();
                    JFXBrowser.this.webEngine.setJavaScriptEnabled(false);
                    JFXBrowser.this.webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(final ObservableValue<? extends Worker.State> observableValue, final Worker.State oldState, final Worker.State newState) {
                            if (newState == Worker.State.SUCCEEDED) {
                                final EventListener listener = new EventListener() {
                                    @Override
                                    public void handleEvent(final Event event) {
                                        if (event.getTarget() instanceof Element) {
                                            Element element;
                                            String href;
                                            for (element = (Element)event.getTarget(), href = element.getAttribute("href"); StringUtils.isEmpty(href) && element.getParentNode() instanceof Element; element = (Element)element.getParentNode(), href = element.getAttribute("href")) {}
                                            if (href != null && href.length() > 0) {
                                                try {
                                                    OperatingSystem.openLink(new URI(href));
                                                }
                                                catch (Exception e) {
                                                    JFXBrowser.LOGGER.error("Unexpected exception opening link " + href, e);
                                                }
                                                event.preventDefault();
                                                event.stopPropagation();
                                            }
                                        }
                                    }
                                };
                                final Document doc = JFXBrowser.this.webEngine.getDocument();
                                if (doc != null) {
                                    final NodeList elements = doc.getElementsByTagName("a");
                                    for (int i = 0; i < elements.getLength(); ++i) {
                                        final Node item = elements.item(i);
                                        if (item instanceof EventTarget) {
                                            ((EventTarget)item).addEventListener("click", listener, false);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    if (JFXBrowser.this.urlToBrowseTo != null) {
                        JFXBrowser.this.loadUrl(JFXBrowser.this.urlToBrowseTo);
                    }
                }
                root.getChildren().add(JFXBrowser.this.browser);
            }
        });
    }
    
    @Override
    public void loadUrl(final String url) {
        synchronized (this.lock) {
            this.urlToBrowseTo = url;
            if (this.webEngine != null) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        JFXBrowser.this.webEngine.load(url);
                    }
                });
            }
        }
    }
    
    @Override
    public Component getComponent() {
        return this.fxPanel;
    }
    
    @Override
    public void resize(final Dimension size) {
        synchronized (this.lock) {
            this.size = size;
            if (this.browser != null) {
                this.browser.setMinSize(size.getWidth(), size.getHeight());
                this.browser.setMaxSize(size.getWidth(), size.getHeight());
                this.browser.setPrefSize(size.getWidth(), size.getHeight());
            }
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
