package main.database;

import main.dto.UserPrincipal;
import main.dto.UserData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("main.database.QueryRepoMapper")
public interface QueryRepoMapper {
    List<UserData> getUserList();

    UserData getUserByUsername(@Param("username") String username);

    int insertUser(UserData userData);

    int updateUser(UserData userData);

    int deleteUser(@Param("username") String username);

    UserPrincipal getUserAndPassAndRole(@Param("username") String username);

}

