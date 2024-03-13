package persistence.context;

import persistence.entity.EntityId;
import persistence.sql.metadata.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {
    private final Map<EntityId, Object> firstLevelCache = new HashMap<>();
    private final Map<EntityId, EntitySnapshot> snapshots = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        return clazz.cast(firstLevelCache.get(EntityId.of(clazz, id)));
    }

    @Override
    public void addEntity(Object id, Object entity) {
        firstLevelCache.put(EntityId.of(entity.getClass(), id), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), entity);
        firstLevelCache.remove(EntityId.of(entity.getClass(), entityMetadata.getPrimaryKey().getValue()));
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object id, Object entity) {
        EntityId entityId = EntityId.of(entity.getClass(), id);
        return snapshots.computeIfAbsent(
                entityId, key -> EntitySnapshot.from(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object id, Object entity) {
        return snapshots.get(EntityId.of(entity.getClass(), id));
    }
}
