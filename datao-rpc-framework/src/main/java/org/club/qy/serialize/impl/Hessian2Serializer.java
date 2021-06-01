package org.club.qy.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.club.qy.exception.SerializationException;
import org.club.qy.serialize.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Author hht
 * @Date 2021/5/18 21:43
 */
public class Hessian2Serializer implements Serialization {
    @Override
    public byte[] serialize(Object obj) {
        byte[] data = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(outputStream);
            output.writeObject(obj);
            output.getBytesOutputStream().flush();
            output.completeMessage();
            output.close();
            data = outputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Serialization failed");
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null) {
            return null;
        }
        Object result = null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            Hessian2Input input = new Hessian2Input(is);
            result = input.readObject();
        } catch (Exception e) {
            throw new SerializationException("DeSerialization failed");
        }
        return (T) result;
    }
}
