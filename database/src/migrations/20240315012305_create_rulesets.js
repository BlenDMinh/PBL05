/**
 * @param { import("knex").Knex } knex
 */

const tableName = "rulesets";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.string("name");
    table.string("detail");
    table.string("description");
    table.boolean("published").defaultTo(false);
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
