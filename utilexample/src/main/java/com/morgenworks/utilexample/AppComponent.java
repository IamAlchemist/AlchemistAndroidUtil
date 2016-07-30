package com.morgenworks.utilexample;

import dagger.Component;

/**
 * This is Created by wizard on 7/30/16.
 */
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
