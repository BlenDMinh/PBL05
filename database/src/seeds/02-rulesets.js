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
      name: "Chơi 10 phút",
      detail: {
        minute_per_turn: 3,
        total_minute_per_player: 10,
        turn_around_steps: -1,
        turn_around_time_plus: -1,
      },
      published: true,
    },
    {
      name: "Chơi 20 phút",
      detail: {
        minute_per_turn: 3,
        total_minute_per_player: 20,
        turn_around_steps: -1,
        turn_around_time_plus: -1,
      },
      published: true,
    },
    {
      name: "Chơi siêu tốc",
      detail: {
        minute_per_turn: 10,
        total_minute_per_player: 10,
        turn_around_steps: 2,
        turn_around_time_plus: 5,
      },
      published: true,
    },
  ]);
};
