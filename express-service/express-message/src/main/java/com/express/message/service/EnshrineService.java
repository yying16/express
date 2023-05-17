package com.express.message.service;

import com.express.message.dao.EnshrineMapper;
import com.express.message.domain.Enshrine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnshrineService {

    @Autowired
    EnshrineMapper enshrineMapper;



    /**添加enshrine*/
    public boolean addEnshrine(Enshrine enshrine){
        try{
            enshrineMapper.insert(enshrine);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**删除enshrine*/
    public boolean deleteEnshrine(long enshrineId){
        try{
            enshrineMapper.deleteById(enshrineId);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
