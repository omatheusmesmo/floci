package io.github.hectorvent.floci.services.rds.model;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.hectorvent.floci.core.storage.PersistentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The apply methods are stored beside the parameters and must survive persistence; a record
 * persisted before they were stored loads without them and the map normalizes to empty.
 */
class ParameterGroupApplyMethodsPersistenceTest {

    @Test
    void parameterGroupApplyMethodsPersistAcrossInstances(@TempDir Path directory) {
        Path file = directory.resolve("rds-parameter-groups.json");
        DbParameterGroup group = new DbParameterGroup("apply-persist-pg", "postgres16", "apply methods");
        group.getParameters().put("max_connections", "250");
        group.getApplyMethods().put("max_connections", "pending-reboot");

        PersistentStorage<String, DbParameterGroup> writer = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbParameterGroup>>() {});
        writer.put(group.getDbParameterGroupName(), group);

        PersistentStorage<String, DbParameterGroup> reader = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbParameterGroup>>() {});
        reader.load();

        Optional<DbParameterGroup> restored = reader.get(group.getDbParameterGroupName());
        assertTrue(restored.isPresent());
        assertEquals("250", restored.get().getParameters().get("max_connections"));
        assertEquals("pending-reboot", restored.get().getApplyMethods().get("max_connections"));
    }

    @Test
    void aRecordWithoutPersistedApplyMethodsLoadsAnEmptyMap(@TempDir Path directory) {
        Path file = directory.resolve("rds-parameter-groups.json");
        DbParameterGroup group = new DbParameterGroup("legacy-pg", "postgres16", "legacy");
        group.getParameters().put("max_connections", "250");
        group.setApplyMethods(null);

        PersistentStorage<String, DbParameterGroup> writer = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbParameterGroup>>() {});
        writer.put(group.getDbParameterGroupName(), group);

        PersistentStorage<String, DbParameterGroup> reader = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbParameterGroup>>() {});
        reader.load();

        Optional<DbParameterGroup> restored = reader.get(group.getDbParameterGroupName());
        assertTrue(restored.isPresent());
        assertTrue(restored.get().getApplyMethods().isEmpty());
    }

    @Test
    void clusterParameterGroupApplyMethodsPersistAcrossInstances(@TempDir Path directory) {
        Path file = directory.resolve("rds-cluster-parameter-groups.json");
        DbClusterParameterGroup group = new DbClusterParameterGroup("apply-persist-cpg", "aurora-postgresql16", "apply methods");
        group.getParameters().put("log_statement", "all");
        group.getApplyMethods().put("log_statement", "pending-reboot");

        PersistentStorage<String, DbClusterParameterGroup> writer = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbClusterParameterGroup>>() {});
        writer.put(group.getDbClusterParameterGroupName(), group);

        PersistentStorage<String, DbClusterParameterGroup> reader = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbClusterParameterGroup>>() {});
        reader.load();

        Optional<DbClusterParameterGroup> restored = reader.get(group.getDbClusterParameterGroupName());
        assertTrue(restored.isPresent());
        assertEquals("all", restored.get().getParameters().get("log_statement"));
        assertEquals("pending-reboot", restored.get().getApplyMethods().get("log_statement"));
    }

    @Test
    void aClusterRecordWithoutPersistedApplyMethodsLoadsAnEmptyMap(@TempDir Path directory) {
        Path file = directory.resolve("rds-cluster-parameter-groups.json");
        DbClusterParameterGroup group = new DbClusterParameterGroup("legacy-cpg", "aurora-postgresql16", "legacy");
        group.getParameters().put("log_statement", "all");
        group.setApplyMethods(null);

        PersistentStorage<String, DbClusterParameterGroup> writer = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbClusterParameterGroup>>() {});
        writer.put(group.getDbClusterParameterGroupName(), group);

        PersistentStorage<String, DbClusterParameterGroup> reader = new PersistentStorage<>(
                file, new TypeReference<Map<String, DbClusterParameterGroup>>() {});
        reader.load();

        Optional<DbClusterParameterGroup> restored = reader.get(group.getDbClusterParameterGroupName());
        assertTrue(restored.isPresent());
        assertTrue(restored.get().getApplyMethods().isEmpty());
    }
}
