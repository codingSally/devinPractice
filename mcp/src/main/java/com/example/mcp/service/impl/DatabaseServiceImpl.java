package com.example.mcp.service.impl;

import com.example.mcp.mapper.DatabaseMapper;
import com.example.mcp.model.DatabaseEntity;
import com.example.mcp.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    private static final Logger logger = Logger.getLogger(DatabaseServiceImpl.class.getName());

    private final DatabaseMapper databaseMapper;

    @Autowired
    public DatabaseServiceImpl(DatabaseMapper databaseMapper) {
        this.databaseMapper = databaseMapper;
    }

    @Override
    public DatabaseEntity getById(Long id) {
        logger.info("Fetching entity with ID: " + id);
        return databaseMapper.findById(id);
    }

    @Override
    public List<DatabaseEntity> getAll() {
        logger.info("Fetching all entities");
        return databaseMapper.findAll();
    }

    @Override
    @Transactional
    public DatabaseEntity create(DatabaseEntity entity) {
        logger.info("Creating new entity: " + entity);
        databaseMapper.insert(entity);
        return entity;
    }

    @Override
    @Transactional
    public DatabaseEntity update(DatabaseEntity entity) {
        logger.info("Updating entity: " + entity);
        databaseMapper.update(entity);
        return entity;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        logger.info("Deleting entity with ID: " + id);
        return databaseMapper.delete(id) > 0;
    }
}
