package com.jisen.seckill.rabbitlistener;

import com.alibaba.fastjson.JSONObject;
import com.jisen.seckill.entity.SeckillOrder;
import com.jisen.seckill.inteface.GoodsService;
import com.jisen.seckill.inteface.OrderService;
import com.jisen.seckill.vo.GoodsVo;
import com.jisen.seckill.vo.SkMessage;
import com.jisen.seckill.vo.UserVo;
import com.jisen.seckill.vo.profix.OrderKeyPrefix;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jisen
 * @date 2019/11/25 20:08
 */
@Component
@Slf4j
public class OrderListener {

    @Autowired
    RedisTemplate redisTemplate;

    @Reference(interfaceClass = GoodsService.class)
    private GoodsService goodsServiceImpl;

    @Reference(interfaceClass = OrderService.class)
    private OrderService orderServiceImpl;

    /**
     * 消息队列名
     */
    public static final String SECKILL_QUEUE = "seckill.queue";

    /**
     * 双MQ只要指定监听的容器工厂,mq消息，
     * @param message
     * @param deliveryTag
     * @param channel
     */
    @RabbitListener(queues = SECKILL_QUEUE)
    @RabbitHandler
    @Transactional
    public void receive(String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
        try {
            //设置该队列是否自动确认消费,设置否
            //channel.basicConsume(SECKILL_QUEUE, false, new DefaultConsumer(channel));

            log.info("收到制造平台发来的队列消息{}", message);

            SkMessage skMessage = JSONObject.parseObject(message,SkMessage.class);


            // 获取秒杀用户信息与商品id
            UserVo user = skMessage.getUser();
            long goodsId = skMessage.getGoodsId();

            // 获取商品的库存,这一步其实可以不用写啊，写了更保险
            GoodsVo goods = goodsServiceImpl.getGoodsVoByGoodsId(goodsId);
            Integer stockCount = goods.getStockCount();
            if (stockCount <= 0) {
                return;
            }

            // 判断是否已经秒杀到了（保证秒杀接口幂等性），秒杀成功的订单摘要会放入redis
            SeckillOrder order = this.getSkOrderByUserIdAndGoodsId(user.getUserId(), goodsId);
            if (order != null) {
                return;
            }

            // 1.减库存 2.写入订单 3.写入秒杀订单
            //orderServiceImpl.seckill(user, goods);

            //处理MQ业务数据，完成MQ业务数据后手动应答

            orderServiceImpl.saveSeckillOrder(user, goods);

            //正常入库,手动应答
            //channel.basicAck(deliveryTag, false);

            log.info("消息处理完毕");
        } catch (Exception ex) {
            //异常入库,重试中，MQ业务处理异常的时候，一直会尝试再次发送，这里的问题是会不会阻塞其他的消息
//            try {
//                channel.basicNack(deliveryTag, false, true);
//            } catch (IOException e) {
//                log.error("{}",e);
//            }
            log.error("设备信息入库失败,重试中>>>>>>{}",ex);
        }

    }


    /**
     * 通过用户id与商品id从订单列表中获取订单信息，这个地方用了唯一索引（unique index!!!!!）
     * <p>
     * 优化，不同每次都去数据库中读取秒杀订单信息，而是在第一次生成秒杀订单成功后，
     * 将订单存储在redis中，再次读取订单信息的时候就直接从redis中读取
     *
     * @param userId
     * @param goodsId
     * @return 秒杀订单信息
     */
    private SeckillOrder getSkOrderByUserIdAndGoodsId(String userId, long goodsId) {

        // 从redis中取缓存，减少数据库的访问
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get(OrderKeyPrefix.SK_ORDER+":" + userId + "_" + goodsId);
        if (seckillOrder != null) {
            return seckillOrder;
        }
        return orderServiceImpl.getSeckillOrderByUserIdAndGoodsId(userId, goodsId);
    }

}
