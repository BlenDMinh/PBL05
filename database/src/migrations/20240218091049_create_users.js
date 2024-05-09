/**
 * @param { import("knex").Knex } knex
 */
// 123456
const DEFAULT_PASSWORD =
  "$2b$10$4WxWKojNnKfDAicVsveh7.ogkWOBMV1cvRUSPCXwxA05NRX18F0QW";

const USER_STATUS = {
  ACTIVE: 0,
  INACTIVE: 1,
  LOCKED: 2,
};

const USER_ROLE = {
  ADMIN: 0,
  PLAYER: 1,
};

exports.USER_ROLE = USER_ROLE;
exports.USER_STATUS = USER_STATUS;

const tableName = "users";
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments("id").unsigned().primary();
    table.string("display_name");
    table.string("email").unique().index();
    table.integer("status").unsigned().defaultTo(USER_STATUS.ACTIVE);
    table.boolean("online").defaultTo(false);
    table.string("password").defaultTo(DEFAULT_PASSWORD);
    table.string("avatar_url");
    table.integer("elo").defaultTo(1000);
    table.integer("role").unsigned().defaultTo(USER_ROLE.PLAYER);
    table.timestamp("created_at").defaultTo(knex.fn.now());
  });
};
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName);
};
