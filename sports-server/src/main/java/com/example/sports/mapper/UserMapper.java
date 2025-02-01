package com.example.sports.mapper;

import com.example.sports.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE nickname = #{nickname}")
    User findByNickname(String nickname);

    @Insert("INSERT INTO user (nickname, phone, credential, status, points, level, created_at, updated_at) " +
            "VALUES (#{nickname}, #{phone}, #{credential}, #{status}, #{points}, #{level}, #{createdAt}, #{updatedAt})")
    void insert(User user);

    @Update("UPDATE user SET grip_style = #{gripStyle}, racket_config = #{racketConfig}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    void update(User user);

    @Select("SELECT * FROM user WHERE nickname LIKE CONCAT('%', #{nickname}, '%')")
    List<User> searchByNickname(String nickname);
} 