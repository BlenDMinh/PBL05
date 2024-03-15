/**
 * @param { import("knex").Knex } knex
 */

const tableName = "admin_logs";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.integer("event_type").unsigned();
    table.json("detail");
    table
      .integer("admin_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
