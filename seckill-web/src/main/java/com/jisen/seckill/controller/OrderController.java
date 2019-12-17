package com.jisen.seckill.controller;

import com.jisen.seckill.entity.OrderInfo;
import com.jisen.seckill.inteface.GoodsService;
import com.jisen.seckill.inteface.OrderService;
import com.jisen.seckill.result.CodeMsg;
import com.jisen.seckill.result.Result;
import com.jisen.seckill.vo.GoodsVo;
import com.jisen.seckill.vo.OrderDetailVo;
import com.jisen.seckill.vo.UserVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 订单服务接口
 * @author jisen
 * @date 2019/7/14 16:00
 */

@Controller
@RequestMapping("/order")
public class OrderController {

    @Reference(interfaceClass = OrderService.class)
    private OrderService orderServiceImpl;
    @Reference(interfaceClass = GoodsService.class)
    private GoodsService goodsServiceImpl;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> orderInfo(Model model, UserVo userVo, @RequestParam("orderId") long orderId){
        if(userVo == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //获取订单信息
        OrderInfo order = orderServiceImpl.getOrderById(orderId);
        if (order == null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        //如果订单存在，则根据订单信息获取商品信息
        Long goodsId = order.getGoodsId();
        GoodsVo goodsVo = goodsServiceImpl.getGoodsVoByGoodsId(goodsId);

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setUser(userVo);
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoods(goodsVo);

        return Result.success(orderDetailVo);

    }
}
