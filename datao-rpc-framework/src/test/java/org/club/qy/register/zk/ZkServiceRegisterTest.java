package org.club.qy.register.zk;

import org.club.qy.register.ServiceDiscover;
import org.club.qy.register.ServiceRegister;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @Author hht
 * @Date 2021/5/19 17:14
 */

class ZkServiceRegisterTest {

    @Test
    public void registerAndDiscover(){
        int i = Integer.bitCount(3);
        System.out.println(i);
       /* ServiceRegister serviceRegister = new ZkServiceRegister();
        InetSocketAddress registerSocketAddress = new InetSocketAddress("127.0.0.1", 9993);
        serviceRegister.registerServices("org.club.qy.register.zk.ZkServiceRegister",registerSocketAddress);
        ServiceDiscover serviceDiscover =  new ZkServiceDiscover();

      *//*  InetSocketAddress inetSocketAddress = serviceDiscover.discoverService("org.club.qy.register.zk.ZkServiceRegister");*//*
        assertEquals(inetSocketAddress.toString(),registerSocketAddress.toString());*/

    }

}