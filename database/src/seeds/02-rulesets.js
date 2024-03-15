/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const tableName = "rulesets";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  await knex(tableName).insert([
    {
      id: 1,
      name: "Chơi tự do",
      detail: {
        minute_per_turn: 0,
        total_minute_per_player: 0,
        turn_around_steps: 0,
        turn_around_time_plus: 0,
      },
    },
  ]);
};
