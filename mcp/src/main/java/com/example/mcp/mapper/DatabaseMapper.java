package com.example.mcp.mapper;

import com.example.mcp.model.DatabaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DatabaseMapper {
    DatabaseEntity findById(@Param("id") Long id);
    List<DatabaseEntity> findAll();
    int insert(DatabaseEntity entity);
    int update(DatabaseEntity entity);
    int delete(@Param("id") Long id);
}
