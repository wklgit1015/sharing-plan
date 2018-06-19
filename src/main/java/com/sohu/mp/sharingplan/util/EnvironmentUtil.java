package com.sohu.mp.sharingplan.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

@Component
public class EnvironmentUtil {

    @Resource
    private Environment environment;

    public boolean isProductionEnv() {
        String[] profiles = environment.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(env -> env.equalsIgnoreCase("prod"));
    }

    public boolean isTestEnv() {
        String[] profiles = environment.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(env -> env.equalsIgnoreCase("test"));
    }

    public boolean isDevEnv() {
        String[] profiles = environment.getActiveProfiles();
        return Arrays.stream(profiles).anyMatch(env -> env.equalsIgnoreCase("dev"));
    }
}
