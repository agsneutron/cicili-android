package com.cicili.mx.cicili;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> categoria1 = new ArrayList<String>();
        categoria1.add("Pregunta 1");
        categoria1.add("Pregunta 2");
        categoria1.add("Pregunta 3");
        categoria1.add("Pregunta 4");
        categoria1.add("Pregunta 5");

        List<String> categoria2 = new ArrayList<String>();
        categoria2.add("Pregunta 1");
        categoria2.add("Pregunta 2");
        categoria2.add("Pregunta 3");
        categoria2.add("Pregunta 4");
        categoria2.add("Pregunta 5");

        List<String> categoria3 = new ArrayList<String>();
        categoria3.add("Pregunta 1");
        categoria3.add("Pregunta 2");
        categoria3.add("Pregunta 3");
        categoria3.add("Pregunta 4");
        categoria3.add("Pregunta 5");

        expandableListDetail.put("CATEGORIA 1", categoria1);
        expandableListDetail.put("CATEGORIA 2", categoria2);
        expandableListDetail.put("CATEGORIA 3", categoria3);
        return expandableListDetail;
    }
}
