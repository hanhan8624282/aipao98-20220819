package com.ecc.aipao98;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Aipao98ApplicationTests {

    @Test
    void contextLoads() {

        System.out.println(((Math.random()*9+1)*1000));
    }

}
