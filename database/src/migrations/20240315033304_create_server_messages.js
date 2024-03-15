/**
 * @param { import("knex").Knex } knex
 */

const tableName = "server_messages";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.text("content");
    table
      .integer("sender_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table.timestamp("sended_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
