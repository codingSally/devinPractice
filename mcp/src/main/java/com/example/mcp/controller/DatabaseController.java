package com.example.mcp.controller;

import com.example.mcp.model.DatabaseEntity;
import com.example.mcp.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/database")
public class DatabaseController {
    private static final Logger logger = Logger.getLogger(DatabaseController.class.getName());

    private final DatabaseService databaseService;

    @Autowired
    public DatabaseController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatabaseEntity> getById(@PathVariable Long id) {
        logger.info("REST request to get entity by ID: " + id);
        DatabaseEntity entity = databaseService.getById(id);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(entity);
    }

    @GetMapping
    public ResponseEntity<List<DatabaseEntity>> getAll() {
        logger.info("REST request to get all entities");
        return ResponseEntity.ok(databaseService.getAll());
    }

    @PostMapping
    public ResponseEntity<DatabaseEntity> create(@RequestBody DatabaseEntity entity) {
        logger.info("REST request to create entity: " + entity);
        return new ResponseEntity<>(databaseService.create(entity), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DatabaseEntity> update(@PathVariable Long id, @RequestBody DatabaseEntity entity) {
        logger.info("REST request to update entity: " + entity);
        entity.setId(id);
        return ResponseEntity.ok(databaseService.update(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("REST request to delete entity with ID: " + id);
        if (databaseService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
