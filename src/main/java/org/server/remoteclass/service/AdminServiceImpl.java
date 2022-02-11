package org.server.remoteclass.service;

import org.modelmapper.ModelMapper;
import org.server.remoteclass.dto.ResponseUserByAdminDto;
import org.server.remoteclass.dto.ResponseUserDto;
import org.server.remoteclass.entity.User;
import org.server.remoteclass.jpa.UserRepository;
import org.server.remoteclass.util.BeanConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AdminServiceImpl(UserRepository userRepository, BeanConfiguration beanConfiguration) {
        this.userRepository = userRepository;
        this.modelMapper = beanConfiguration.modelMapper();
    }

    @Override
    public ResponseUserByAdminDto getUser(Long userId) {
        return ResponseUserByAdminDto.from(userRepository.findByUserId(userId).orElse(null));
    }

    @Override
    public List<ResponseUserByAdminDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, ResponseUserByAdminDto.class)).collect(Collectors.toList());
    }
}
