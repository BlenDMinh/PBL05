/**
 * @param { import("knex").Knex } knex
 */

const tableName = "friendships";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table
      .integer("user_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table
      .integer("friend_id")
      .unsigned()
      .notNullable()
      .references("id")
      .inTable("users");
    table.timestamp("created_at").defaultTo(knex.fn.now());
    table.primary(["user_id", "friend_id"]);
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
