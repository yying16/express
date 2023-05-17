package com.express.user.feign;

import com.express.common.vo.R;
import com.express.user.feign.hystrix.MessageClientHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "eureka-client-express-message", fallback = MessageClientHystrix.class)
public interface MessageClient {

    @GetMapping("/express-message/getMessageUnRead/{account}")
    R getMessageUnRead(@PathVariable String account);

}
