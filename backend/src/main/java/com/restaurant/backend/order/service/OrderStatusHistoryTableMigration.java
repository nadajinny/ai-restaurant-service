package com.restaurant.backend.order.service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusHistoryTableMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OrderStatusHistoryTableMigration.class);
    private static final String LEGACY_TABLE = "order_status_histories";
    private static final String CURRENT_TABLE = "order_status_history";

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public OrderStatusHistoryTableMigration(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean hasLegacyTable = tableExists(LEGACY_TABLE);
        boolean hasCurrentTable = tableExists(CURRENT_TABLE);

        if (!hasLegacyTable || !hasCurrentTable) {
            return;
        }

        int migratedRows = jdbcTemplate.update("""
                insert into order_status_history (id, created_at, updated_at, changed_by, from_status, to_status, order_id)
                select legacy.id,
                       legacy.created_at,
                       legacy.updated_at,
                       legacy.changed_by,
                       legacy.from_status,
                       legacy.to_status,
                       legacy.order_id
                 from order_status_histories legacy
                 where not exists (
                       select 1
                         from order_status_history target_row
                        where target_row.id = legacy.id
                 )
                """);

        if (migratedRows > 0) {
            log.info("Migrated {} rows from {} to {}", migratedRows, LEGACY_TABLE, CURRENT_TABLE);
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(null, null, tableName, null)) {
                while (tables.next()) {
                    String currentName = tables.getString("TABLE_NAME");
                    if (tableName.equalsIgnoreCase(currentName)) {
                        return true;
                    }
                }
            }

            try (ResultSet tables = metadata.getTables(null, null, tableName.toUpperCase(), null)) {
                while (tables.next()) {
                    String currentName = tables.getString("TABLE_NAME");
                    if (tableName.equalsIgnoreCase(currentName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
