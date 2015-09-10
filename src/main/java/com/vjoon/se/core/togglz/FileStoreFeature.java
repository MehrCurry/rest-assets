package com.vjoon.se.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.context.FeatureContext;

/**
 * Created by guido on 08.09.15.
 */
public enum FileStoreFeature implements Feature {
    @EnabledByDefault
    LOCAL_MIRROR,
    S3_MIRROR;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
