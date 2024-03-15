/**
 * @param { import("knex").Knex } knex
 */

const tableName = "hack_reports";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.string("description");
    table
      .integer("reporter_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table
      .integer("violator_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table.boolean("resolved").defaultTo(false);
    table.timestamp("reported_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
