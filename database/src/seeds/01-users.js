/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */

const tableName = 'users'
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del()
  await knex(tableName).insert([{ id: 1, username: 'user1' }])
}
