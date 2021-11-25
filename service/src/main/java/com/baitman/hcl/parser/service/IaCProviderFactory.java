package com.baitman.hcl.parser.service;

import com.baitman.hcl.parser.model.enums.SupportedIaCProviders;

import java.util.HashMap;
import java.util.Map;

public class IaCProviderFactory {
    public static volatile IaCProviderFactory iaCProviderFactory;

    private static volatile Map<String, IaCProvider> register;

    private IaCProviderFactory() {

    }

    public void registerIaCProvider(String registerKey, IaCProvider iaCProvider) {
        if(!register.containsKey(registerKey)) {
            register.put(registerKey, iaCProvider);
        }
    }

    public static IaCProviderFactory getIaCProviderFactory() {
        if(iaCProviderFactory == null && register == null) {
            synchronized (IaCProviderFactory.class) {
                if(iaCProviderFactory == null && register == null) {
                    iaCProviderFactory = new IaCProviderFactory();
                    register = new HashMap<>();
                }
            }
        }
        return iaCProviderFactory;
    }

    public IaCProvider getIaCProvider(SupportedIaCProviders supportedIaCProviders) {
        if(supportedIaCProviders != null) {
            switch(supportedIaCProviders) {
                case TERRAFORM -> {return new Terraform();}
            }
        }
        String errMsg = new StringBuilder("Provider with key ").append(supportedIaCProviders.name())
                .append(" is not registered").toString();
        throw new IllegalArgumentException(errMsg);
    }
}
