package com.urbandiscovery.service.impl;

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
    @SuppressWarnings("unchecked")
    public Result queryTypeList() {
        //查询Redis缓存，是否存在店铺类型信息
        String shopTypeJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE_KEY);

        //如果存在，返回信息
        if (StrUtil.isNotBlank(shopTypeJson)) {
            return Result.ok(JSONUtil.toList(JSONUtil.parseArray(shopTypeJson), ShopType.class));
        }

        //如果不存在，查询数据库
        List<ShopType> shopTypes = lambdaQuery()
                .orderByAsc(ShopType::getSort)
                .list();

        //如果数据库不存在，返回错误信息
        if (shopTypes == null) {
            return Result.fail("商铺类型不存在");
        }

        //如果数据库存在，写入缓存
        String jsonStr = JSONUtil.toJsonStr(shopTypes);
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_TYPE_KEY, jsonStr);

        //返回店铺类型信息
        return Result.ok(shopTypes);
    }
}
