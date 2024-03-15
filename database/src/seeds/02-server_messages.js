/**
 * @param { import("knex").Knex } knex
 * @returns { Promise<void> }
 */
const { fakerVI } = require("@faker-js/faker");
const { NUM_USERS } = require("./01-users");

const NUM_SERVER_MESSAGES = 100;

const tableName = "server_messages";
exports.seed = async function (knex) {
  // Deletes ALL existing entries
  await knex(tableName).del();
  const serverMessages = Array.from(
    { length: NUM_SERVER_MESSAGES },
    (_, index) => ({
      content: fakerVI.lorem.sentence(),
      sender_id: Math.floor(Math.random() * (NUM_USERS - 1) + 1),
      sended_at: fakerVI.date.past(),
    })
  );
  await knex(tableName).insert(serverMessages);
};
