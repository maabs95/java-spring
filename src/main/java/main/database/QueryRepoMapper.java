package main.database;

import main.dto.UserData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("main.database.QueryRepoMapper")
public interface QueryRepoMapper {
    public List<UserData> getUserList();

}

