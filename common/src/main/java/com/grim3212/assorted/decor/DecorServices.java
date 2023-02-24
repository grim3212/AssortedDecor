package com.grim3212.assorted.decor;

import com.grim3212.assorted.decor.services.IDecorHelper;
import com.grim3212.assorted.lib.platform.Services;

public class DecorServices {
    public static final IDecorHelper DECOR = Services.load(IDecorHelper.class);
}
