package com.cicili.mx.cicili.directionhelpers;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Vishal on 10/20/2018.
 */

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);

    void onTaskDoneData(String... result);


}
