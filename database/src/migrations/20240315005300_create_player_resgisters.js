/**
 * @param { import("knex").Knex } knex
 */

const tableName = "player_registers";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.string("id").primary().defaultTo(knex.fn.uuid());
    table.string("display_name");
    table.string("email");
    table.string("password");
    table.string("verify_code", 6);
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
