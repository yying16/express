package com.express.user;

import com.express.common.vo.R;
import com.express.user.feign.MessageClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class UserApplicationTest {

    @Autowired
    MessageClient messageClient;

    RestTemplate restTemplate = new RestTemplate();

    @Test
    public void test(){
        System.out.println(messageClient.getMessageUnRead("lisi"));
    }

    @Test
    public void test2(){
        R r = restTemplate.getForObject("http://eureka-client-express-message:7006/express-message/getMessageUnRead/lisi", R.class);
        System.out.println(r.getData());
    }
}
