package org.club.qy.extension;

/**
 * @Author hht
 * @Date 2021/5/19 16:14
 */
public class Holder<T> {
    private volatile T val;
    public void setValue(T val){
        this.val = val;
    }
    public T getValue(){
        return this.val;
    }
}
