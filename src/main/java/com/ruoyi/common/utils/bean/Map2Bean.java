package com.ruoyi.common.utils.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * About:Map2Bean 工具类。 只要bean中存在属性自动赋值
 * Other:
 * Created: xuefeng jiang on 2016/6/30 16:45.
 * Editored:
 */
@Slf4j
public class Map2Bean {


    // Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
    public static Object transMap2Bean(Map<String, Object> map, Object obj) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    if (value == null) {
                        continue;
                    } else if (value.equals("")) {
                        continue;
                    }
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    if (property.getPropertyType().equals(String.class)) {
                        setter.invoke(obj, String.valueOf(value));
                    } else if (property.getPropertyType().equals(Date.class)) {
                        setter.invoke(obj, Date.valueOf(value.toString()));
                    } else if (property.getPropertyType().equals(int.class) || property.getPropertyType().equals(Integer.class)) {
                        setter.invoke(obj, Integer.parseInt(value.toString()));
                    } else if (property.getPropertyType().equals(double.class) || property.getPropertyType().equals(Double.class)) {
                        setter.invoke(obj, Double.parseDouble(value.toString()));
                    } else if (property.getPropertyType().equals(boolean.class) || property.getPropertyType().equals(Boolean.class)) {
                        setter.invoke(obj, Boolean.valueOf(value.toString()));
                    } else {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            log.error("由Map转为Bean异常", e);
        }
        return obj;
    }

    /**
     * 将 bean 的字段值转化为 map
     *
     * @param obj
     * @param map
     * @return
     */
    public static Object transBean2Map(Object obj, Map<String, Object> map) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                //属性名称
                String propertyName = pd.getName();
                //属性值,用getter方法获取
                Method method = pd.getReadMethod();
                //用对象执行getter方法获得属性值
                Object properValue = method.invoke(obj);
                //把属性名-属性值 存到Map中
                map.put(propertyName, properValue);
            }
        } catch (Exception e) {
            log.error("由Bean转为Map异常", e);
        }
        return obj;
    }

    // Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
    public static Map<String, Object> transBean2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Map) {
            return (Map) obj;
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    Object value = getter.invoke(obj);

                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("由Map转为Bean异常", e);
        }
        return map;
    }

    // Bean --> JSONObject 1: 利用Introspector和PropertyDescriptor 将Bean --> JSONObject
    public static JSONObject transBean2JSONObject(Object obj) {
        if (obj == null) {
            return null;
        }
        //对象属性值为空时也返回
        String json = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
        return JSONObject.parseObject(json);
    }

}
