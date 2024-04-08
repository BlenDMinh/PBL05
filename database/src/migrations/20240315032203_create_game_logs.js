/**
 * @param { import("knex").Knex } knex
 */

const tableName = "game_logs";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.json("state");
    table.json("move");
    table
      .integer("player_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table
      .string("game_id")
      .notNullable()
      .references("id")
      .inTable("games");
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
