package com.evilcorp.firebaseintegration.ui.base;

/**
 * Created by hristo.stoyanov on 3/29/2017.
 */

public interface BaseContract {
    interface View {

        void onError(String error);

    }

    interface Presenter {

        void onCreate();

        void onDestroy();

    }

    interface Interactor {

    }
}
