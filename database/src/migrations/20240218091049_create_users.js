/**
 * @param { import("knex").Knex } knex
 */
const DEFAULT_PASSWORD =
  '$2b$10$4WxWKojNnKfDAicVsveh7.ogkWOBMV1cvRUSPCXwxA05NRX18F0QW'
const tableName = 'users'
exports.up = async (knex) => {
  await knex.schema.createTable(tableName, (table) => {
    table.increments('id').unsigned().primary()
    table.string('username')
    table.string('password').defaultTo(DEFAULT_PASSWORD)
  })
}
/**
 * @param { import("knex").Knex } knex
 */
exports.down = async (knex) => {
  await knex.schema.dropTable(tableName)
}
