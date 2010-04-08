/**
 * Copyright (c) 2008-2010 Andrey Somov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yaml.snakeyaml.introspector;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.yaml.snakeyaml.error.YAMLException;

public class FieldProperty extends Property {
    private final Field field;

    public FieldProperty(Field field) {
        super(field.getName(), field.getType());
        this.field = field;
    }

    @Override
    public void set(Object object, Object value) throws Exception {
        field.set(object, value);
    }

    @Override
    public Object get(Object object) {
        try {
            return field.get(object);
        } catch (Exception e) {
            throw new YAMLException("Unable to access field " + field.getName() + " on object "
                    + object + " : " + e);
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) field.getGenericType();
            return t.getActualTypeArguments();
        } else {
            return null;
        }
    }
}