package com.itheima.ssm.service;

import com.itheima.ssm.domain.Orders;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IOrdersService {

    /**
     * 分页查询所有订单
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    List<Orders> findAll(int page,int size) throws Exception;

    /**
     * 根据Id查询订单详情
     * @param ordersId
     * @return
     */
    Orders findById(String ordersId) throws Exception;
}
