package com.jisen.seckillweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.jisen.seckillcommon.entity.SeckillOrder;
import com.jisen.seckillcommon.inteface.OrderService;
import com.jisen.seckillcommon.inteface.SeckillService;
import com.jisen.seckillcommon.result.CodeMsg;
import com.jisen.seckillcommon.result.Result;
import com.jisen.seckillcommon.util.MD5Util;
import com.jisen.seckillcommon.util.UUIDUtil;
import com.jisen.seckillcommon.vo.SkMessage;
import com.jisen.seckillcommon.vo.UserVo;
import com.jisen.seckillcommon.vo.profix.GoodsKeyPrefix;
import com.jisen.seckillcommon.vo.profix.OrderKeyPrefix;
import com.jisen.seckillcommon.vo.profix.SkKeyPrefix;
import com.jisen.seckillweb.mvcconfig.intercepter.AccessLimit;
import com.jisen.seckillweb.rabbitmq.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jisen
 * @date 2019/7/7 10:11
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController {

    /**
     * 用于内存标记，标记库存是否为空，从而减少redis的访问
     */
    private Map<Long,Object> localOverMap = new HashMap();

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Reference(interfaceClass = OrderService.class)
    private OrderService orderServiceImpl;

    @Reference(interfaceClass = SeckillService.class)
    private SeckillService seckillServiceImpl;

    /**
     * 获取秒杀接口地址
     * 1，第一次点击秒杀，都会生成一个随机的秒杀地址返回给客户端
     * 2，对秒杀次数做限制（通过自定义拦截注解完成）
     *
     * @param model
     * @param userVo
     * @param goodsId 秒杀商品id
     * @param veryfyCode 验证码
     * @return 被隐藏的秒杀接口路径
     */

    @AccessLimit(seconds = 5,maxAccessCount = 5,needLogin = true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    public Result<String> getSeckillPath(Model model, UserVo userVo, @RequestParam("goodsId") long goodsId,@RequestParam(value = "verifyCode",defaultValue = "0") int veryfyCode){
        //在执行这个
        model.addAttribute("user",userVo);
        if(goodsId <= 0){
            String msg = CodeMsg.SECKILL_PARM_ILLEGAL.getMsg();
            CodeMsg.SECKILL_PARM_ILLEGAL.setMsg(msg+"商品id小于0");
            return Result.error(CodeMsg.SECKILL_PARM_ILLEGAL);
        }
        //校验验证码
        boolean check = this.checkVerifyCode(userVo,goodsId,veryfyCode);
        if(check){
            return Result.error(CodeMsg.VERITF_FAIL);//检验不通过，请求非法
        }
        //检验通过，获取秒杀路径
        String path = this.crateSkPath(userVo,goodsId);

        return Result.success(path);
    }

    @RequestMapping(value = "{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill(Model model, UserVo userVo, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path){
        model.addAttribute("user",userVo);
        //验证path是否正确
        boolean chech = this.checPath(userVo,goodsId,path);
        if(chech){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);//非法请求
        }

        //通过内存标记，减少对redis的访问，秒杀未结束才继续访问redis
        boolean over = (Boolean) localOverMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //预减库存，同时在库存为0时标记该商品已经结束秒杀
        long decrement = redisTemplate.opsForValue().decrement(GoodsKeyPrefix.GOODS_STOCK.getPrefix() + goodsId);
        if(decrement < 0){
            localOverMap.put(goodsId,true);//秒杀结束，标记该商品已经秒杀结束
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        //判断是否重复秒杀
        //从redis中取缓存，减少数据库的访问
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get(OrderKeyPrefix.SK_ORDER.getPrefix() + ":" + userVo.getUuid() + "_" + goodsId);
        if (seckillOrder == null){
            seckillOrder = orderServiceImpl.getSeckillOrderByUserIdAndGoodsId(userVo.getUuid(),goodsId);
        }
        if(seckillOrder != null){
            return Result.error(CodeMsg.REPEATE_SECKILL);//重复秒杀
        }
        //商品有库存且用户为秒杀商品，则将秒杀请求放入MQ
        SkMessage skMessage = new SkMessage();
        skMessage.setUser(userVo);
        skMessage.setGoodsId(goodsId);

        //放入mq
        rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_QUEUE, JSONObject.toJSONString(skMessage));

        //排队中
        return Result.success(0);
    }


    /**
     * 检验路径是否正确
     *
     * @param userVo
     * @param goodsId
     * @param path
     * @return
     */
    private boolean checPath(UserVo userVo, long goodsId, String path) {
        if (userVo == null || goodsId <= 0){
            return false;
        }
        String oldPath = (String)redisTemplate.opsForValue().get(SkKeyPrefix.SK_PATH.getPrefix()+userVo.getUuid()+"_"+goodsId);
        return path.equals(oldPath);
    }

    /**
     * 创建秒杀地址，并将其存储在redis中
     *
     * @param userVo
     * @param goodsId
     * @return
     */
    private String crateSkPath(UserVo userVo, long goodsId) {
        if (userVo == null || goodsId <= 0){
            return null;
        }
        //随机生成秒杀地址
        String path = MD5Util.md5(UUIDUtil.uuid()+"123456");
        //将随机生成的秒杀地址存储在redis中（保证不同用户和不同商品的秒杀地址是不一样的）
        redisTemplate.opsForValue().set(SkKeyPrefix.SK_PATH.getPrefix()+userVo.getUuid()+"_"+goodsId,path);
        return path;
    }

    /**
     * 检验校验码,与redis中的校验码对比
     * @param userVo
     * @param goodsId
     * @param veryfyCode
     * @return
     */
    private boolean checkVerifyCode(UserVo userVo, long goodsId, int veryfyCode) {
        if(userVo == null || goodsId <= 0){
            return false;
        }
        //从redis中获取验证码计算结果
        Integer oldCode = (Integer)redisTemplate.opsForValue().get(SkKeyPrefix.VERIFY_RESULT.getPrefix() + userVo.getUuid() + "_" + goodsId);
        if (oldCode == null || oldCode-veryfyCode != 0){
            return false;
        }
        //如果校验成功，则说明验证码过期，删除校验码缓存，防止重复提交同一个验证码
        redisTemplate.delete(SkKeyPrefix.VERIFY_RESULT.getPrefix()+userVo.getUuid()+"_"+goodsId);
        return true;
    }

    public Result<Long> getSeckillResult(Model model, UserVo userVo,@RequestParam("goodsId") long goodsId){
        model.addAttribute("user",userVo);
        //seckillService.
        return Result.success(0L);
    }

}
