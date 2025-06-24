package com.example.activiti_demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.activiti_demo.entity.UserEntity;
import com.example.activiti_demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp extends ServiceImpl<UserMapper, UserEntity> implements UserService {
}