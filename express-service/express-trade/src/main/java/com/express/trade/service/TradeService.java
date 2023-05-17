package com.express.trade.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.express.trade.dao.TradeMapper;
import com.express.trade.domain.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TradeService {

    @Autowired
    TradeMapper tradeMapper;

    public List<Trade> getTrades(){
        QueryWrapper<Trade> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        return tradeMapper.selectList(wrapper);
    }

    public boolean addTrade(Trade trade){
        try{
            tradeMapper.insert(trade);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrade(Trade trade){
        try{
            tradeMapper.updateById(trade);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
