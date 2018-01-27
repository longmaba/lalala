package com.dongbat.game.util;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 *
 * @author tao
 */
public class ReflectionUtil {

  public static void setProperty(Object object, String fieldName, Object value) {
    Class<? extends Object> clazz = object.getClass();
    try {
      Field field = getField(clazz, fieldName);
      if (field != null) {
        field.setAccessible(true);
        field.set(object, value);
      }
    } catch (Exception ex) {

    }
  }

  private static Field getField(Class<? extends Object> clazz, String fieldName) {
    Class<?> c = clazz;
    while (c.getSuperclass() != null) {
      try {
        return ClassReflection.getDeclaredField(c, fieldName);
      } catch (ReflectionException ex) {
        c = c.getSuperclass();
      }
    }
    return null;
  }
}
