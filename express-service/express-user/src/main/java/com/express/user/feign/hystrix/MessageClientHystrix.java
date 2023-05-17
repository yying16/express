package com.express.user.feign.hystrix;

import com.express.common.vo.R;
import com.express.user.feign.MessageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageClientHystrix implements MessageClient {

    @Override
    public R getMessageUnRead(String account) {
        return R.failed();
    }
}
