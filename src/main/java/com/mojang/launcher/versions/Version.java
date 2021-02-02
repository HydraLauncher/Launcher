package com.mojang.launcher.versions;

import java.util.*;

public interface Version
{
    String getId();
    
    ReleaseType getType();
    
    Date getUpdatedTime();
    
    Date getReleaseTime();
}
