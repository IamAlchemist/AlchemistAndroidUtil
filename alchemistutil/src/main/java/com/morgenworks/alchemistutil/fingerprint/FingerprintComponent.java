package com.morgenworks.alchemistutil.fingerprint;

import javax.inject.Singleton;

import dagger.Component;

/**
 * This is Created by wizard on 7/29/16.
 */
@Singleton
@Component(modules = {FingerprintModule.class})
public interface FingerprintComponent {
    void inject(FingerprintHelper helper);
}
