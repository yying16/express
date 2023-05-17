package com.express.trade.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.express.common.vo.R;
import com.express.trade.domain.Trade;
import com.express.trade.service.TradeCacheService;
import com.express.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TradeController {

    @Autowired
    TradeService tradeService;

    @Autowired
    TradeCacheService tradeCacheService;

    /**
     * 查询/获取所有帖子
     * */
    @PostMapping("/getTrades/{currentPage}/{pageSize}/{account}")
    public R getTrades(@PathVariable long currentPage, @PathVariable long pageSize, @RequestBody Map<String, String> condition, @PathVariable String account) {
        IPage iPage = tradeCacheService.getTrades(currentPage,pageSize,condition,account);
        if(iPage==null)
            return R.failed();
        return R.ok(iPage);
    }

    /**
     * 添加帖子（先添加到redis,再用kafka异步写入数据库）
     * */
    @PostMapping("/addTrade")
    public R addTrade(@RequestBody Trade trade){
        if(tradeCacheService.addTrade(trade)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getDetail/{id}")
    public R getDetail(@PathVariable String id){
        Trade trade = tradeCacheService.getDetail(id);
        if(trade==null){
            return R.failed();
        }
        return R.ok(trade);
    }

    @GetMapping("/delete/{tradeId}")
    public R delete(@PathVariable String tradeId){
        if(tradeCacheService.delete(tradeId)){
            return R.ok();
        }
        return R.failed();
    }

    @GetMapping("/getMyCollection/{account}")
    public R getMyCollection(@PathVariable String account){
        List<Trade> list = tradeCacheService.getMyCollection(account);
        if(list==null)
            return R.failed();
        return R.ok(list);
    }
}
