package io.unbong.ubcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * plugins entrypoint
 *
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-15 19:46
 */
@Component
public class UBApplicationListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    List<UBplugin> plugins;

    /**
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ApplicationReadyEvent are){
            // almost same with ApplicationRunner
            for (UBplugin plugin : plugins) {
                plugin.init();
                plugin.startup();
            }
        } else if (event instanceof ContextClosedEvent cce) {
            // almost same with predestroy
            for (UBplugin plugin : plugins) {
                plugin.shutdown();
            }
        }
    }
}
