package com.example.mcp.mapper;

import com.example.mcp.model.Prompt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PromptMapper {
    Prompt findById(@Param("id") Long id);
    List<Prompt> findAll();
    List<Prompt> findByCategory(@Param("category") String category);
    int insert(Prompt prompt);
    int update(Prompt prompt);
    int delete(@Param("id") Long id);
}
