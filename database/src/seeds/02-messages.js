/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");
const { NUM_RAN_USERS } = require("./01-users");

const NUM_MESSAGES = 10;

const tableName = "messages";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  let messages = Array.from({ length: NUM_MESSAGES }, (_, index) => ({
    content: fakerVI.lorem.sentence(),
    sender_id: Math.floor(Math.random() * (NUM_RAN_USERS - 1) + 1),
    receiver_id: NUM_RAN_USERS + 3,
    sended_at: fakerVI.date.past(),
  }));
  await knex(tableName).insert(messages);

  messages = Array.from({ length: NUM_MESSAGES }, (_, index) => ({
    content: fakerVI.lorem.sentence(),
    sender_id: Math.floor(Math.random() * (NUM_RAN_USERS - 1) + 1),
    receiver_id: NUM_RAN_USERS + 2,
    sended_at: fakerVI.date.past(),
  }));
  await knex(tableName).insert(messages);

  messages = Array.from({ length: NUM_MESSAGES }, (_, index) => ({
    content: fakerVI.lorem.sentence(),
    sender_id: NUM_RAN_USERS + 2,
    receiver_id: NUM_RAN_USERS + 3,
    sended_at: fakerVI.date.past(),
  }));
  await knex(tableName).insert(messages);
};
