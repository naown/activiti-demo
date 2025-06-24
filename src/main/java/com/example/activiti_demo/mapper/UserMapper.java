package com.example.activiti_demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.activiti_demo.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
 
/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ningbeibei
 * @since 2024-09-26
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
 
}