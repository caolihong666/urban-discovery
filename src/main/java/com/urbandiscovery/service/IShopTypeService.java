package com.urbandiscovery.service;

import com.urbandiscovery.dto.Result;
import com.urbandiscovery.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @since 2021-12-22
 */
public interface IShopTypeService extends IService<ShopType> {

    /**
     *
     * 查询店铺类型
     *
     *
     */
    Result queryTypeList();
}
