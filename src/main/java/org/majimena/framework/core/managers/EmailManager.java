package org.majimena.framework.core.managers;

/**
 * Created by todoken on 2015/06/22.
 */
public interface EmailManager {

    void send(String to, String from, String subject, String content);

}
