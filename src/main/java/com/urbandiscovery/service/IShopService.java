package com.urbandiscovery.service;

import com.urbandiscovery.dto.Result;
import com.urbandiscovery.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {

    /**
     * 根据id查询商铺信息
     *
     * @param id 商铺id
     * @return 商铺详情数据
     */
    Result queryById(Long id);

    /**
     * 更新商铺信息
     *
     * @param shop 商铺数据
     * @return 无
     */
    Result update(Shop shop);
}
