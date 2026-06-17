package com.urbandiscovery.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.urbandiscovery.dto.Result;
import com.urbandiscovery.entity.ShopType;
import com.urbandiscovery.mapper.ShopTypeMapper;
import com.urbandiscovery.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urbandiscovery.utils.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     *
     * 查询店铺类型
     *
     */
    @Override
    @SuppressWarnings("unchecked")//抑制警告
    public Result queryTypeList() {
        //先查redis里面的数据 key是cache:shop:type: 值是所有商铺类型信息
        String shopTypeJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE_KEY);

        //如果能查到，封装成集合，返回
        if (StrUtil.isNotBlank(shopTypeJson)) {
            List<ShopType> list = JSONUtil.toList(shopTypeJson, ShopType.class);
            return Result.ok(list);
        }

        //如果查不到，查询数据库,并排序
        List<ShopType> list = lambdaQuery()
                .orderByAsc(ShopType::getSort)
                .list();

        //如果数据库查询不到，数据不存在，返回报错信息
        if (CollUtil.isEmpty(list)) {
            return Result.fail("数据不存在！");
        }

        //如果数据库能查到,把查询到的数据写入缓存当中
        String jsonStr = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_TYPE_KEY, jsonStr);

        //返回数据
        return Result.ok(list);
    }
}
