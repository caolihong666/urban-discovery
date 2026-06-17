package com.urbandiscovery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.urbandiscovery.dto.LoginFormDTO;
import com.urbandiscovery.dto.Result;
import com.urbandiscovery.dto.UserDTO;
import com.urbandiscovery.entity.User;
import com.urbandiscovery.mapper.UserMapper;
import com.urbandiscovery.service.IUserService;
import com.urbandiscovery.utils.RedisConstants;
import com.urbandiscovery.utils.RegexUtils;
import com.urbandiscovery.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     *
     * 发送手机验证码
     *
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        //校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            //如果不符合，返回错误信息
            return Result.fail("手机号格式错误");
        }

        //如果符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

        //保存验证码到redis当中,验证码有效期5分钟
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);

        //TODO 模拟发送验证码
        log.debug("发送验证码成功！验证码：{}，验证码有效期5分钟。", code);

        return Result.ok();
    }

    /**
     *
     * 实现登录功能
     *
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //校验手机号
        String phone = loginForm.getPhone();

        //如果手机号不通过，返回错误信息
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail("手机号错误！");
        }

        //校验验证码

        //前端传来的验证码
        String code = loginForm.getCode();
        //Redis当中的验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);

        //如果验证码不通过，返回错误信息
        //没发过验证码 || 验证码和session的验证码不一致
        //防止特殊情况的一致：如果用户不请求验证码，也不输入验证码，会出现null==null，所以才要加上判空
        if (cacheCode == null || !code.equals(cacheCode)) {
            return Result.fail("验证码错误");
        }

        //如果验证码通过，根据手机号查询用户
        User user = query().eq("phone", phone).one();

        //判断用户是否存在
        if (user == null) {
            //如果用户不存在，创建新用户,保存新用户到数据库
            user = createUserWithPhone(phone);
        }

        //保存新用户到Redis

        //1.随机生成token作为键
        String token = UUID.randomUUID().toString(true);

        // 2. 将user对象转成HashMap去保存 (带空值处理的完美版)
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true) // 忽略空值字段
                        .setFieldValueEditor((fieldName, fieldValue) -> {
                            // 【核心修复】先判空！如果是 null 就直接返回 null，避免调用 toString() 报空指针
                            if (fieldValue == null) {
                                return null;
                            }
                            return fieldValue.toString(); // 非空则转为字符串
                        }));

        stringRedisTemplate.opsForHash().putAll(RedisConstants.LOGIN_USER_KEY + token, userMap);

        //3.给用户信息设置有效期
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);

        //4.把token返回给前端
        return Result.ok(token);
    }

    //根据手机号创建用户
    private User createUserWithPhone(String phone) {
        //创建用户对象
        User user = User.builder()
                .createTime(LocalDateTime.now())
                .phone(phone)
                .nickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10))
                .build();

        //把用户对象保存到数据库当中
        save(user);

        return user;
    }
}
