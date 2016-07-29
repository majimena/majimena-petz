package org.majimena.petical.web.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by todoken on 2016/07/05.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue implements Serializable {
    private String key;
    private Object value;
}
