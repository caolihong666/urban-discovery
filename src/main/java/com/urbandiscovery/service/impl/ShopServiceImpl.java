package com.urbandiscovery.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.urbandiscovery.dto.Result;
import com.urbandiscovery.entity.Shop;
import com.urbandiscovery.mapper.ShopMapper;
import com.urbandiscovery.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urbandiscovery.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据id查询商铺信息
     *
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @Override
    public Result queryById(Long id) {
        //从redis查询商铺缓存 key是字符串，value是字符串
        String shopJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_KEY + id);

        //判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            //如果存在,转成json对象，返回信息
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }

        //如果命中的是空值
        //shopJson=“”代表查过一次但是数据不存在；shopJson=null表示查询Redis缓存未命中
        if (shopJson != null) {
            return Result.fail("商铺不存在！");
        }

        //如果不存在，根据id查询数据库
        Shop shop = getById(id);

        //如果不存在
        if (shop == null) {
            //将空值写入redis
            stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);

            //返回错误
            return Result.fail("商铺不存在！");
        }

        //如果存在，把数据写入redis当中
        String jsonStr = JSONUtil.toJsonStr(shop);
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY + id, jsonStr, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);

        //返回信息
        return Result.ok(shop);
    }

    /**
     * 更新商铺信息
     *
     * @param shop 商铺数据
     * @return 无
     */
    @Override
    @Transactional
    public Result update(Shop shop) {
        //如果店铺id为空，返回错误
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空！");
        }

        //如果店铺不存在
        Shop shopById = getById(id);
        if (shopById == null) {
            return Result.fail("店铺不存在！");
        }

        //更新数据库
        updateById(shop);

        //删除缓存
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + id);

        return Result.ok();
    }
}
