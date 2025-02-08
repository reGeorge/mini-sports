package com.example.sports.mapper;

import com.example.sports.entity.User;
import com.example.sports.entity.PointsRecord;
import com.example.sports.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Options;

import java.util.List;

@Mapper
public interface UserMapper {
    
    @Select("SELECT * FROM user WHERE id = #{id}")
    User selectById(Long id);
    
    @Select("SELECT * FROM user")
    List<User> findAll();

    @Select("SELECT * FROM user WHERE nickname = #{nickname}")
    User findByNickname(String nickname);

    @Insert("INSERT INTO user (nickname, phone, credential, status, points, level, created_at, updated_at) " +
            "VALUES (#{nickname}, #{phone}, #{credential}, #{status}, #{points}, #{level}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE user SET grip_style = #{gripStyle}, racket_config = #{racketConfig}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    void update(User user);

    @Update("UPDATE user SET points = #{points}, level = #{level}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateById(User user);

    @Select("SELECT * FROM user WHERE nickname LIKE CONCAT('%', #{nickname}, '%')")
    List<User> searchByNickname(String nickname);

    @Insert("INSERT INTO user_role(user_id, role_id) VALUES(#{userId}, #{roleId})")
    int addUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);
    
    @Delete("DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int removeUserRole(@Param("userId") Long userId, @Param("roleId") Integer roleId);

    @Select("SELECT r.* FROM role r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId}")
    List<Role> findUserRoles(Long userId);

    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);

    @Select("SELECT points FROM user WHERE id = #{userId}")
    Integer getUserPoints(Long userId);

    @Select("SELECT * FROM user ORDER BY points DESC LIMIT #{limit}")
    List<User> getPointsRanking(int limit);

    @Select("SELECT pr.* FROM points_record pr WHERE pr.user_id = #{userId} ORDER BY pr.created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<PointsRecord> getPointsRecords(@Param("userId") Long userId, @Param("offset") int offset, @Param("size") int size);

    @Update("UPDATE user SET points = #{points} WHERE id = #{userId}")
    void updateUserPoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Select("SELECT COUNT(*) FROM points_record WHERE user_id = #{userId}")
    long getPointsRecordsCount(Long userId);
}