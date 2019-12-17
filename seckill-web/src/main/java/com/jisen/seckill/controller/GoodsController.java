package com.jisen.seckill.controller;

import com.jisen.seckill.inteface.GoodsService;
import com.jisen.seckill.result.Result;
import com.jisen.seckill.vo.GoodsVo;
import com.jisen.seckill.vo.GoodsDetailVo;
import com.jisen.seckill.vo.UserVo;
import com.jisen.seckill.vo.profix.GoodsKeyPrefix;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 这个Controller获取商品列表
 * @author jisen
 * @date 2019/6/29 16:32
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static Logger logger = LoggerFactory.getLogger(GoodsController.class);
    @Autowired
    RedisTemplate redisTemplate;

    @Reference(interfaceClass = GoodsService.class)
    private GoodsService goodsServiceImpl;

    /**
     * 因为redis缓存中不存在页面缓存时需要手动渲染，所以注入一个视图解析器，自定义渲染
     */
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 加responsebody返回给页面的是对象转为json，不加返回的是视图名称，因为本方法要兼顾缓存，所以将渲染好的有动态数据html返回到页面，
     *
     * @return
     */
    @RequestMapping(value = "/goodsList",produces = "text/html")
    @ResponseBody
    public String goodList(HttpServletRequest request, HttpServletResponse response, Model model,UserVo userVo){
        logger.info("获取商品列表");

        //从redis中获取用户
        //SeckillUser seckillUser = (SeckillUser)redisTemplate.opsForValue().get(SkUserKeyPrefix.TOKEN.getPrefix()+userToken);

        //1,从redis中获取缓存的页面
        String redisGoodsListHtml = (String)redisTemplate.opsForValue().get(GoodsKeyPrefix.GOODS_LIST_HTML.getPrefix());


        if(StringUtils.isNotEmpty(redisGoodsListHtml)){
            return redisGoodsListHtml;
        }




        //2，如果为空则要绚染页面
        //查询商品列表，用于手动渲染时将商品数据填充的页面
        List<GoodsVo> goodList = goodsServiceImpl.listGoods();
        model.addAttribute("user",userVo);
        model.addAttribute("goodsList",goodList);


        //3，渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        String goodsListHtml = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);

        if(StringUtils.isNotEmpty(goodsListHtml)){
            redisTemplate.opsForValue().set(GoodsKeyPrefix.GOODS_LIST_HTML.getPrefix(),goodsListHtml);
        }

        return goodsListHtml;
    }

    /**
     * 获取商品详情
     * @param userVo
     * @param goodsId
     * @return
     */
    @RequestMapping("getDetails/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> getDetails(UserVo userVo, @PathVariable long goodsId){
        logger.info("获取商品详情");
        //通过商品id在数据库中查询
        GoodsVo goodsVo = goodsServiceImpl.getGoodsVoById(goodsId);
        //获取商品的秒杀开始与结束时间
        long startTime = goodsVo.getStartDate().getTime();
        long endTime = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();

        //秒杀状态；0秒杀未开始，1秒杀进行中，2秒杀已结束
        int skStatus = 0;
        //秒杀剩余时间
        int remainSeconds = 0;

        if(now<startTime){
            //秒杀未开始
            skStatus = 0;
            remainSeconds = (int)((startTime - now)/1000);
        }else if(now>endTime){
            //秒杀已结束
            skStatus = 1;
            remainSeconds = -1;
        }else {
            //秒杀进行中
            skStatus = 1;
            remainSeconds = 0;
        }

        //服务端封装商品数据直接传送给客户端，而不用绚染页面
        // 服务端封装商品数据直接传递给客户端，而不用渲染页面
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goodsVo);
        goodsDetailVo.setUser(userVo);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(skStatus);

        return Result.success(goodsDetailVo);
    }
}
