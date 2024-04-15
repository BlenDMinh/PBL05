/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");
const { NUM_RAN_USERS } = require("./01-users");

const NUM_FRIENDS = NUM_RAN_USERS / 2;

const tableName = "friendships";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  let friendships = Array.from({ length: NUM_FRIENDS }, (_, index) => ({
    user_id: Math.floor(
      Math.random() * (NUM_RAN_USERS / 2 - 1) + NUM_RAN_USERS / 2 + 1
    ),
    friend_id: NUM_RAN_USERS + 3,
    created_at: fakerVI.date.past(),
  }));
  await knex(tableName)
    .insert(friendships)
    .onConflict(["user_id", "friend_id"])
    .ignore();

  friendships = Array.from({ length: NUM_FRIENDS }, (_, index) => ({
    user_id: Math.floor(
      Math.random() * (NUM_RAN_USERS / 2 - 1) + NUM_RAN_USERS / 2 + 1
    ),
    friend_id: NUM_RAN_USERS + 2,
    created_at: fakerVI.date.past(),
  }));
  await knex(tableName)
    .insert(friendships)
    .onConflict(["user_id", "friend_id"])
    .ignore();
  await knex(tableName).insert({
    user_id: NUM_RAN_USERS + 3,
    friend_id: NUM_RAN_USERS + 2,
    created_at: fakerVI.date.past(),
  });
};
