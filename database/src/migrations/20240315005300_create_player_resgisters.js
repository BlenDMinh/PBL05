/**
 * @param { import("knex").Knex } knex
 */

const tableName = "player_registers";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.string("display_name");
    table.string("email");
    table.string("password");
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
