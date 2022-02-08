package fr.epita.assistant.jws.presentation.utils;

import java.lang.reflect.Field;

public class RequestManager {
    public static boolean hasParams(Object obj) {
        for (Field f : obj.getClass().getDeclaredFields()){
            try {
                if (f.get(obj) == null){
                    return false;
                }
            } catch (IllegalAccessException e) {
                continue;
            }
        }
        return true;
    }
}
