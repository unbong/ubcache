package io.unbong.ubcache;

/**
 * ub cache plugin
 *
 * @author <a href="ecunbong@gmail.com">unbong</a>
 * 2024-06-15 19:44
 */
public interface UBplugin {

    /**
     * life cycle function
     */
    void init();
    void startup();
    void shutdown();

}
