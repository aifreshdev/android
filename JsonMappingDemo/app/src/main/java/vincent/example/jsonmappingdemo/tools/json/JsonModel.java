package vincent.example.jsonmappingdemo.tools.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import vincent.example.jsonmappingdemo.model.Contact;
import vincent.example.jsonmappingdemo.model.ObjectModel;

/**
 * Created by vincent on 11/1/2018.
 */

public class JsonModel {

    private static final String TAG = "JsonModel";

    /**
     *
     * @param <T> Name.class
     * @param <V> Class.newInstance(V) / enclosing class instance
     * @return
     */

    public static <T, V> T parseObject(JSONObject jsonObj, Class<T> clazz, V v){
        try {
            if(jsonObj == null || clazz == null){
                return null;
            }

            T model = null;
            if(v == null) {
                model = clazz.newInstance();
            }else{
                try {
                    Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                    constructor.setAccessible(true);
                    /**
                     * @link : https://stackoverflow.com/questions/17485297/how-to-instantiate-inner-class-with-reflection-in-java
                     * inner class constructor
                     * Class<?> clazz = Class.forName("com.example.Mother");
                     * Object enclosingInstance = clazz.newInstance();
                     *
                     * Class<?> innerClass = Class.forName("com.example.Mother$Child");
                     * Constructor<?> ctor = innerClass.getDeclaredConstructor(enclosingClass);
                     * Object innerInstance = ctor.newInstance(enclosingInstance);
                     */
                    model = (T) constructor.newInstance(v);
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                    Log.i(TAG, "Constructor must be has a argument.");
                }
            }

            Field[] fields = clazz.getDeclaredFields();
            for(Field f : fields){
                f.setAccessible(true);
                Class<?> type = f.getType();
                String name = f.getName();

                if(!jsonObj.has(name))
                    continue; // not found skip

                String typeName = type.getName();
                Log.i(TAG, "field : " + f.getName() + ", type : " + typeName);
                if(typeName.equals("java.lang.String")) {
                    String value = jsonObj.getString(name);
                    if (value != null && value.equals("null")) {
                        value = "";
                    }

                    f.set(model, value);
                }else if(typeName.equals("int") || typeName.equals("java.lang.Integer")){
                    int value = jsonObj.getInt(name);
                    f.set(model, value);
                }else if(typeName.equals("java.util.List") || typeName.equals("java.util.ArrayList")){
                    Object obj = jsonObj.get(name);
                    if (obj instanceof JSONArray) {
                        JSONArray jsonArr = (JSONArray) obj;
                        Class<?> subClazz = getClassByType(f.getGenericType());
                        if(subClazz != null){

                            ArrayList<?> arrayList = parseArray(jsonArr, subClazz, getInnerSuperClass(subClazz));
                            f.set(model, arrayList);
                        }
//                        if (genericType instanceof ParameterizedType) {
//                            ParameterizedType pType = (ParameterizedType) genericType;
//                            Class<?> realClazz = (Class<?>)pType.getActualTypeArguments()[0];
//                            String clazzName = realClazz.getName();
//                            Log.i(TAG, "Parameterized Type (Class Name): " + clazzName);
//                            Class<?> subClazz = Class.forName(clazzName);
//                            //InstantiationException : has no zero argument constructor
//                            ArrayList<?> arrayList = parseArray(jsonArr, subClazz, model);
//                            f.set(model, arrayList);
//                        }
                    }
                }else{ // Object.class
                    Object subObj = jsonObj.get(name);
                    Class<?> subClazz = Class.forName(typeName);
                    if(subObj instanceof JSONObject){
                        //model => InstantiationException : has no zero argument constructor
                        Object newObj = parseObject((JSONObject)subObj, subClazz, getInnerSuperClass(subClazz));
                        f.set(model, newObj);
                    }
                }

            }

            return model;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T, V> ArrayList<T> parseArray(JSONArray jsonArr, Class<T> clazz, V args){
        if(jsonArr == null){
            return new ArrayList<>();
        }

        int size = jsonArr.length();
        ArrayList<T> list = new ArrayList<>(size);

        try {
            for (int i = 0; i < size; i++) {
                Object obj = jsonArr.get(i);
                if(obj instanceof JSONObject){
                    T t = parseObject((JSONObject)obj, clazz, args);
                    list.add(t);
                }else{
                    list.add((T) obj);
                }
            }

            return list;
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    public static <T, V> T mapObject(JSONObject jsonObj, Class<T> clazz, V v){
        try {
            if (jsonObj == null || clazz == null) {
                return null;
            }

            T model = null;
            Iterator<?> keys = jsonObj.keys();
            if (v == null) {
                model = clazz.newInstance();
            } else {
                try {
                    Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                    constructor.setAccessible(true);
                    model = (T) constructor.newInstance(v); // v = superClass.newInstance() or args
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Log.i(TAG, "Constructor must be has a argument.");
                }
            }

            while (keys.hasNext()) {
                String name = (String) keys.next();
                Object value = jsonObj.get(name);
                name = mapName(name);

                try {
                    Field f = clazz.getField(name);
                    f.setAccessible(true);
                    Class<?> fieldType = f.getType();
                    String fieldName = fieldType.getName();

                    if(fieldType.isAssignableFrom(ObjectModel.class)) {
                        final ObjectModel o = new ObjectModel();
                        o.key = name;
                        o.value = value;
                        value = o;
                        f.set(model, value);
                    }

                    if (value instanceof JSONObject) {
                        Class<?> subClazz = Class.forName(fieldName);
                        if (subClazz != null) {
                            Object newObj = mapObject((JSONObject) value, subClazz, getInnerSuperClass(subClazz));
                            f.set(model, newObj);
                        }
                    } else if (value instanceof JSONArray) {
                        Class<?> subClazz = getClassByType(f.getGenericType());
                        if (subClazz != null) {
                            Class superClazz = null;
                            ArrayList<?> list = mapArray((JSONArray) value, subClazz, getInnerSuperClass(subClazz));
                            f.set(model, list);
                        }
                    } else if (value instanceof String) {
                        if (value == null || value.equals("null")) {
                            value = "";
                        }

                        f.set(model, value);
                    } else if (value instanceof Integer) {
                        Log.d(TAG, "Integer: " + value);
                        f.set(model, value);
                    }
                }catch (NoSuchFieldException e){
                    continue;
                }
            }

            return model;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static <T, V> ArrayList<T> mapArray(JSONArray jsonArr, Class<T> clazz, V v){
        if(jsonArr == null){
            return new ArrayList<>();
        }

        int size = jsonArr.length();
        ArrayList<T> list = new ArrayList<>(size);

        try {
            for (int i = 0; i < size; i++) {
                Object obj = jsonArr.get(i);
                if(obj instanceof JSONObject){
                    T t = mapObject((JSONObject)obj, clazz, v);
                    list.add(t);
                }else{
                    list.add((T) obj);
                }
            }

            return list;
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }

    private static String mapName(String name){
        String[] keys = name.replaceAll("\\W+", " ").split(" ");
        name = keys[0].toLowerCase();
        if(keys.length > 0) {
            for (int i = 1; i < keys.length; i++) {
                String k = keys[i].trim().toLowerCase();
                name += k.substring(0, 1).toUpperCase() + k.substring(1);
            }
        }else{
            name = name.trim();
        }

        return name;
    }

    private static Class<?> getClassByType(Type genericType) {
        Log.i(TAG, "generic type: " + genericType);
        if (genericType instanceof Class) {
            return (Class<?>) genericType;
        } else if (genericType instanceof ParameterizedType) {
            try {
                ParameterizedType pType = (ParameterizedType) genericType;
                Class<?> realClazz = (Class<?>) pType.getActualTypeArguments()[0];
                return Class.forName(realClazz.getName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else if (genericType instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) genericType).getGenericComponentType();
            Class<?> componentClass = getClassByType(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        }

        return null;
    }

    private static boolean isInnerClass(Class<?> clazz) {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    private static <T> T getInnerSuperClass(Class<?> clazz){
        if(isInnerClass(clazz)){
            int lastIndex = clazz.getName().indexOf("$");
            if(lastIndex > -1) {
                String superClass = clazz.getName().substring(0, lastIndex);
                try {
                    return (T) Class.forName(superClass).newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
