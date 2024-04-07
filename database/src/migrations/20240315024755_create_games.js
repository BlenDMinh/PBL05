/**
 * @param { import("knex").Knex } knex
 */

const tableName = "games";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.integer("status").unsigned();
    table
      .integer("player1_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table
      .integer("player2_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table
      .integer("ruleset_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("rulesets");
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
