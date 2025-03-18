package com.example.mcp.service;

import com.example.mcp.model.DatabaseEntity;
import java.util.List;

public interface DatabaseService {
    DatabaseEntity getById(Long id);
    List<DatabaseEntity> getAll();
    DatabaseEntity create(DatabaseEntity entity);
    DatabaseEntity update(DatabaseEntity entity);
    boolean delete(Long id);
}
