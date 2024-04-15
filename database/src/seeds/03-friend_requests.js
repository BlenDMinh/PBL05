/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");
const { NUM_RAN_USERS } = require("./01-users");

const NUM_REQUEST = NUM_RAN_USERS / 2;

const tableName = "friend_requests";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  let requests = Array.from({ length: NUM_REQUEST }, (_, index) => ({
    sender_id: Math.floor(Math.random() * (NUM_RAN_USERS / 2 - 1) + 1),
    receiver_id: NUM_RAN_USERS + 3,
    created_at: fakerVI.date.past(),
  }));
  await knex(tableName)
    .insert(requests)
    .onConflict(["sender_id", "receiver_id"])
    .ignore();

  requests = Array.from({ length: NUM_REQUEST }, (_, index) => ({
    sender_id: Math.floor(Math.random() * (NUM_RAN_USERS / 2 - 1) + 1),
    receiver_id: NUM_RAN_USERS + 2,
    created_at: fakerVI.date.past(),
  }));
  await knex(tableName)
    .insert(requests)
    .onConflict(["sender_id", "receiver_id"])
    .ignore();
};
