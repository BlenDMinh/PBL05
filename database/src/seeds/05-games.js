/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");
const { NUM_RAN_USERS, NUM_USERS } = require("./01-users");

const tableName = "games";

const GAME_STATUS = {
  WHITE_WIN: 2,
  BLACK_WIN: 3,
  DRAW: 4,
};
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  let games = Array.from({ length: NUM_RAN_USERS }, (_, index) => {
    const value = Object.values(GAME_STATUS);
    const player1_id = Math.floor(Math.random() * NUM_RAN_USERS) + 1;
    let player2_id;
    do {
      player2_id = Math.floor(Math.random() * NUM_RAN_USERS) + 1;
    } while (player2_id === player1_id);
    return {
      ruleset_id: 1,
      player1_id,
      player2_id,
      created_at: fakerVI.date.past(),
      status: value[Math.floor(Math.random() * value.length)],
    };
  });
  await knex(tableName).insert(games);

  games = Array.from({ length: NUM_RAN_USERS }, (_, index) => {
    const value = Object.values(GAME_STATUS);
    const player1_id = NUM_RAN_USERS + 2;
    let player2_id;
    do {
      player2_id = Math.floor(Math.random() * NUM_RAN_USERS) + 1;
    } while (player2_id === player1_id);
    return {
      ruleset_id: 1,
      player1_id,
      player2_id,
      created_at: fakerVI.date.past(),
      status: value[Math.floor(Math.random() * value.length)],
    };
  });
  await knex(tableName).insert(games);

  games = Array.from({ length: NUM_RAN_USERS }, (_, index) => {
    const value = Object.values(GAME_STATUS);
    const player1_id = NUM_RAN_USERS + 3;
    let player2_id;
    do {
      player2_id = Math.floor(Math.random() * NUM_RAN_USERS) + 1;
    } while (player2_id === player1_id);
    return {
      ruleset_id: 1,
      player1_id,
      player2_id,
      created_at: fakerVI.date.past(),
      status: value[Math.floor(Math.random() * value.length)],
    };
  });
  await knex(tableName).insert(games);
};
